package com.coupontown;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;
import com.coupontown.adapter.HomeRecyclerViewAdapter;
import com.coupontown.adapter.ViewPagerSlideAdapter;
import com.coupontown.model.ItemOfferModel;
import com.coupontown.utility.DataGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int[] animationList = {R.anim.layout_animation_up_to_down,
            R.anim.layout_animation_right_to_left,
            R.anim.layout_animation_down_to_up,
            R.anim.layout_animation_left_to_right};


    private static final String FIREBASE = MainActivity.class.getName() + ":Firebase";

    //FireBase
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;


    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter recyclerViewAdapter;
    private List<ItemOfferModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        TextView textView = findViewById(R.id.toolbartitle);
        textView.setText(R.string.app_name);

        CircleImageView circleImageView = findViewById(R.id.logoXmarks);

        Picasso.with(this).load(DataGenerator.android_image_urls[0])
                .into(circleImageView);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirecttoProfile();
            }
        });


        //VIew Page Image Slider
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerSlideAdapter viewPagerSlideAdapter = new ViewPagerSlideAdapter(this);
        viewPager.setAdapter(viewPagerSlideAdapter);


        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        populateData();
        initAdapter();
        runAnimationAgain();


        //DrawerLayout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Left Navigation View
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthState();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            firebaseAuth.signOut();
            redirecttoLogin();
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
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            redirecttoProfile();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateData() {
        arrayList = DataGenerator.generateData();
    }

    private void initAdapter() {
        recyclerViewAdapter = new HomeRecyclerViewAdapter(this, arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void runAnimationAgain() {
        Random r = new Random();
        int randomNumber = r.nextInt(animationList.length);

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, animationList[randomNumber]);
        recyclerView.setLayoutAnimation(controller);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void redirecttoLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void redirecttoProfile() {
        Intent loginIntent = new Intent(this, ProfileActivity.class);
        startActivity(loginIntent);
    }

    private void checkAuthState() {

        Log.i(FIREBASE, "Checking Authentication State");
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


       if (firebaseUser == null) {
            redirecttoLogin();
        } else if (!firebaseUser.isEmailVerified() && firebaseUser.getEmail() != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(FIREBASE, "User need to verify email=>" + firebaseUser.getEmail());
                        Toast.makeText(MainActivity.this, "Please verify email to login", Toast.LENGTH_LONG).show();
                        redirecttoLogin();
                    }
                }
            });
        } else {
            checkuserexists();
            //
        }
    }

    private void checkuserexists() {
        Log.i(FIREBASE, "Reading the data from Database");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    }


}
