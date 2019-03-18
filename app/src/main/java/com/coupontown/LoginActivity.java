package com.coupontown;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.tasks.OnFailureListener;
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
    FirebaseAuth.AuthStateListener authStateListener;


    private static final String SKIPLOGIN_TAG = "SKIP";
    private static final String G_TAG = "GoogleLogin";
    private static final String E_TAG = "EmailLogin";

    Intent intentHome;

    FirebaseStorage storage;
    StorageReference storageReference;


    TextView resetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFirebaseAuth();

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

    }

    @Override
    public void onClick(View view) {

        if (view == skipLogin) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("skipLogin", true);
            startActivity(intent);
            finish();
        }

        if (view == googleLogin) {
            Log.d(G_TAG, "Sign Method using Gmail Options");
            // signInUsingGoogle();
            hidekeypad();
        }

        if (view == loginbutton) {
            Log.d(E_TAG, "Sign Method using Normal Options");
            loginExistingUser();
        }

        if (view == resetPassword) {
            Log.d("Reset", "Reset the password");
            resetPassword();
            hidekeypad();
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

        email.setText("");
        password.setText("");

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
                                redirecttoHome();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify the Email, before login", Toast.LENGTH_LONG).show();
                                sendVerificationEmail();
                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                Log.getStackTraceString(e);

            }
        });
    }


    //Check User exists, if not register
    private void registeruser(final String emailStr, final String passwordStr) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("register", "onComplete: Successful registration via email and password =>" + task.isSuccessful());
                            Toast.makeText(LoginActivity.this, "User Email Created, Please check email to verify", Toast.LENGTH_LONG).show();
                            sendVerificationEmail();

                            FirebaseAuth.getInstance().signOut();

                        } else {
                            Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();

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

    private void hidekeypad() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        //  this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setupFirebaseAuth() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    if (firebaseUser.isEmailVerified()) {
                        Log.i("1.Firebase", "onAuthStateChanged: User Signed in =>" + firebaseUser.getUid());
                        Log.i("1.Firebase", "onAuthStateChanged: User Signed in Email =>" + firebaseUser.getEmail());
                        redirecttoHome();
                    }

                } else {
                    Log.i("1.Firebase", "onAuthStateChanged: User Signed out or Not Authenticated");
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void sendVerificationEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Sent Email Verification Email!", Toast.LENGTH_LONG).show();
                        Log.i("2.Firebase", "sendVerificationEmail: onComplete =>" + task.isSuccessful());
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed::Sent Email Verification Email!", Toast.LENGTH_LONG).show();
                        Log.i("2.Firebase", "failed sendVerificationEmail: onComplete =>" + task.isSuccessful());
                    }
                }
            });
        }
    }


    private void redirecttoHome() {
        Intent loginIntent = new Intent(this, HomeActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
}




