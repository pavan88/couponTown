package com.coupontown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.coupontown.utility.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    EditText fullname;
    EditText number;
    EditText email;
    Button button;
    de.hdodenhof.circleimageview.CircleImageView circleImageView;

    Uri selectedImageUri;

    private static final int SELECT_PICTURE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);


        FirebaseUser firebaseUser = FirebaseUtil.firebaseUser();
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullname = findViewById(R.id.input_name);
        fullname.setText(firebaseUser.getDisplayName());

        email = findViewById(R.id.input_email);
        email.setText((firebaseUser.getEmail()));

        number = findViewById(R.id.input_phonenumber);
        circleImageView = findViewById(R.id.profile_image_view);
        button = findViewById(R.id.btn_update);

        circleImageView.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {


        if (v == button) {
            String displayname = fullname.getText().toString();
            String mobile = number.getText().toString();
            String emailstr = email.getText().toString();
            FirebaseUtil.updateuser(displayname, emailstr, mobile, selectedImageUri);

        } else if (v == circleImageView) {

            openImageChooser();

        } else {

            this.onBackPressed();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        System.out.println("Here in OnActivityResult");
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
