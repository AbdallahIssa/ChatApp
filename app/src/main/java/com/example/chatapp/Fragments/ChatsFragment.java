package com.example.chatapp.Fragments;

import  android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;


import com.example.chatapp.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatsFragment extends Fragment {
    //ActivityMessageBinding binding;

    //private ResultProfileBinding binding;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<String> userList;
    private FragmentChatsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View view = inflater.inflate(R.layout.fragment_chats, container, false);

         binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_chats, container, false);
         initViews();
        return binding.getRoot();

    }

    private void initViews() {

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new java.util.concurrent.CopyOnWriteArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert firebaseUser != null;

                    assert chat != null;
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        userList.add(chat.getReceiver());
                    }

                    if(chat.getReceiver().equals(firebaseUser.getUid())){
                        userList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readChats() {
        mUsers = new java.util.concurrent.CopyOnWriteArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    //display 1 user from chats
                    for(String id : userList){
                        assert user != null;
                        if(user.getId().equals(id)){
                            if(mUsers.size() != 0){
                                for(User user1 : mUsers){
                                    if(!user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                    }
                                }
                            }else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}