package com.coupontown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.coupontown.model.UserProfile;
import com.coupontown.utility.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    EditText fullname;
    EditText number;
    EditText email;
    Button button;
    de.hdodenhof.circleimageview.CircleImageView circleImageView;

    Uri selectedImageUri;

    private static final int SELECT_PICTURE = 100;

    FirebaseStorage storage;
    StorageReference storageReference;


    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("userProfile").child(firebaseUser.getUid());


        Intent intent = getIntent();
        userProfile = intent.getParcelableExtra("userProfile");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        fullname = findViewById(R.id.input_name);
        fullname.setText(userProfile.getFull_name());

        email = findViewById(R.id.input_email);
        email.setText((firebaseUser.getEmail()));

        number = findViewById(R.id.input_phonenumber);
        number.setText(userProfile.getPhonenumber());

        circleImageView = findViewById(R.id.profile_image_view);
        //circleImageView.setImageURI(Uri.parse(userProfile.getPicurlstr()));

        Picasso.with(ProfileActivity.this).load(userProfile.getPicurlstr()).resize(1200, 450)
                .centerInside().into(circleImageView);

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

            String name = fullname.getText().toString();
            String phone = number.getText().toString();
            UserProfile.UserProfileBuilder userProfileBuilder = new UserProfile.UserProfileBuilder(email.getText().toString());
            UserProfile profile = null;

            if (!name.equalsIgnoreCase(userProfile.getFull_name())) {
                profile = userProfileBuilder.withfullname(name).build();
            }

            if (!phone.equalsIgnoreCase(userProfile.getPhonenumber())) {
                profile = userProfileBuilder.withPhoneNumber(phone).build();
            }

            if (selectedImageUri == null) {
                //User is updating only number and name
                FirebaseUtil.updateuser(profile);
                return;
            }

            if (selectedImageUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);

                progressDialog.setTitle("Uploading...");
                progressDialog.show();
               final String uid =  FirebaseAuth.getInstance().getCurrentUser().getUid();
                final StorageReference ref = storageReference.child("images/" + uid);
                StorageMetadata storageMetadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .setContentLanguage("en").build();
                // Need to optimise to handle same image uploading multiple times.
                final UserProfile finalProfile = profile;
                ref.putFile(selectedImageUri, storageMetadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("URL", "onSuccess: uri= " + uri.toString());
                                progressDialog.dismiss();
                                finalProfile.setPicurlstr(uri.toString());
                                FirebaseUtil.updateuser(finalProfile);
                                Log.i("updateiimage", databaseReference.getKey());
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
            }

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                circleImageView.setImageBitmap(bitmap);
                System.out.println("Here in OnActivityResult=" + selectedImageUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Here in OnActivityResult" + selectedImageUri);
            }
        }
    }

}
