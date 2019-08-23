package com.coupontown;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.coupontown.adapter.HomeRecyclerViewAdapter;
import com.coupontown.adapter.ViewPagerSlideAdapter;
import com.coupontown.model.ItemOfferModel;
import com.coupontown.model.UserProfile;
import com.coupontown.utility.DataGenerator;
import com.coupontown.utility.FirebaseUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter recyclerViewAdapter;
    private List<ItemOfferModel> arrayList = new ArrayList<>();

    private UserProfile userProfile;

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

        userProfile = FirebaseUtil.getUserProfile();


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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO add Progress bar , as Profile Builder
        checkAuthState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            FirebaseUtil.signout();
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
            Intent profileIntent = new Intent(this, ProfileActivity.class);

            profileIntent.putExtra("userProfile", userProfile);
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
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("userProfile", FirebaseUtil.getUserProfile());
        startActivity(profileIntent);
    }

    private void checkAuthState() {
        final FirebaseUser firebaseUser = FirebaseUtil.firebaseUser();
        Log.i(FIREBASE, "Checking Authentication State");

        if (firebaseUser == null) {
            redirecttoLogin();
        } else if (!firebaseUser.isEmailVerified() && firebaseUser.getEmail() != null) {
            FirebaseUtil.sendVerificationEmail(this);
            redirecttoLogin();
        } else {
            Log.i(FIREBASE, "Verified User=>" + firebaseUser.getEmail());
            checkuserexists();
            //
        }
    }

    private void checkuserexists() {

        Log.i(FIREBASE, "Reading the data from Database");
        final FirebaseUser firebaseUser = FirebaseUtil.firebaseUser();

        //This logic should change now
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userProfile").child(firebaseUser.getUid());


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Log.i(FIREBASE, dataSnapshot.getKey());
                    Log.i(FIREBASE, dataSnapshot.toString());
                    userProfile = FirebaseUtil.addnewuser(firebaseUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.getStackTraceString(databaseError.toException());
            }
        });
    }


}
