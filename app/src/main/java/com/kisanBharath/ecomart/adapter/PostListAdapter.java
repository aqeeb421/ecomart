package com.kisanBharath.ecomart.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.activity.ViewPostActivity;
import com.kisanBharath.ecomart.fragment.ViewPostFragment;
import com.kisanBharath.ecomart.models.Post;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.kisanBharath.ecomart.activity.MainActivity.ITEMS_PER_AD;


/**
 * Created by User on 11/1/2017.
 */

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "PostListAdapter";
    private static final int NUM_GRID_COLUMNS = 3;

    private static final int ITEM_TYPE_POST = 123;
    private static final int ITEM_TYPE_BANNER_AD = 321;

    private ArrayList<Post> mPosts;
    private ArrayList<Post> mPostsFull;
    private Context mContext;


    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mPostsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Post post : mPostsFull) {
                    if (post.getLocation().toLowerCase().contains(filterPattern)) {

                        String s = post.getLocation();
                        Log.d(TAG, "performFiltering: " + s);

                        filteredList.add(post);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPosts.clear();
            mPosts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class MyPostViewHolder extends RecyclerView.ViewHolder {

        ImageView mPostImage;
        TextView text_title, text_price, text_address, text_UserName;
        CardView add_card_view;
        ProgressBar progressBar;

        public MyPostViewHolder(View itemView) {
            super(itemView);
            mPostImage = itemView.findViewById(R.id.post_image);
            text_title = itemView.findViewById(R.id.text_title);
            text_UserName = itemView.findViewById(R.id.text_UserName);
            text_price = itemView.findViewById(R.id.text_price);
            text_address = itemView.findViewById(R.id.text_address);
            add_card_view = itemView.findViewById(R.id.add_card_view);
            progressBar = itemView.findViewById(R.id.image_progress);

            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            mPostImage.setMaxHeight(imageWidth);
            mPostImage.setMaxWidth(imageWidth);
        }
    }

    //Banner Ad View Holder
    class MyAdViewHolder extends RecyclerView.ViewHolder {
        MyAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    public PostListAdapter(Context context, ArrayList<Post> posts) {
        mPosts = posts;
        mContext = context;
        mPostsFull = new ArrayList<>(mPosts);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_TYPE_BANNER_AD:
                //Inflate ad banner container
                View bannerLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_layout, parent, false);

                //Create View Holder
                MyAdViewHolder myAdViewHolder = new MyAdViewHolder(bannerLayoutView);

                return myAdViewHolder;
            case ITEM_TYPE_POST:
            default:
                //Inflate RecyclerView row
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_items, parent, false);

                //Create View Holder
                final MyPostViewHolder myCountryViewHolder = new MyPostViewHolder(view);

                return myCountryViewHolder;
        }

    }
    @Override
    public int getItemViewType(int position)
    {
        if (position == 0 || mPosts.get(position) instanceof Post)
        {
            return ITEM_TYPE_POST;
        } else
        {
            return (position % ITEMS_PER_AD == 0) ? ITEM_TYPE_BANNER_AD : ITEM_TYPE_POST;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType){
            case ITEM_TYPE_BANNER_AD:
                if (mPosts.get(position) !=null)
                {
                    /*MyAdViewHolder bannerHolder = (MyAdViewHolder) holder;
                    AdView adView = (AdView) mPosts.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.getChildCount() > 0)
                    {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null)
                    {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);*/
                }
                break;

            case ITEM_TYPE_POST:
            default:
                if (mPosts.get(position) instanceof Post)
                {
                    MyPostViewHolder holder1 = (MyPostViewHolder) holder;
                    Post post = (Post) mPosts.get(position);


                    HashMap<String, String> hashMap = mPosts.get(position).getImages();

                    try {
                        Collection<String> s = hashMap.values();

                        ArrayList<String> ss = new ArrayList<>();

                        for (String elem : s) {
                            System.out.println("elem = " + elem);
                            ss.add(elem);
                        }

                        Picasso.get().load(ss.get(0)).error(R.drawable.add_photo).into(holder1.mPostImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder1.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Picasso.get().load(R.drawable.add_photo).error(R.drawable.add_photo).into(holder1.mPostImage);
                        holder1.progressBar.setVisibility(View.GONE);
                    }

                    holder1.text_title.setText(mPosts.get(position).getTitle());

                    holder1.text_price.setText("\u20B9 " + mPosts.get(position).getPrice());
                    holder1.text_address.setText(mPosts.get(position).getLocation());

                    DatabaseReference UserName = FirebaseDatabase.getInstance().getReference().child("Users").child(mPosts.get(position).getUserId()).child("userName");
                    UserName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.getValue().toString();
                            holder1.text_UserName.setText(username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    holder1.add_card_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: selected a post");

              AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            Fragment myFragment = new ViewPostFragment();
                            Bundle args = new Bundle();
                            args.putString("mPostId", mPosts.get(position).getPostId());
                            myFragment.setArguments(args);

                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();


                            Intent intent = new Intent(mContext, ViewPostActivity.class);
                            intent.putExtra("mPostId", mPosts.get(position).getPostId());
                            mContext.startActivity(intent);


                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("lastPosition", position);
                            editor.apply();
                        }
                    });


                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


}





  /*  @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder,int position){
        HashMap<String, String> hashMap = mPosts.get(position).getImages();

        try {
            Collection<String> s = hashMap.values();

            ArrayList<String> ss = new ArrayList<>();

            for (String elem : s) {
                System.out.println("elem = " + elem);
                ss.add(elem);
            }

            Picasso.get().load(ss.get(0)).error(R.drawable.add_photo).into(holder.mPostImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "onError: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Picasso.get().load(R.drawable.add_photo).error(R.drawable.add_photo).into(holder.mPostImage);
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.text_title.setText(mPosts.get(position).getTitle());

        holder.text_price.setText("\u20B9 " + mPosts.get(position).getPrice());
        holder.text_address.setText(mPosts.get(position).getLocation());

        DatabaseReference UserName = FirebaseDatabase.getInstance().getReference().child("Users").child(mPosts.get(position).getUserId()).child("userName");
        UserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                holder.text_UserName.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.add_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selected a post");

               *//* AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new ViewPostFragment();
                Bundle args = new Bundle();
                args.putString("mPostId", mPosts.get(position).getPostId());
                myFragment.setArguments(args);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
*//*

                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("mPostId", mPosts.get(position).getPostId());
                mContext.startActivity(intent);


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("lastPosition", position);
                editor.apply();
            }
        });
    }

*/


