package com.coupontown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.coupontown.model.UserProfile;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    FirebaseStorage storage;
    StorageReference storageReference;


    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    Uri selectedImageUri;


    ImageView imageView;
    Button buttonUpload;

    EditText name;
    EditText email;
    EditText number;

    private static final int SELECT_PICTURE = 100;
    Animation myAnim;

    ImageView user_picture;

    UserProfile userProfileObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();


        userProfileObject = intent.getParcelableExtra("profile");


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        imageView = findViewById(R.id.imageViewProfile);
        imageView.setOnClickListener(this);


        myAnim = AnimationUtils.loadAnimation(this, R.anim.profile_button);
        buttonUpload = findViewById(R.id.uploadProfile);
        buttonUpload.setOnClickListener(this);

        name = findViewById(R.id.profile_name);
        name.setOnClickListener(this);
        email = findViewById(R.id.profile_email);
        email.setOnClickListener(this);
        number = findViewById(R.id.profile_phone);
        number.setOnClickListener(this);


        //
        //Need to write the logic for read & update

        if (userProfileObject.getFull_name() != null) {
            name.setText(userProfileObject.getFull_name());
        }


        if (userProfileObject.getPhonenumber() != null) {
            number.setText(userProfileObject.getPhonenumber());
        }

        email.setText(userProfileObject.getEmail());


        user_picture = findViewById(R.id.imageViewProfile);
        //Check the pic exists in firebase storage
        Picasso.with(ProfileActivity.this).load(userProfileObject.getProfile_pic()).resize(1200, 450)
                .centerInside().into(imageView);


        //


    }

    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);


    }


    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.imageViewProfile) {

            openImageChooser();

        }
        if (id == R.id.uploadProfile) {
            view.startAnimation(myAnim);
            if (selectedImageUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);

                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                StorageMetadata storageMetadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .setContentLanguage("en").build();

                ref.putFile(selectedImageUri, storageMetadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("URL", "onSuccess: uri= " + uri.toString());
                                progressDialog.dismiss();
                                updateDBprofile(uri);
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


            } else {
                updateDBprofile(null);
                Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();


                //Now update the UserProfile object to the key
                redirecttoHome();
            }

        }
    }

    private void redirecttoHome() {
        Intent loginIntent = new Intent(this, HomeActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateDBprofile(Uri uri) {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("profile").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (uri != null) {
            mFirebaseDatabase.child("picurlstr").setValue(uri.toString());
            Toast.makeText(ProfileActivity.this, "Image Saved Succussfully in Firebase DB", Toast.LENGTH_SHORT).show();
        }

        if (!name.getText().toString().equalsIgnoreCase("")) {
            mFirebaseDatabase.child("full_name").setValue(name.getText().toString());
        }

        if (!number.getText().toString().equalsIgnoreCase("")) {
            mFirebaseDatabase.child("phonenumber").setValue(number.getText().toString());
        }
        //Now update the UserProfile object to the key
        redirecttoHome();

    }

    private void sendVerificationEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Sent Email Verification Email!", Toast.LENGTH_LONG).show();
                        Log.i("2.Firebase", "sendVerificationEmail: onComplete =>" + task.isSuccessful());
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed::Sent Email Verification Email!", Toast.LENGTH_LONG).show();
                        Log.i("2.Firebase", "failed sendVerificationEmail: onComplete =>" + task.isSuccessful());
                    }
                }
            });
        }
    }


}
