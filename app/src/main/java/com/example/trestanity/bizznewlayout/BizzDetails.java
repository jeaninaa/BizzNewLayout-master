package com.example.trestanity.bizznewlayout;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BizzDetails extends AppCompatActivity {

    TextView tvBizzName;

    Button btnInfo, btnMapInfo, btnContactInfo;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizz_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        assignID();

        Bundle bundle = getIntent().getExtras();
        tvBizzName.setText(bundle.getString("BizzName"));

        btnInfo.setBackgroundColor(Color.parseColor("#0f303f"));
        btnMapInfo.setBackgroundColor(Color.parseColor("#1b607f"));
        btnContactInfo.setBackgroundColor(Color.parseColor("#1b607f"));

        firstView();

    }

    public void assignID()
    {
        tvBizzName = (TextView) findViewById(R.id.tvBizzName);

        btnInfo = (Button) findViewById(R.id.btnInfo);
        btnMapInfo = (Button) findViewById(R.id.btnMapInfo);
        btnContactInfo = (Button) findViewById(R.id.btnContactInfo);
    }

    public void firstView()
    {
        NearbyInformationFragment nearbyInformationFragment = new NearbyInformationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, nearbyInformationFragment,nearbyInformationFragment.getTag())
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .commit();
    }

    public void btnClickDetail(View view)
    {
        FragmentManager manager = getSupportFragmentManager();

        if(view==btnInfo)
        {
            NearbyInformationFragment nearbyInformationFragment = new NearbyInformationFragment();
            manager.beginTransaction().replace(R.id.content,
                    nearbyInformationFragment,
                    nearbyInformationFragment.getTag())
                    .commit();

            btnInfo.setBackgroundColor(Color.parseColor("#0f303f"));
            btnMapInfo.setBackgroundColor(Color.parseColor("#1b607f"));
            btnContactInfo.setBackgroundColor(Color.parseColor("#1b607f"));
        }
        else if(view==btnMapInfo)
        {
            MapInformationFragment mapInformationFragment = new MapInformationFragment();
            manager.beginTransaction().replace(R.id.content,
                    mapInformationFragment,
                    mapInformationFragment.getTag())
                    .commit();

            btnInfo.setBackgroundColor(Color.parseColor("#1b607f"));
            btnMapInfo.setBackgroundColor(Color.parseColor("#0f303f"));
            btnContactInfo.setBackgroundColor(Color.parseColor("#1b607f"));
        }
        else if(view==btnContactInfo)
        {
            ContactInformationFragment contactInformationFragment = new ContactInformationFragment();
            manager.beginTransaction().replace(R.id.content,
                    contactInformationFragment,
                    contactInformationFragment.getTag())
                    .commit();

            btnInfo.setBackgroundColor(Color.parseColor("#1b607f"));
            btnMapInfo.setBackgroundColor(Color.parseColor("#1b607f"));
            btnContactInfo.setBackgroundColor(Color.parseColor("#0f303f"));

        }

    }

}
