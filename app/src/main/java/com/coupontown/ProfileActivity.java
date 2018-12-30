package com.coupontown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_GET_SINGLE_FILE = 001;
    private int GALLERY = 1, CAMERA = 2;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageView = findViewById(R.id.imageView2);
        imageView.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (v == imageView) {


            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY);

        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


}
