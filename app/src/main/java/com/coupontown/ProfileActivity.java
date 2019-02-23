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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("profile");


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


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    Log.i("dbkey", key);
                    UserProfile userProfile = postSnapshot.getValue(UserProfile.class);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (userProfile != null && firebaseUser.getUid().equalsIgnoreCase(userProfile.getUid())) {
                        name.setText(userProfile.getFull_name());
                        email.setText(userProfile.getEmail());
                        number.setText(userProfile.getPhonenumber());
                        user_picture = findViewById(R.id.imageViewProfile);
                        //Check the pic exists in firebase storage
                        Picasso.with(ProfileActivity.this).load(firebaseUser.getPhotoUrl()).resize(1200, 450)
                                .centerInside().into(imageView);
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SELECT_PICTURE:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            //  Show your own message here
                        } else {
                            showSettingsAlert();
                        }
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

                StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                ref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Profile Image uploaded Succussfully", Toast.LENGTH_SHORT).show();
                        progressDialog.setTitle("Updating the profile");
                        progressDialog.show();
                        //Write the logic for read and update

                      /*  UserProfile userProfile = new UserProfile();
                        userProfile.setEmail(email.getText().toString());
                        userProfile.setPhonenumber(number.getText().toString());
                        userProfile.setFull_name(name.getText().toString());


                        String uploadiD = mFirebaseDatabase.getKey();
                        mFirebaseDatabase.child(uploadiD).setValue(userProfile);*/
                        Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();


                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed to upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ProfileActivity.this, "Updating the Profile only", Toast.LENGTH_SHORT).show();
                //Retrieve the Current user profile from DB
                UserProfile userProfile = new UserProfile();
                userProfile.setEmail("PAVANCS0451@GMAIL.com");
                userProfile.setPhonenumber("9986339732");
                userProfile.setFull_name("PAVAN1");

                //Now update the UserProfile object to the key
                //Logic todo
            }

        }
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


    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAppSettings(ProfileActivity.this);
                    }
                });
        alertDialog.show();
    }

    public static void openAppSettings(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

}
