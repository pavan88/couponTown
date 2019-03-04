package com.coupontown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Email
    private Button loginbutton;
    private EditText email;
    private EditText password;


    //SkipLogin views
    TextView skipLogin;

    //Google
    private static final int RC_SIGN_IN = 9001;

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


    TextView resetPassword;


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

        //Email
        loginbutton = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginbutton.setOnClickListener(this);

        //Reset Password
        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(this);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        intentHome = new Intent(this, HomeActivity.class);

        if (isLoggedIn()) {
            FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("google.com")) {
                signInUsingGoogle();
            }
            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("password")) {
                //Autologin Logic
            }
        }


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

        if (view == loginbutton) {
            Log.d(E_TAG, "Sign Method using Normal Options");
            loginExistingUser();
        }

        if (view == resetPassword) {
            Log.d("Reset", "Reset the password");
            resetPassword();
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
                        //setuserProfile(guserProfile);
                        //TODO need to get proper profile details and load the profile data in profile actvity

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (!firebaseUser.isEmailVerified()) {
                            Log.i(G_TAG, "Sending Email Verification");
                            Exception exception = firebaseUser.sendEmailVerification().getException();
                            exception.printStackTrace();
                        } else {
                            userProfile = mapFirebaseuser(firebaseUser);
                            userProfile.setEmail(account.getEmail());

                            //Set the userprofile to intent and diplay in profile activtity

                            setuserProfile();
                        }


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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Google sign...");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Toast.makeText(LoginActivity.this, "Login Success via Gmail", Toast.LENGTH_LONG).show();

        try {
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("profile");

            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String key = data.getKey();
                        Log.i("dbkey", key);
                        String email = (String) data.child("email").getValue();
                        String uid = (String) data.child("uid").getValue();

                        if (email.equalsIgnoreCase(userProfile.getEmail()) && uid.equalsIgnoreCase(userProfile.getUid())) {
                            emailExists = Boolean.TRUE;
                            break;
                        }

                    }

                    if (!emailExists) {
                        mFirebaseDatabase.push().setValue(userProfile);
                        Log.i(G_TAG, "******DONE*********");
                    }

                    intentHome.putExtra("profile", userProfile);

                    Log.i(G_TAG, userProfile.toString());
                    progressDialog.dismiss();
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
        if (firebaseUser.getPhotoUrl() != null) {
            userProfile.setPicurlstr(firebaseUser.getPhotoUrl().toString());
        }
        userProfile.setProvider(firebaseUser.getProviders().get(0));
        userProfile.setLastLogin(new Date());
        userProfile.setUid(firebaseUser.getUid());

        return userProfile;
    }


    private void signOut() {
        firebaseAuth.getInstance().signOut();
        firebaseAuth.signOut();
    }

    private void loginExistingUser() {
        final String emailStr = email.getText().toString().trim();
        final String passwordStr = password.getText().toString().trim();


        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Enter email address!");
            Toast.makeText(LoginActivity.this, "Enter email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            password.setError("Enter password!");
            Toast.makeText(LoginActivity.this, "Enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(emailStr)) {
            Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
            return;

        }

        //1. Check email exists
        firebaseAuth.fetchSignInMethodsForEmail(emailStr).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean emailexists;
                emailexists = task.getResult().getSignInMethods().isEmpty();
                if (emailexists) {
                    registeruser(emailStr, passwordStr);
                } else {
                    //User Exists , So sign in
                    signin(emailStr, passwordStr);
                }
            }
        });
    }


    //User Existing. So login with valid credentails.
    private void signin(final String emailStr, final String passwordStr) {
        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Please verify the email/password", Toast.LENGTH_LONG).show();
                            task.getException();
                        } else {
                            if (task.getResult().getUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Login Success via Email & Password", Toast.LENGTH_LONG).show();
                                userProfile = mapFirebaseuser(task.getResult().getUser());
                                userProfile.setEmail(emailStr);
                                setuserProfile();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify the Email, before login", Toast.LENGTH_LONG).show();

                            }

                        }

                    }
                });
    }


    //Check User exists, if not register
    private void registeruser(final String emailStr, final String passwordStr) {
        firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "User Email Created, Please check email to verify", Toast.LENGTH_LONG).show();
                            task.getResult().getUser().sendEmailVerification();

                        } else {
                            Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            Exception exception = task.getException();
                            exception.printStackTrace();

                        }
                    }
                });
    }


    private final static boolean isValidEmail(String emailID) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailID).matches();

    }


    private void resetPassword() {
        final String emailStr = email.getText().toString().trim();

        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Enter email address to reset password");
            Toast.makeText(LoginActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(emailStr)) {
            Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
            return;

        }

        firebaseAuth.sendPasswordResetEmail(emailStr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                            startActivity(getIntent());
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send, reset email!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
