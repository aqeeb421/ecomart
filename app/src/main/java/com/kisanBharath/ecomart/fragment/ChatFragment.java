package com.kisanBharath.ecomart.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.adapter.UserAdapter;
import com.kisanBharath.ecomart.models.Chatlist;
import com.kisanBharath.ecomart.models.User;
import com.kisanBharath.ecomart.notification.Token;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    public ChatFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "ChatActivity";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<Chatlist> usersList;
    private SearchView search_user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        search_user = rootView.findViewById(R.id.search_user);
        recyclerView = rootView.findViewById(R.id.usersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        search_user.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    userAdapter.getFilter().filter(newText);
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        return rootView;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fUser.getUid()).setValue(token1);
    }

    private void chatList() {

        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList) {
                        assert user != null;
                        if (user.getUser_id() != null){
                            if (user.getUser_id().equals(chatlist.getId())) {
                                mUsers.add(user);
                            }
                        }

                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}