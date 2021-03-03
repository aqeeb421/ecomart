package com.kisanBharath.ecomart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.activity.MessageActivity;
import com.kisanBharath.ecomart.models.Chat;
import com.kisanBharath.ecomart.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kisanBharath.ecomart.fragment.ChatFragment.TAG;

public class UserAdapter extends RecyclerView.Adapter<com.kisanBharath.ecomart.adapter.UserAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<User> mUsers;
    private List<User> mUsersFull;
    private boolean ischat;

    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        mUsersFull = new ArrayList<>(mUsers);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUserName());
        try {
            Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.profile).into(holder.profile_image);

        } catch (NullPointerException e) {
            Log.d(TAG, "onBindViewHolder: " + e.getMessage());
            Log.d("TAG", "onDataChange: Upload image");
            Picasso.get().load(R.drawable.profile).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(user.getUser_id(), holder.last_msg);

        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mUsersFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User user : mUsersFull) {
                    if (user.getUserName().toLowerCase().contains(filterPattern)) {

                        String s = user.getUserName();
                        Log.d(TAG, "performFiltering: " + s);

                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUsers.clear();
            mUsers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public CircleImageView profile_image;
        private final CircleImageView img_on;
        private final CircleImageView img_off;
        private final TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.userName);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}

