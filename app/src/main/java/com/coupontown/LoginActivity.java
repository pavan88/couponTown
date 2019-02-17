package com.coupontown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.coupontown.model.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //SkipLogin views
    TextView skipLogin;

    //Google
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_Result = 1001;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleLogin;


    //Firebase Auth
    FirebaseAuth firebaseAuth;


    //Firebase DB
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    private static final String SKIPLOGIN_TAG = "SKIP";
    private static final String G_TAG = "GoogleLogin";
    private static final String E_TAG = "EmailLogin";

    //Profile
    UserProfile userProfile;

    Intent intentHome;

    FirebaseStorage storage;
    StorageReference storageReference;


    boolean emailExists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        //SkipLogin
        skipLogin = findViewById(R.id.skipLoginTv);
        skipLogin.setOnClickListener(this);

        //Google
        googleLogin = findViewById(R.id.g_loginButton);
        googleLogin.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (isLoggedIn()) {
            FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("google.com")) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Restoring google account ....");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();//displays the progress bar
                signInUsingGoogle();
                progressDialog.dismiss();

            }
            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("password")) {
                //Autologin Logic
            }
        }

        intentHome = new Intent(this, HomeActivity.class);
    }

    @Override
    public void onClick(View view) {

        if (view == skipLogin) {


            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("SkipLogin");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();//displays the progress bar

            Log.d(SKIPLOGIN_TAG, "Skip Login");
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("skiplogin", Boolean.TRUE);
            startActivity(intent);
            finish();
        }

        if (view == googleLogin) {
            Log.d(G_TAG, "Sign Method using Gmail Options");
            signInUsingGoogle();
        }

    }

    //Google login flow
    //***********GMAIL LOGIN PROCESS ************//
    private void signInUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /// Activity validation for Facebook and Gmail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final GoogleSignInAccount account = task.getResult();

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Success via Gmail", Toast.LENGTH_LONG).show();
                        //setuserProfile(guserProfile);
                        //TODO need to get proper profile details and load the profile data in profile actvity

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        userProfile = mapFirebaseuser(firebaseUser);
                        userProfile.setEmail(account.getEmail());

                        //Set the userprofile to intent and diplay in profile activtity

                        setuserProfile();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(G_TAG, "Sign in using gmail failed", task.getException());
                        signOut();
                    }

                }
            });

        }
    }

    private boolean isLoggedIn() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        return firebaseUser != null;
    }


    private void setuserProfile() {

        //Logic to save in save in database if emailid isnull

        //save data in firebase for the first time

        try {
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference("profile");

            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String email = (String) data.child("email").getValue();
                        if (email != null) {
                            if (email.equalsIgnoreCase(userProfile.getEmail())) {
                                emailExists = Boolean.TRUE;
                                break;
                            }
                        }

                    }

                    if (!emailExists) {
                        mFirebaseDatabase.push().setValue(userProfile);
                        Log.i(G_TAG, "******DONE*********");
                    }

                    intentHome.putExtra("profile", userProfile);

                    Log.i(G_TAG, userProfile.toString());

                    startActivity(intentHome);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private UserProfile mapFirebaseuser(FirebaseUser firebaseUser) {

        UserProfile userProfile = new UserProfile();
        userProfile.setFull_name(firebaseUser.getDisplayName());
        userProfile.setPhonenumber(firebaseUser.getPhoneNumber());
        userProfile.setEmail(firebaseUser.getEmail());
        userProfile.setPicurlstr(firebaseUser.getPhotoUrl().toString());
        userProfile.setUid(firebaseUser.getUid());
        return userProfile;
    }


    private void signOut() {
        firebaseAuth.getInstance().signOut();
        firebaseAuth.signOut();
    }

    private byte[] getbytesfromURI() {
        Uri data = firebaseAuth.getCurrentUser().getPhotoUrl();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(data.getPath()));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bbytes = baos.toByteArray();
        return bbytes;
    }

}
