package com.coupontown;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.coupontown.model.FacebookResponse;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.LockOnGetVariable;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class DashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FacebookResponse facebookResponse = new FacebookResponse();

    JSONObject response, profile_pic_data, profile_pic_url;
    TextView user_name, user_email;
    ImageView user_picture;
    NavigationView navigation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");
        setNavigationHeader();    // call setNavigationHeader Method.<br />
        if (jsondata != null) {
            Log.i("success", jsondata);

            setUserProfile(jsondata);  // call setUserProfile Method.</p>

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            AlertDialog.Builder alertbox = new AlertDialog.Builder(DashBoardActivity.this);
            alertbox.setIcon(R.drawable.com_facebook_button_icon);
            alertbox.setTitle("You Want To Logout ?");
            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // finish used for destroyed activity
                    LoginManager.getInstance().logOut();
                    finish();
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
        // super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {
            LoginManager.getInstance().logOut();
            Intent loginIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
        Set Navigation header by using Layout Inflater.
     */
    public void setNavigationHeader() {
        navigation_view = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_dash_board, null);
        navigation_view.addHeaderView(header);
        user_name = (TextView) header.findViewById(R.id.username);
        user_picture = (ImageView) header.findViewById(R.id.imageView);
        user_email = (TextView) header.findViewById(R.id.email);
    }

    /*
       Set User Profile Information in Navigation Bar.
     */
    public void setUserProfile(String jsondata) {
        try {
            response = new JSONObject(jsondata);
            user_email.setText(response.get("email").toString());
            user_name.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url"))
                    .into(user_picture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
