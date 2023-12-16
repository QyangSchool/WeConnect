package com.example.weconnect;

import static com.example.weconnect.chat.receiverPic;
import static com.example.weconnect.chat.senderPic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messageAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messageAdapterArrayList) {
        this.context = context;
        this.messageAdapterArrayList = messageAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message messages = messageAdapterArrayList.get(position);
        if (holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.textmsg.setText(messages.getMessage());
            Picasso.get().load(senderPic).into(viewHolder.circleImageView);
        }else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.textmsg.setText(messages.getMessage());
            Picasso.get().load(receiverPic).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Message messages = messageAdapterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())){
            return ITEM_SEND;
        }else {
            return ITEM_RECEIVE;
        }
    }

    // Create a holder class for the sender set(profile pic + text block)
    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textmsg;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.sidePicS);
            textmsg = itemView.findViewById(R.id.senderTextBlock);
        }
    }

    // Create a holder class for the receiver set(profile pic + text block)
    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textmsg;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.sidePicR);
            textmsg = itemView.findViewById(R.id.receiverTextBlock);
        }
    }
}