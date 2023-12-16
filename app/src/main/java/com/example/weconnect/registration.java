package com.example.weconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {

    // defines the variables
    TextView toLogin;
    EditText name;
    EditText email;
    EditText password;
    EditText password1;
    Button signUpBtn;
    CircleImageView profilePic;
    FirebaseAuth auth;
    Uri imageURI;
    String imageUri;
    String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        database = FirebaseDatabase.getInstance("https://weconnect-ef221-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // define the variable with the xml
        toLogin = findViewById(R.id.textviewLogin);
        name = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        password1 = findViewById(R.id.editTextPassword1);
        profilePic = findViewById(R.id.profile_image);
        signUpBtn = findViewById(R.id.signUpBtn);

        // when user already have an account, click on Login will take user to Login page
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registration.this, login.class);
                startActivity(intent);
                finish();
            }
        });

        // when user click on signUpBtn, do the following to create new account
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String Password1 = password1.getText().toString();
                String status = "Hi, I'm Online";

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Password1)){ // when any of the required information is empty
                    Toast.makeText(registration.this, "Please Enter the Information", Toast.LENGTH_SHORT).show(); // display message asking user to enter details
                }else if (!Email.matches(pattern)){ // when email pattern is invalid
                    email.setError("Enter A Valid Email Pattern"); // show error
                }else if (Password.length()<4){ // password is set at least 4 characters
                    password.setError("Password need at least 4 characters");
                }else if (!Password.equals(Password1)){ // when confirm password is not matching with password
                    password.setError("Password not match"); // show error
                }else {
                    // when everything is good, start authentication
                    auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);

                                // check if user upload their own profile pic
                                if (imageURI!= null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        // save the user details in real time database
                                                        imageUri = uri.toString();
                                                        Users users = new Users(id, Name, Email, Password, Password1, imageUri);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Intent intent =new Intent(registration.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(registration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{ // if user don't use their own profile pic, provide default pic
                                    imageUri = "https://firebasestorage.googleapis.com/v0/b/weconnect-ef221.appspot.com/o/man%20(1).png?alt=media&token=13b07ce0-45b9-44ad-823e-5aab094ea5c0&_gl=1*1nnev31*_ga*MTQ1NDk5MzU3Mi4xNjk2MjQzNzkw*_ga_CW55HF8NVT*MTY5NzQ2ODgwMi41LjEuMTY5NzQ2ODk4Mi40MS4wLjA.";
                                    Users users = new Users(id, Name, Email, Password, imageUri, status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent =new Intent(registration.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(registration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // when user click on the profile picture to add the picture
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null){
                imageURI = data.getData();
                profilePic.setImageURI(imageURI);
            }
        }
    }
}