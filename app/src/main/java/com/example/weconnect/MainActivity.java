package com.example.weconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView messages_view;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imageHome;
    ImageView imageSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://weconnect-ef221-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference reference = database.getReference().child("user");

        usersArrayList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Home Image functions
        imageHome = findViewById(R.id.homeImage);


        // Setting Image functions
        imageSetting = findViewById(R.id.settingImage);


        // Users list
        messages_view = findViewById(R.id.messages_view);
        messages_view.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserAdapter(MainActivity.this, usersArrayList);
        messages_view.setAdapter(adapter);



        if (auth.getCurrentUser() == null){
            Intent intent =  new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }

    }
}