package com.coupontown.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.coupontown.ProfileActivity;
import com.coupontown.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerSlideAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private int pos = 0;

    private List<Uri> images;

    public ViewPagerSlideAdapter(Context context, List<Uri> images) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.imageslide_layout, container, false);
        ImageView imageView = view.findViewById(R.id.imageviewslider);

        Glide.with(context).load(images.get(pos)).into(imageView);
    //    Picasso.with(context).load(images.get(pos)).into(imageView);
        if (pos >= images.size() - 1)
            pos = 0;
        else
            ++pos;

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
