package com.coupontown;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coupontown.model.ItemOfferModel;
import com.like.LikeButton;

public class MoreDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_name, tv_cat, tv_item_desc, status;
    private ImageView circleImageView;
    private ImageView sharebutton;
    private LikeButton likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(this);

        ItemOfferModel itemdetails = getIntent().getParcelableExtra("itemdetails");

        Log.i("itemdetails", itemdetails.toString());


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = findViewById(R.id.ll);

        View itemrow = inflater.inflate(R.layout.item_row, linearLayout, true);

        View contentmoredetails  = inflater.inflate(R.layout.content_more_details, linearLayout, true);

        TextView name = itemrow.findViewById(R.id.tv_name);
        name.setText(itemdetails.getName());

        TextView category = itemrow.findViewById(R.id.tv_cat);
        category.setText(itemdetails.getCategory());

        TextView textView = findViewById(R.id.moredetails_desc);



        textView.setText(itemdetails.getMoreDetails().getDetail_desc());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        this.onBackPressed();
    }
}
