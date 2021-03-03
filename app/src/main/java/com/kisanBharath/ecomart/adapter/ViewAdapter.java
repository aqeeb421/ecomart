package com.kisanBharath.ecomart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;
import com.kisanBharath.ecomart.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] imagesUrls;
    PhotoView imageView;


    public ViewAdapter(Context context, String[] imagesUrls) {
        this.context = context;
        this.imagesUrls = imagesUrls;
    }

    @Override
    public int getCount() {
        return imagesUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_image, null);
        imageView = view.findViewById(R.id.post_view_image);
        ProgressBar image_progress = view.findViewById(R.id.image_progress);
        Picasso.get().load(imagesUrls[position]).fit().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                image_progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
