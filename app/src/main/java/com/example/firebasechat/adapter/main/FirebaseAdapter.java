package com.example.firebasechat.adapter.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasechat.model.main.ChatMessage;
import com.example.firebasechat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseAdapter extends FirebaseRecyclerAdapter<ChatMessage, FirebaseAdapter.MessageViewHolder> {

    private Context mContext;
    private Activity mActivity;

    public FirebaseAdapter(Context context, Activity activity, FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
        this.mContext = context;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {
        holder.messageTextView.setText(model.getText());
        holder.messengerTextView.setText(model.getName());

        if(model.getPhotoUrl() != null) {
            holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_account_circle_black_24dp));
        } else {
            Glide.with(mActivity)
                    .load(model.getPhotoUrl())
                    .into(holder.messengerImageView);
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = v.findViewById(R.id.messageTextView);
            messageImageView = v.findViewById(R.id.messageImageView);
            messengerTextView = v.findViewById(R.id.messengerTextView);
            messengerImageView = v.findViewById(R.id.messengerImageView);
        }
    }
}
