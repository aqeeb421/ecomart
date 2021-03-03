package com.kisanBharath.ecomart.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.activity.MainActivity;
import com.kisanBharath.ecomart.activity.MessageActivity;
import com.kisanBharath.ecomart.adapter.ViewAdapter;
import com.kisanBharath.ecomart.models.Post;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class ViewPostFragment extends Fragment {


    public ViewPostFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "ViewPostFragment";

    //vars
    private String mPostId, phonenumber;
    private Post mPost;

    //widgets
    private ImageView back_img;
    private TextView text_title, text_category, text_price, text_address, text_description, post_id, text_date, txt_add_details, text_UserName;
    private Button contact_btn, chat_btn, remove_ad_btn;
    private LinearLayout same_ad_info_layout, btn_details_layout;
    private ViewPager view_pager;
    private DotsIndicator dotsIndicator;
    ViewAdapter viewAdapter;

    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        mPostId = (String) getArguments().getString("mPostId");

        Log.d(TAG, "onCreate: " + mPostId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        //post_view_image = view.findViewById(R.id.post_view_image);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        text_title = view.findViewById(R.id.text_title);
        text_category = view.findViewById(R.id.text_category);
        text_price = view.findViewById(R.id.text_price);
        text_address = view.findViewById(R.id.text_address);
        text_description = view.findViewById(R.id.text_description);
        post_id = view.findViewById(R.id.post_id);
        text_date = view.findViewById(R.id.text_date);
        contact_btn = view.findViewById(R.id.contact_btn);
        chat_btn = view.findViewById(R.id.chat_btn);
        txt_add_details = view.findViewById(R.id.txt_add_details);
        text_UserName = view.findViewById(R.id.text_UserName1);
        remove_ad_btn = view.findViewById(R.id.remove_ad_btn);
        same_ad_info_layout = view.findViewById(R.id.same_ad_info_layout);
        btn_details_layout = view.findViewById(R.id.btn_details);
        back_img = view.findViewById(R.id.back_img);

        //viewPager
        view_pager = view.findViewById(R.id.view_pager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);

        back_img.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

        // hideSoftKeyBoad();

        return view;
    }

    private void init() {
        getPostInfo();
    }

    private void getPostInfo() {

        Log.d(TAG, "getPostInfo: getting post info");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.node_posts))
                .orderByKey()
                .equalTo(mPostId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot singleSnapshot = snapshot.getChildren().iterator().next();
                if (singleSnapshot != null) {
                    mPost = singleSnapshot.getValue(Post.class);
                    assert mPost != null;
                    Log.d(TAG, "onDataChange: found the post" + mPost.getTitle());

                    text_title.setText(mPost.getTitle());
                    text_description.setText(mPost.getDescription());
                    text_category.setText(mPost.getMainCategories());
                    text_address.setText(mPost.getLocation());
                    text_price.setText("\u20B9 " + mPost.getPrice());
                    post_id.setText(mPost.getPostId());
                    text_date.setText(mPost.getDate());
                    DatabaseReference UserName = FirebaseDatabase.getInstance().getReference().child("Users").child(mPost.getUserId()).child("userName");
                    UserName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.getValue().toString();
                            text_UserName.setText(username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // toolbar_textView.setText(mPost.getTitle());

                    if (mPost.getUserId().equals(mUser.getUid())) {
                        btn_details_layout.setVisibility(View.GONE);
                        same_ad_info_layout.setVisibility(View.VISIBLE);
                    }

                    remove_ad_btn.setOnClickListener(v -> {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("posts").child(mPostId);

                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        ad.setCancelable(true);
                        ad.setTitle(R.string.delepost);
                        ad.setMessage(R.string.diaolog_delete);
                        ad.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                Toast.makeText(getContext(), R.string.post_deleted, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.notext, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();


                    });


                    if (mPost.getPhoneNumber() == null || mPost.getPhoneNumber().equalsIgnoreCase("")) {
                        phonenumber = "+91 9353587021";
                    } else
                        phonenumber = mPost.getPhoneNumber();

                    HashMap<String, String> hashMap = mPost.getImages();

                    Collection<String> s = hashMap.values();

                    ArrayList<String> ss = new ArrayList<>();

                    for (String elem : s) {
                        System.out.println("elem = " + elem);
                        ss.add(elem);
                    }

                    String ImgArray[] = new String[ss.size()];
                    for (int j = 0; j < ss.size(); j++) {
                        ImgArray[j] = ss.get(j);
                    }

                   /* Picasso.get().load(ss.get(0)).fit().error(R.drawable.add_photo).into(post_view_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            image_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });*/

                    viewAdapter = new ViewAdapter(getContext(), ImgArray);
                    view_pager.setAdapter(viewAdapter);
                    dotsIndicator.setViewPager(view_pager);

                    contact_btn.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phonenumber));
                        startActivity(intent);
                    });

                    chat_btn.setOnClickListener(v -> {

                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("uid", mPost.getUserId());
                        intent.putExtra("postId", mPost.getPostId());
                        startActivity(intent);

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}