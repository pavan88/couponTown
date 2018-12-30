package com.coupontown;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

public class RateActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<String> mArrayList = new ArrayList();
    private DataAdapterSMS mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initsms();

        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"}, 101);

        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            Uri uriSMSURI = Uri.parse("content://sms/inbox");
            Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
            String sms = "";
            for (String curstr : cur.getColumnNames()) {
                Log.i("sms", curstr);

            }
            Log.i("sms", String.valueOf(cur.getColumnNames()));
            while (cur.moveToNext()) {

                sms = "From :" + cur.getString(2) + " : " + cur.getString(11) + "\n";
                sms = sms +  cur.getString(13) + "\n";
                mArrayList.add(sms);

            }


            mAdapter = new DataAdapterSMS(mArrayList);
            mRecyclerView.setAdapter(mAdapter);
            Log.i("json", mArrayList.toString());

        }


    }

    private void initsms() {
        mRecyclerView = findViewById(R.id.card_recycler_view_sms);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }
}
