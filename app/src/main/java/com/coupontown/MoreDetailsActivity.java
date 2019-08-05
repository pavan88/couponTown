package com.coupontown;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.coupontown.model.ItemOfferModel;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

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

        final ItemOfferModel itemdetails = getIntent().getParcelableExtra("itemdetails");

        Log.i("itemdetails", itemdetails.toString());


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = findViewById(R.id.ll);

        View itemrow = inflater.inflate(R.layout.item_row, linearLayout, true);

        View contentmoredetails = inflater.inflate(R.layout.content_more_details, linearLayout, true);

        TextView name = itemrow.findViewById(R.id.tv_name);
        name.setText(itemdetails.getName());

        TextView category = itemrow.findViewById(R.id.tv_cat);
        category.setText(itemdetails.getCategory());

        CircleImageView circleImageView = itemrow.findViewById(R.id.item_id);
        circleImageView.setImageURI(itemdetails.getItem_img());

        Picasso.with(this).load(itemdetails.getItem_img())
                .resize(75, 75)
                .into(circleImageView);

        TextView desc = itemrow.findViewById(R.id.tv_item_desc);
        desc.setVisibility(View.INVISIBLE);

        TextView textView = findViewById(R.id.moredetails_desc);
        textView.setText(itemdetails.getMoreDetails().getDetail_desc());

        Button button = contentmoredetails.findViewById(R.id.button);
        PackageManager packageManager = this.getPackageManager();
        final Intent intent = packageManager.getLaunchIntentForPackage(itemdetails.getMoreDetails().getAppurl());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent==null){
                   Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + "net.one97.paytm"));
                    MoreDetailsActivity.this.startActivity(intent);
                }else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MoreDetailsActivity.this.startActivity(intent);
                }
            }
        });
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
