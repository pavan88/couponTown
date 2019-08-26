package com.coupontown;

import android.content.Intent;
import android.net.Uri;
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
import com.coupontown.model.Favourite;
import com.coupontown.model.ItemOfferModel;
import com.coupontown.model.UserProfile;
import com.coupontown.utility.DataGenerator;
import com.coupontown.utility.FirebaseUtil;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.*;
import java.util.concurrent.ExecutionException;

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
    List<Uri> viewslist;

    ViewPagerSlideAdapter viewPagerSlideAdapter;
    ViewPager viewPager;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        userProfile = FirebaseUtil.getUserProfile();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        TextView textView = findViewById(R.id.toolbartitle);
        textView.setText(R.string.app_name);


        final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("viewpager");
        viewslist = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ListResult listResult = Tasks.await(imagesRef.listAll());
                    for (StorageReference child : listResult.getItems()) {
                        Uri uri = Tasks.await(child.getDownloadUrl());
                        viewslist.add(uri);
                    }
                    viewPager = findViewById(R.id.viewpager);
                    viewPagerSlideAdapter = new ViewPagerSlideAdapter(MainActivity.this, viewslist);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viewPager.setAdapter(viewPagerSlideAdapter);

        circleImageView = findViewById(R.id.logoXmarks);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirecttoProfile();
            }
        });


        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        populateData();
        initAdapter();
        if (recyclerViewAdapter != null) {
            runAnimationAgain();
        }


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
        //VIew Page Image Slider
        //getWindow().getDecorView().findViewById(R.id.logoXmarks).invalidate();
        if (userProfile != null) {
            Picasso.with(this).load(userProfile.getPicurlstr()).into(circleImageView);
        } else {
            Picasso.with(this).load(DataGenerator.android_image_urls[0])
                    .into(circleImageView);
        }


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
        String uid = FirebaseUtil.firebaseUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("favorites").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Map> favorites = (List<Map>) dataSnapshot.getValue();
                List<ItemOfferModel> itemOfferModels = new ArrayList<>();
                Gson gson = new Gson();
                for(Map itemOfferModelMap: favorites){
                    JsonElement jsonElement = gson.toJsonTree(itemOfferModelMap);
                    ItemOfferModel itemOfferModel = gson.fromJson(jsonElement, ItemOfferModel.class);
                    itemOfferModel.setFav(true);
                    itemOfferModels.add(itemOfferModel);
                }

                if (favorites == null || favorites.size() == 0) {
                    recyclerViewAdapter = new HomeRecyclerViewAdapter(MainActivity.this, arrayList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {


                    //  Favourite favourite  =new Favourite()
                    Set<ItemOfferModel> set = new LinkedHashSet<>(itemOfferModels);
                    set.addAll(arrayList);
                    List<ItemOfferModel> combinedList = new ArrayList<>(set);
                    System.out.println("Final List:=>" + combinedList);

                    recyclerViewAdapter = new HomeRecyclerViewAdapter(MainActivity.this, combinedList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
                // recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //  recyclerViewAdapter = new HomeRecyclerViewAdapter(this, arrayList);
        //recyclerView.setAdapter(recyclerViewAdapter);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userProfile").child(firebaseUser.getUid());


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
