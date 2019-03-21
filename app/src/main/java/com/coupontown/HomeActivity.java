package com.coupontown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.coupontown.api.AndroidVersion;
import com.coupontown.api.JSONResponse;
import com.coupontown.api.RequestInterface;
import com.coupontown.model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    TextView user_name, user_email;
    ImageView user_picture;
    NavigationView navigation_view;

    //Search API

    public static final String BASE_URL = "https://api.learn2crack.com";
    private RecyclerView mRecyclerView;
    private ArrayList<AndroidVersion> mArrayList;
    private DataAdapter mAdapter;

    //FireBase
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;


    //FAB changes
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;


    UserProfile userProfile;

    Boolean skiplogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFirebaseAuth();


        //FAB Button
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);


        initViews();
        loadJSON();
        skiplogin = getIntent().getBooleanExtra("skipLogin", false);
        if (!skiplogin)
            setNavigationHeader(false);

        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /*
     Set User Profile Information in Navigation Bar.
   */
    public void setUserProfile(UserProfile userProfile) {
        try {
            user_email.setText(userProfile.getEmail());
            user_name.setText(userProfile.getFull_name());
            String url = userProfile.getPicurlstr();
            // Picasso.with(this).load(url).resize(150, 150).into(user_picture);
            Picasso.with(this).load(url).resize(600, 120).centerInside().into(user_picture);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.search) {
            SearchView search = (SearchView) item.getActionView();
            Log.i("searchView", search.toString());
            search(search);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews() {
       /* mRecyclerView = findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);*/

        mRecyclerView = findViewById(R.id.card_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DataAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    private void loadJSON() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getJSON();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {

                JSONResponse jsonResponse = response.body();
                mArrayList = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
                mAdapter = new DataAdapter(mArrayList);
                mRecyclerView.setAdapter(mAdapter);
                Log.i("json", mArrayList.toString());

            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.profile) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra("profile", userProfile);

            startActivity(profileIntent);

        } else if (id == R.id.home) {


        } else if (id == R.id.rate_item) {
            //Now read sms from ur mobile
            Toast.makeText(this, "Rate Item Clicked", Toast.LENGTH_LONG).show();
            Intent profileIntent = new Intent(this, RateActivity.class);
            startActivity(profileIntent);

        } else if (id == R.id.help) {

        } else if (id == R.id.feedback) {

            Toast.makeText(this, "Clicked on Feedback", Toast.LENGTH_SHORT).show();
            sendFeedback();
        } else if (id == R.id.logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    private void sendFeedback() {
        final Intent _Intent = new Intent(android.content.Intent.ACTION_SEND);
        _Intent.setType("text/html");
        _Intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_feedback_email)});
        _Intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.mail_feedback_subject));
        _Intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.mail_feedback_message));
        startActivity(Intent.createChooser(_Intent, getString(R.string.title_send_feedback)));
    }


    /*
        Set Navigation header by using Layout Inflater.
     */
    public void setNavigationHeader(boolean skipLogin) {
        navigation_view = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header, null);
        navigation_view.addHeaderView(header);
        user_name = (TextView) header.findViewById(R.id.username);
        user_picture = (ImageView) header.findViewById(R.id.imageView);
        user_email = (TextView) header.findViewById(R.id.email);
        Menu menu = navigation_view.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.logout);
        MenuItem profileItem = menu.findItem(R.id.profile);
        if (skipLogin) {
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
            Button button = header.findViewById(R.id.guestlogin);
            button.setVisibility(View.VISIBLE);
            button.setText("LOGIN");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    redirecttoLogin();
                }
            });
        }
        //else it will logged in using gmail or normal registrations


    }

    private void logout() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        Log.i("provider", firebaseAuth.getCurrentUser().getProviderData().get(0).toString());
        //  UserProfile userProfile = intent.getParcelableExtra("profile");
        alertbox.setIcon(R.drawable.com_facebook_button_icon);
        alertbox.setTitle("You Want To Logout ?");

        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // finish used for destroyed activity
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(HomeActivity.this, "User Session Closed", Toast.LENGTH_LONG).show();
                redirecttoLogin();
            }
        });

        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Nothing will be happened when clicked on no button
                // of Dialog
            }
        });
        alertbox.show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:

                Log.d("Raj", "Fab 1");
                break;
            case R.id.fab2:

                Log.d("Raj", "Fab 2");
                break;
        }
    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
    }

    private void setupFirebaseAuth() {
        Log.i("1.firebase", "setupFirebaseAuth in HomeActivity");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    if (firebaseUser.isEmailVerified()) {
                        Log.i("HomeActivityFirebase", "onAuthStateChanged: User Signed in =>" + firebaseUser.getUid());
                        Log.i("HomeActivityFirebase", "onAuthStateChanged: User Signed in Email =>" + firebaseUser.getEmail());

                    } else {
                        FirebaseAuth.getInstance().signOut();
                        redirecttoLogin();
                    }

                } else {
                    Log.i("HomeActivityFirebase", "onAuthStateChanged: User Signed out or Not Authenticated");
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "Calling onStop Method");
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStart() {
        Log.i("onStart", "Calling onStart Method");
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthState();

    }

    private void checkAuthState() {

        Log.i("firebase", "Checking Authentication State");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (skiplogin) {
            //This will execute if Skip Login is selected
            setNavigationHeader(true);
            return;
        } else if (firebaseUser == null) {
            redirecttoLogin();
        } else if (!firebaseUser.isEmailVerified() && firebaseUser.getEmail() != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("HomeActivityFirebase", "User need to verify email " + firebaseUser.getEmail());
                        Toast.makeText(HomeActivity.this, "Please verify email to login", Toast.LENGTH_LONG).show();
                        redirecttoLogin();
                    }
                }
            });


        } else {
            checkuserexists();
            //

        }
    }


    private void redirecttoLogin() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
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
        userProfile.setVerified(firebaseUser.isEmailVerified());

        return userProfile;
    }


    private void checkuserexists() {
        Log.i("dbref", "Reading the data from Database");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("profile")
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.i("dbkey", snapshot.getKey());
                        String email = (String) snapshot.child("email").getValue();
                        String uid = (String) snapshot.child("uid").getValue();

                        if (email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                && uid.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            //User existing in DB. So just login
                            //Construct the userprofile from DB.
                            userProfile = snapshot.getValue(UserProfile.class);
                            Log.i("existinguser", userProfile.toString());
                            setUserProfile(userProfile);


                        } else {

                            createnewuserinfoindb();

                        }
                    }
                } else {
                    createnewuserinfoindb();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void createnewuserinfoindb() {
        Log.i("dbregister", "Registering the user for the firsttime");
        //create an entry in db for the first time
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("profile")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());
        userProfile = mapFirebaseuser(FirebaseAuth.getInstance().getCurrentUser());
        databaseReference.setValue(userProfile);
        Log.i("FirebaseDatabase", "****** Profile Information saved Successfully ******");
        Log.i("FirebaseDatabase", "************** D O N E **************");
        setUserProfile(userProfile);
    }

}
