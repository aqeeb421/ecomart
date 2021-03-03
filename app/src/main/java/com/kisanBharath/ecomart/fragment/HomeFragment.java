package com.kisanBharath.ecomart.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.adapter.PostListAdapter;
import com.kisanBharath.ecomart.models.Post;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.kisanBharath.ecomart.activity.MainActivity.ITEMS_PER_AD;


public class HomeFragment extends Fragment implements View.OnClickListener {
    public HomeFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "SearchFragment";

    String userId;
    private ArrayList<Post> mPosts;
    private RecyclerView mRecyclerView;
    private PostListAdapter mPostListAdapter;
    private TextView tv_filter;
    private SearchView search_view;
    ProgressDialog progressDialog;
    String filterText;
    final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();
    private LinearLayout vegetable_image, fruits_image, flower_image, grains_image, more_image;
    int lastPosition;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        vegetable_image = view.findViewById(R.id.vegetable_image);
        fruits_image = view.findViewById(R.id.fruits_image);
        flower_image = view.findViewById(R.id.flower_image);
        grains_image = view.findViewById(R.id.grains_image);
        more_image = view.findViewById(R.id.more_image);
        tv_filter = view.findViewById(R.id.tv_filter);
        search_view = view.findViewById(R.id.search_view);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        init();
        getAdds(userId);
        getBannerAds(userId);
        return view;
    }

    private void getBannerAds(String userId) {
        for (int i = 0; i < mPosts.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(getResources().getString(R.string.banner_add_unitid));
            //mPosts.add(adView);
        }
    }

    private void init() {
        more_image.setOnClickListener(this);
        tv_filter.setOnClickListener(this);
        vegetable_image.setOnClickListener(this);
        flower_image.setOnClickListener(this);
        fruits_image.setOnClickListener(this);
        grains_image.setOnClickListener(this);


        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPostListAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    mPostListAdapter.getFilter().filter(newText);
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private void getAdds(String userId) {

        mPosts = new ArrayList<>();

        final Query databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        showProgressDialog();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");
        Log.d(TAG, "init: " + storageReference);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.removeAll(mPosts);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    tv_filter.setVisibility(View.GONE);
                    try {
                        if (!post.getUserId().equals(userId)) {
                            if (snapshot.getChildrenCount() == 11) {

                                mPosts.add(post);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: ", e.getCause());
                        snapshot.getRef().removeValue();
                    }

                }

                sortDate();
                int count = mPosts.size();

                SetUpPostsLists();
                mPostListAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled: " + error.getMessage());
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void getFilterData(String userId, String filterText) {


        mPosts = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("posts").orderByChild("mainCategories").equalTo(filterText);
        showProgressDialog();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");
        Log.d(TAG, "init: " + storageReference);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.removeAll(mPosts);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    tv_filter.setVisibility(View.VISIBLE);
                    if (!post.getUserId().equals(userId)) {
                        if (snapshot.getChildrenCount() == 11) {
                            mPosts.add(post);
                        }

                    }

                }


                int count = mPosts.size();
                sortDate();
                SetUpPostsLists();
                mPostListAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_image:
                showRadioButtonDialog();
                break;
            case R.id.tv_filter:
                getAdds(userId);
                break;
            case R.id.vegetable_image:
                filterText = "ತರಕಾರಿಗಳು";
                getFilterData(userId, filterText);
                break;
            case R.id.fruits_image:
                filterText = "ಹಣ್ಣುಗಳು";
                getFilterData(userId, filterText);
                break;
            case R.id.flower_image:
                filterText = "ಹೂಗಳು";
                getFilterData(userId, filterText);
                break;
            case R.id.grains_image:
                filterText = "ಧಾನ್ಯ ಮತ್ತು ಮಸಾಲೆಗಳು";
                getFilterData(userId, filterText);
                break;
        }
    }


    private void sortDate() {
        Collections.sort(mPosts, new Comparator<Post>() {
            DateFormat f = new SimpleDateFormat("dd MMM yyyy HH:mm");

            @Override
            public int compare(Post lhs, Post rhs) {
                try {
                    Log.d(TAG, "compare: " + rhs.getDate() + "..." + lhs.getDate());
                    return f.parse(rhs.getDate()).compareTo(f.parse(lhs.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    private void SetUpPostsLists() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPostListAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mPostListAdapter);

        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            lastPosition = sharedPreferences.getInt("lastPosition", 0);
            mRecyclerView.scrollToPosition(lastPosition);
        } catch (Exception e) {
            Log.d(TAG, "SetUpPostsLists: " + e.getMessage());
            //startActivity(new Intent(getActivity(), MainActivity.class));
        }

      /*  mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = linearLayoutManager.findFirstVisibleItemPosition();
            }
        });*/


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

    private void showRadioButtonDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        List<String> stringList = new ArrayList<>();  // here is list
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                stringList.add("ನರ್ಸರಿ ಸಸ್ಯಗಳು");
            }
            if (i == 1) {
                stringList.add("ಅಕ್ಕಿ");
            }
            if (i == 2) {
                stringList.add("ಗೋಧಿ");
            }
            if (i == 3) {
                stringList.add("ರಾಗಿ");
            }
            if (i == 4) {
                stringList.add("ತೈಲ");
            }
            if (i == 5) {
                stringList.add("ಇತರೆ");
            }

        }
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        for (int i = 0; i < stringList.size(); i++) {
            RadioButton rb = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rb.setTextSize(16);
            rb.setPadding(4, 4, 4, 4);
            rg.addView(rb);
        }
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            int childCount = group.getChildCount();
            for (int x = 0; x < childCount; x++) {
                RadioButton btn = (RadioButton) group.getChildAt(x);
                if (btn.getId() == checkedId) {
                    Log.e("selected RadioButton->", btn.getText().toString());

                    Toast.makeText(getActivity(), btn.getText(), Toast.LENGTH_SHORT).show();

                    getFilterData(userId, btn.getText().toString().trim());

                    dialog.dismiss();

                }
            }
        });

        dialog.show();

    }


}