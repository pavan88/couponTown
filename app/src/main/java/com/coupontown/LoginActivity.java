package com.coupontown;

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
import com.facebook.login.widget.LoginButton;
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

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    //Email
    private Button loginbutton;
    private EditText email;
    private EditText password;


    FirebaseAuth firebaseAuth;

    private static final String FB_TAG = "FacebookLogin";
    private static final String G_TAG = "GoogleLogin";
    private static final String E_TAG = "EmailLogin";

    FirebaseStorage storage;
    StorageReference storageReference;


    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    //Google
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton g_loginButton;

    //Facebook
    //Facebook Login
    private LoginButton fb_loginButton;


    Intent intent;

    TextView resetPassword;

    TextView skipLogin;

    public boolean isLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar
                = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();

        if (isLoggedIn()) {
            Log.i("login", "User Already Logged in");
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("password")) {
                fetchProfileInfo(firebaseUser.getEmail());
            }


            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("google.com")) {
                //Re initiating the gmail login process
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().requestProfile()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                signInUsingGoogle();
            }
            if (firebaseUser.getProviders().get(0).equalsIgnoreCase("facebook.com")) {

//                callbackManager = CallbackManager.Factory.create();
//                fb_loginButton = findViewById(R.id.fb_loginButton);
//
//                signInUsingFacebook();
//                intent = new Intent(this, HomeActivity.class);
            }

        }


        //Email Based Login Initializations Button

        loginbutton = findViewById(R.id.buttonLogin);

        email = findViewById(R.id.email);

        password = findViewById(R.id.password);

        loginbutton.setOnClickListener(this);

        g_loginButton = findViewById(R.id.g_loginButton);
        g_loginButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Facebook Login Initializations
        // callbackManager = CallbackManager.Factory.create();
        // fb_loginButton = findViewById(R.id.fb_loginButton);
        //fb_loginButton.setOnClickListener(this);


        //Reset Password
        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(this);

        //SkipLogin
        skipLogin = findViewById(R.id.skip);
        skipLogin.setOnClickListener(this);

        intent = new Intent(this, HomeActivity.class);

    }


    @Override
    public void onClick(View view) {

        if (view == loginbutton) {
            Log.d(E_TAG, "Sign Method using Normal Options");
            loginExistingUser();
        }

        if (view == g_loginButton) {
            Log.d(G_TAG, "Sign Method using Gmail Options");
            signInUsingGoogle();
        }

//        if (view == fb_loginButton) {
//            Log.d(G_TAG, "Sign Method using Facebook Options");
//            //signInUsingFacebook();
//        }

        if (view == resetPassword) {
            Log.d("Reset", "Reset the password");
            resetPassword();
        }

        if (view == skipLogin) {
            Log.d("SkipLogin", "SkipLogin");
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra("skiplogin", Boolean.TRUE);
            startActivity(intent);
            finish();
        }


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


    /// Activity validation for Facebook and Gmail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            final UserProfile guserProfile = new UserProfile();


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult();
            guserProfile.setEmail(account.getEmail());

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Success via Gmail", Toast.LENGTH_LONG).show();
                        setuserProfile(guserProfile);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(G_TAG, "Sign in using gmail failed", task.getException());
                        signOut();
                    }

                }
            });
        } else {
            // Pass the activity result back to the Facebook SDK
            //  callbackManager.onActivityResult(requestCode, resultCode, data);

        }
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
        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Please verify the email/password", Toast.LENGTH_LONG).show();
                    task.getException();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Success via Email & Password", Toast.LENGTH_LONG).show();


                    //Fetch User Profile based on email as unique and show HomeActivity
                    fetchProfileInfo(emailStr);
                    // setuserProfile(userProfile);
                }

            }
        });
    }

    private void fetchProfileInfo(final String emailStr) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String provider = user.getProviders().get(0);
        if (provider.equalsIgnoreCase("password")) {
            //inside if if not google login
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
            //  mFirebaseDatabase = mFirebaseInstance.getReference("profile");


            System.out.println(mFirebaseDatabase.toString());
            System.out.println(mFirebaseDatabase.getPath());
            mFirebaseDatabase.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    System.out.println(dataSnapshot.getChildren().iterator().next().getValue().getClass());
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    System.out.println(dataSnapshot.getValue().getClass());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = postSnapshot.getValue(UserProfile.class);
                        if (userProfile.getEmail().equalsIgnoreCase(emailStr)) {
                            intent.putExtra("profile", userProfile);
                            Log.i(G_TAG, "*************Profile********");
                            Log.i(G_TAG, userProfile.toString());
                            Log.i(G_TAG, "**********End***********");

                            startActivity(intent);
                            finish();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    //Check User exists, if not register
    private void registeruser(final String emailStr, final String passwordStr) {
        firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            task.getException();
                        } else {
                            Toast.makeText(LoginActivity.this, "User Email Created", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        startActivity(getIntent());
        finish();
    }


    //***********GMAIL LOGIN PROCESS ************//
    private void signInUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //***********Facebook LOGIN PROCESS ************//

//    private void signInUsingFacebook() {
//        fb_loginButton.setReadPermissions("email", "public_profile");
//
//        fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e(FB_TAG, "Error" + error.toString());
//            }
//        });
//    }

//    private void handleFacebookAccessToken(AccessToken token) {
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            goMainScreenViaFB();
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(FB_TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed via facebook",
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                        // ...
//                    }
//                });
//    }

//    private void goMainScreenViaFB() {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken == null) return;
//        GraphRequest data_request = GraphRequest.newMeRequest(
//                accessToken,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject json_object,
//                            GraphResponse response) {
//                        UserProfile userProfile = new UserProfile();
//                        try {
//                            userProfile.setEmail(json_object.getString("email"));
//                            setuserProfile(userProfile);
//                        } catch (JSONException e) {
//                            Toast.makeText(LoginActivity.this, "Authentication failed via facebook",
//                                    Toast.LENGTH_LONG).show();
//                            signOut();
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//        Bundle permission_param = new Bundle();
//        // permission_param.putString("fields", "id,name,email,picture.width(500).height(500)");
//        permission_param.putString("fields", "id,name,email,picture.width(200).height(200)");
//        data_request.setParameters(permission_param);
//        data_request.executeAsync();
//    }

    private void setuserProfile(UserProfile userProfile) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Check user isnew
        //is Email Verified -> NO then new


        //Log.i(G_TAG, user.getEmail());
        if (user != null) {
            //Check the user is normal login , if normal logged in then pull the profile info from the firebasedb and storage
            userProfile.setPhonenumber(user.getPhoneNumber());
            userProfile.setProvider(user.getProviders().get(0));
            userProfile.setFull_name(user.getDisplayName());
            userProfile.setProfile_pic(user.getPhotoUrl());
            userProfile.setUid(user.getUid());
            userProfile.isVerified(user.isEmailVerified());

            intent.putExtra("profile", userProfile);

            Log.i(G_TAG, userProfile.toString());

            startActivity(intent);
            finish();
        }
    }

    private void signOut() {
        firebaseAuth.getInstance().signOut();
        firebaseAuth.signOut();
    }

    public final static boolean isValidEmail(String emailID) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailID).matches();

    }
}
