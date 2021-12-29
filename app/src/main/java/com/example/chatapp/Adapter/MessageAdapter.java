package com.example.chatapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chat;

import com.example.chatapp.R;

import com.example.chatapp.databinding.ChatItemLeftBinding;
import com.example.chatapp.databinding.ChatItemRightBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mcontext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mcontext = mcontext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //ChatItemRightBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mcontext), R.layout.chat_item_right, parent, false);


        switch (viewType) {
            case MSG_TYPE_RIGHT:
                ChatItemRightBinding binding = ChatItemRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ViewHolder1(binding);
            case MSG_TYPE_LEFT:
                ChatItemLeftBinding binding2 = ChatItemLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ViewHolder2(binding2);
        }
        ChatItemRightBinding binding = ChatItemRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder1(binding);

        /*
        //View view;

        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new HomeViewHolder(view);
        */

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        Log.d("TAG", "onDataChange: "+chat.getMessage()+chat.getSender()+chat.getReceiver());
        //holder.binding.showMessage.setText(chat.getMessage());
        if(holder.getItemViewType()== MSG_TYPE_RIGHT){
            MessageAdapter.ViewHolder1 me = (MessageAdapter.ViewHolder1) holder ;
            me.binding.showMessage.setText(chat.getMessage());

            if(imageurl.equals("default")){
                me.binding.profileImage.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mcontext).load(imageurl).into(me.binding.profileImage);
            }
        }else
        {
            MessageAdapter.ViewHolder2 receiver = (MessageAdapter.ViewHolder2) holder ;
            receiver.binding.receiveMessage.setText(chat.getMessage());
            if(imageurl.equals("default")){
                receiver.binding.profileImage.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mcontext).load(imageurl).into(receiver.binding.profileImage);
            }
        }


    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }


    private static class ViewHolder1 extends RecyclerView.ViewHolder {
        ChatItemRightBinding binding;

        public ViewHolder1(@NonNull ChatItemRightBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private static class ViewHolder2 extends RecyclerView.ViewHolder {
        ChatItemLeftBinding binding;

        public ViewHolder2(@NonNull ChatItemLeftBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    /*
    public class HomeViewHolder extends RecyclerView.ViewHolder {
        ChatItemRightBinding binding;

        public HomeViewHolder(@NonNull ChatItemRightBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
        }

    }*/

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
