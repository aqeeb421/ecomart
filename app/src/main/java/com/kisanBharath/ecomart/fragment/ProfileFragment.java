package com.kisanBharath.ecomart.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.activity.ContactActivity;
import com.kisanBharath.ecomart.activity.UserInfoActivity;
import com.kisanBharath.ecomart.adapter.PostListAdapter;
import com.kisanBharath.ecomart.models.Post;
import com.kisanBharath.ecomart.models.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;


public class ProfileFragment extends Fragment {

    User user;
    CircleImageView profile_image;
    TextView userName, phoneNumber, email;
    private ArrayList<User> mUsers;
    private ArrayList<Post> mPosts;
    private RecyclerView mRecyclerView;
    private PostListAdapter mPostListAdapter;
    final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();
    private static final int NUM_GRID_COLUMNS = 2;
    String userId;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = new User();
        profile_image = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.userName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        email = view.findViewById(R.id.email);
        mRecyclerView = view.findViewById(R.id.myads_recyclerView);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        view.findViewById(R.id.edit_fab).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.buttonMenu).setOnClickListener(v -> {
           // FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                try {

                    Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.profile).into(profile_image);
                    userName.setText(user.getUserName());
                    phoneNumber.setText(user.getPhoneNumber());
                    email.setText(user.getEmail());


                } catch (NullPointerException e) {
                    userName.setText("user Name");
                    phoneNumber.setText("phone Number");
                    email.setText("email");

                    startActivity(new Intent(getActivity(), UserInfoActivity.class));

                    Toast.makeText(getActivity(), "Please Update your profile to continue...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled: " + error.getMessage());

            }
        });

        getOwnAdds(userId);

        removeAdd(userId);

        return view;
    }


    private void getOwnAdds(String userId) {
        mPosts = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        showProgressDialog();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");
        Log.d(TAG, "init: " + storageReference);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mPosts.removeAll(mPosts);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    try {
                        if (post.getUserId().equals(userId)) {
                            if (snapshot.getChildrenCount() == 11) {
                                mPosts.add(post);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: ", e.getCause());
                    }
                }

                SetUpPostsLists();
                mPostListAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //  Log.d(TAG, "onCancelled: " + error.getMessage());
                //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void removeAdd(String userId) {
        //delete data
        Query oldItems = FirebaseDatabase.getInstance().getReference("posts").orderByChild("date");
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    //itemSnapshot.getRef().removeValue();
                    Post post = itemSnapshot.getValue(Post.class);
                    try {
                        if (post.getUserId().equals(userId)) {
                            String postDateString = post.getDate();
                            String currentDateString = getDateTime();
                            Date postDate = new Date(), currentDate = new Date();
                            try {
                                postDate = new SimpleDateFormat("dd MMM yyyy").parse(postDateString);
                                currentDate = new SimpleDateFormat("dd MMM yyyy").parse(currentDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            long diffInMillies = Math.abs(currentDate.getTime() - postDate.getTime());
                            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);


                            if (diff > 60) {
                                itemSnapshot.getRef().removeValue();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                if (Build.VERSION.SDK_INT >= 26) {
                                    ft.setReorderingAllowed(false);
                                }
                                ft.detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
                                Log.d(TAG, "onDataChange: ad deleted" + postId);
                            } else {

                            }

                        }
                    } catch (NullPointerException exception) {
                        Log.e(TAG, "onDataChange: " + exception.getMessage(), exception.fillInStackTrace());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private String getDateTime() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void SetUpPostsLists() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPostListAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mPostListAdapter);
    }
}