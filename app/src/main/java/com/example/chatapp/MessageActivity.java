package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.databinding.ActivityMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;

    Intent intent;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //recyclerView = findViewById(R.id.recycler_view);
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);


        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = binding.textSend.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }else{
                    Toast.makeText(MessageActivity.this, "You can’t send empty message", Toast.LENGTH_SHORT).show();
                }
                binding.textSend.setText("");
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                binding.username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    binding.profileImage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(binding.profileImage);
                }

                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender,String receiver,String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);

    }


    private void readMessage(final String myid,final String userid,final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid) )||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) ){
                        mchat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                binding.recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}