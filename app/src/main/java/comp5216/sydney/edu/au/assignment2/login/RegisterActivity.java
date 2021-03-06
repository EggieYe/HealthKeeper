package comp5216.sydney.edu.au.assignment2.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import comp5216.sydney.edu.au.assignment2.R;
import comp5216.sydney.edu.au.assignment2.main.MainActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private EditText Name,Email,Password,Confirm_password,Security;
    private DatabaseReference databaseReference;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger_signup);
        mAuth = FirebaseAuth.getInstance();
        //define buttons
        final Button RegisterInBtn=findViewById(R.id.btn_register_in);;
        final Button BackToSignUpBtn = findViewById(R.id.btn_back_to_sign_in);

        //get user input text
        Name=(EditText) findViewById(R.id.logger_signup_username);
        Email=(EditText) findViewById(R.id.logger_signup_email);
        Password=(EditText) findViewById(R.id._logger_signup_password);
        Confirm_password=(EditText) findViewById(R.id.logger_signup_password_confirm);
        Security=(EditText) findViewById(R.id.logger_signup_security_code);


        BackToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
        });

        RegisterInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                registerusers();
//                userlogin();
            }
        });
    }


    public void userlogin(){
        String email=Email.getText().toString().trim();
        String password=Password.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            databaseReference =  FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    public void registerusers(){

        final String username=Name.getText().toString().trim();
        final String email=Email.getText().toString().trim();
        final String password=Password.getText().toString().trim();
        final String confirm_password=Confirm_password.getText().toString().trim();
        final String security=Security.getText().toString().trim();

        if(username.isEmpty()){
            Name.setError("User name is required");
            Name.requestFocus();
            return;
        }
        if(email.isEmpty()){
            Email.setError("Email is required");
            Email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Please enter a valid email");
            Email.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Password.setError("Password is required");
            Password.requestFocus();
            return;
        }
        if(password.length()<6){
            Password.setError("Minimum password length should be 6 characters");
            Password.requestFocus();
            return;
        }
        if(confirm_password.isEmpty()){
            Confirm_password.setError("Password confirmation required");
            Confirm_password.requestFocus();
            return;
        }
        if(!confirm_password.equals(password)){
            Confirm_password.setError("Password doesn't match");
            Confirm_password.requestFocus();
            return;
        }
        if(security.isEmpty()){
            Security.setError("Security question answer is required");
            Security.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user=new User(username,email,password,confirm_password,security);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // send verification link
                                        FirebaseUser fuser = mAuth.getCurrentUser();
                                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                            }
                                        });

                                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        if ( i != null) {
                                            RegisterActivity.this.startActivity(i);
                                        }
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this,"Register Failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Register Failed",Toast.LENGTH_LONG).show();
                        }
                        // ...
                    }
                });

    }
}
