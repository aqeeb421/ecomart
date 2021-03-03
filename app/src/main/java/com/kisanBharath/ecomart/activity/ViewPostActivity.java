package com.kisanBharath.ecomart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.kisanBharath.ecomart.adapter.ViewAdapter;
import com.kisanBharath.ecomart.models.Post;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ViewPostActivity extends AppCompatActivity {

    private static final String TAG = "ViewPostFragment";

    //vars
    private String mPostId, phonenumber;
    private Post mPost;

    //widgets
    private ImageView back_img;
    private TextView text_title, text_category, text_price, text_address, text_description, post_id, text_date, txt_add_details;
    private Button contact_btn, chat_btn, remove_ad_btn;
    private LinearLayout same_ad_info_layout, btn_details_layout;
    private ViewPager view_pager;
    private DotsIndicator dotsIndicator;
    ViewAdapter viewAdapter;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mPostId = getIntent().getStringExtra("mPostId");
        Log.d(TAG, "onCreate: " + mPostId);


        text_title = findViewById(R.id.text_title);
        text_category = findViewById(R.id.text_category);
        text_price = findViewById(R.id.text_price);
        text_address = findViewById(R.id.text_address);
        text_description = findViewById(R.id.text_description);
        post_id = findViewById(R.id.post_id);
        text_date = findViewById(R.id.text_date);
        contact_btn = findViewById(R.id.contact_btn);
        chat_btn = findViewById(R.id.chat_btn);
        txt_add_details = findViewById(R.id.txt_add_details);
        remove_ad_btn = findViewById(R.id.remove_ad_btn);
        same_ad_info_layout = findViewById(R.id.same_ad_info_layout);
        btn_details_layout = findViewById(R.id.btn_details);
        back_img = findViewById(R.id.back_img);

        //viewPager
        view_pager = findViewById(R.id.view_pager);
        dotsIndicator = findViewById(R.id.dots_indicator);

        back_img.setOnClickListener(v -> {
            onBackPressed();
        });


        mUser = FirebaseAuth.getInstance().getCurrentUser();


        init();

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
                    // toolbar_textView.setText(mPost.getTitle());

                    if (mPost.getUserId().equals(mUser.getUid())) {
                        btn_details_layout.setVisibility(View.GONE);
                        same_ad_info_layout.setVisibility(View.VISIBLE);
                    }

                    remove_ad_btn.setOnClickListener(v -> {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("posts").child(mPostId);

                        AlertDialog.Builder ad = new AlertDialog.Builder(ViewPostActivity.this);
                        ad.setCancelable(true);
                        ad.setTitle(R.string.delepost);
                        ad.setMessage(R.string.diaolog_delete);
                        ad.setPositiveButton("ಸರಿ", (dialog, which) -> {
                            reference.removeValue();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.post_deleted, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }).setNegativeButton("ಇಲ್ಲ", new DialogInterface.OnClickListener() {
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

                    viewAdapter = new ViewAdapter(ViewPostActivity.this, ImgArray);
                    view_pager.setAdapter(viewAdapter);
                    dotsIndicator.setViewPager(view_pager);

                    contact_btn.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phonenumber));
                        startActivity(intent);
                    });

                    chat_btn.setOnClickListener(v -> {

                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
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