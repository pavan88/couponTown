package com.coupontown.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.coupontown.R;

public class ViewPagerSlideAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private Integer[] images = {R.mipmap.image_1, R.mipmap.image_2};

    public ViewPagerSlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.imageslide_layout, container, false);
        ImageView imageView = view.findViewById(R.id.imageviewslider);
        imageView.setImageResource(images[position]);

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
