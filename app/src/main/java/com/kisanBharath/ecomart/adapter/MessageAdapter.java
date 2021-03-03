package com.kisanBharath.ecomart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.models.Chat;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<com.kisanBharath.ecomart.adapter.MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChats;
    private String imageUrl;

    FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Chat> mChats, String imageUrl) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public com.kisanBharath.ecomart.adapter.MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new com.kisanBharath.ecomart.adapter.MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new com.kisanBharath.ecomart.adapter.MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChats.get(position);

        holder.show_message.setText(chat.getMessage());

        holder.txt_dateTime.setText(chat.getDate());

        Picasso.get().load(imageUrl).fit().placeholder(R.drawable.profile).into(holder.profile_image);

        //check for last seen
        if (position == mChats.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");

            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, txt_seen, txt_dateTime;
        public CircleImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_msg);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_dateTime = itemView.findViewById(R.id.txt_dateTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
