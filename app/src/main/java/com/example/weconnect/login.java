package com.example.weconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login extends AppCompatActivity {

    Button button;
    EditText email;
    EditText password;
    TextView toSignup;
    FirebaseAuth auth;
    // set the email pattern
    // characters before @ can be accepted are lower and upper case alphabet, number, symbol(._-)
    // characters after @ can be accepted are lower case alphabet, for example: gmail
    // characters after . can be accepted are lower case alphabet, for example: com
    String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.loginBtn);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        toSignup = findViewById(R.id.textviewSignUp);

        // when user don't have an account yet, click on Sign Up will take user to Registration to create new account
        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        // set button object to listen to the user click
        // when user click on the login button, listen to the following instructions
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(Email)){ // when user leave the email empty
                    Toast.makeText(login.this, "Please enter your email", Toast.LENGTH_SHORT).show(); // ask user to enter email
                }else if(TextUtils.isEmpty(pass)) { // when user leave the password empty
                    Toast.makeText(login.this, "Please enter your password", Toast.LENGTH_SHORT).show(); // ask user to enter password
                }else if(!Email.matches(pattern)){ // when the email is not matching the proper email format
                    email.setError("Please enter a proper email address format");
                }else if(password.length()<4){ // set the minimum password characters as 4
                    password.setError("More than 4 characters"); // if the user enter less than 4 characters, show error
                    Toast.makeText(login.this, "Password Needs To Be Longer than 4 Characters", Toast.LENGTH_SHORT).show(); // ask user to enter the password flw the pattern
                }else{
                    // when both email and password are type in properly, sign in the user's account
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // when the login is successful
                            if (task.isSuccessful()){
                                Intent intent = new Intent(login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                // if the login is unsuccessful, display the error message
                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() { // when any error happens during the login process
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // display the error message
                            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }




}