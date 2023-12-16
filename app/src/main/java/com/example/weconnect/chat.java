package com.example.weconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat extends AppCompatActivity {

    String receiverName;
    String receiverProfile;
    String receiverUid;
    String SenderUid;
    CircleImageView profile;
    TextView receivername;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    public static String senderPic;
    public static String receiverPic;
    String senderRoom;
    String receiverRoom;
    RecyclerView content;
    ArrayList<Message> messageArrayList;
    MessageAdapter messageAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.profileimgg);
        receivername = findViewById(R.id.receivername);

        content = findViewById(R.id.content_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        content.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(chat.this,messageArrayList);
        content.setAdapter(messageAdapter);

        // get receiver information
        receiverName = getIntent().getStringExtra("ReceiverName");
        receiverProfile = getIntent().getStringExtra("ReceiverProfile");
        receiverUid = getIntent().getStringExtra("ReceiverUid");

        messageArrayList = new ArrayList<>();

        Picasso.get().load(receiverProfile).into(profile);
        receivername.setText(""+ receiverName);

        SenderUid = firebaseAuth.getUid(); // get sender id
        senderRoom = SenderUid+receiverUid;
        receiverRoom = receiverUid+SenderUid;

        DatabaseReference reference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message messages = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(messages);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderPic = snapshot.child("profilepic").getValue().toString();
                receiverPic = receiverProfile;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(chat.this, "Enter your message.", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                Message messages = new Message(message, SenderUid, date.getTime());

                firebaseDatabase = FirebaseDatabase.getInstance("https://weconnect-ef221-default-rtdb.asia-southeast1.firebasedatabase.app/");
                firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseDatabase.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
    }
}