package com.example.trestanity.bizznewlayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    List<String> categoryNearby;

    EditText etBizzName,
            etSubCategory,
            etMobileNumber,
            etTelephoneNumber,
            etLandmark,
//            etRegion,
//            etCity,
            etStreet,
            etBarangay,
            etTag,

            etLatitude,
            etLongitude;

    Spinner spAddPlaceCategory, spAddPlaceRegion, spAddPlaceProvince, spAddPlaceCity;

    RadioGroup rgUsage, rgExistence, rgAvailability, rgLocation;
    RadioButton rbAvailability, rbExistence, rbUsage, rbLocation;

    String selectedAddPlaceCategory, selectedAddPlaceRegion, selectedAddPlaceProvince, selectedAddPlaceCity;

    LinearLayout llprivate, lllocation;

    MainDBHelper mainDBHelper;
    SessionManager sessionManager;

    //mapkeys
    public static final String NEARBY_NAME = "st_name";
    public static final String NEARBY_CATEGORY = "st_cat";
    public static final String NEARBY_SUB_CATEGORY = "st_subcat";
    public static final String NEARBY_MOB_NUMBER = "st_cel";
    public static final String NEARBY_TEL_NUMBER = "st_tel";
    public static final String NEARBY_LANDMARK = "st_lmark";

    public static final String NEARBY_REGION = "st_regiojn";
    public static final String NEARBY_PROVINCE = "st_province";
    public static final String NEARBY_CITY = "st_city";

    public static final String NEARBY_STREET= "st_street";
    public static final String NEARBY_BARANGAY = "st_brgy";
    public static final String NEARBY_LATITUDE = "mp_lat";
    public static final String NEARBY_LONGITUDE = "mp_lon";
    //public static final String NEARBY_EXISTENCE = "nearby_existence";
    //public static final String NEARBY_TAG = "nearby_tag";
    //public static final String NEARBY_AVAILABILITY = "nearby_availability";
    public static final String NEARBY_USER = "entry";

    //mapkey for fetching spinner data
    public static final String SEL_REGION = "region_name";
    public static final String SEL_PROVINCE = "province_name";

    //for spinner data
    public ArrayList<String> regionList = new ArrayList<String>();
    public ArrayList<String> provinceList = new ArrayList<String>();
    public ArrayList<String> cityList = new ArrayList<String>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                cancelPrompt();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Nearby Place");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //databasehelper
        mainDBHelper = new MainDBHelper(getApplicationContext());

        //session
        sessionManager = new SessionManager(getApplicationContext());

        //assigning component ids
        assignID();

        //spinner category
        spinners();
        fetchAddPlaceRegion();

        //floating button
        FloatingActionButton fabSaveNearby = (FloatingActionButton) findViewById(R.id.fabSaveNearby);
        fabSaveNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlace();
            }
        });

        llprivate.setVisibility(View.GONE);
        lllocation.setVisibility(View.GONE);
        radioButton();


        //CURRENT LOCATION
        //map builder GOOGLE API get current location (coordinates)
        if (mapBuilder == null) {
            mapBuilder = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void spinners()
    {
        //category

        categoryNearby = mainDBHelper.getNearbyCategory();

        ArrayAdapter<String> adapterAddPlaceCategory = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_item, categoryNearby);
        adapterAddPlaceCategory.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAddPlaceCategory.setAdapter(adapterAddPlaceCategory);

        spAddPlaceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddPlaceCategory = spAddPlaceCategory.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //regions
        ArrayAdapter<String> adapterAddPlaceRegion = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_item, regionList);
        adapterAddPlaceRegion.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAddPlaceRegion.setAdapter(adapterAddPlaceRegion);

        spAddPlaceRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddPlaceRegion = spAddPlaceRegion.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(), selectedAddPlaceRegion, Toast.LENGTH_LONG).show();
                cityList.clear();
                provinceList.clear();
                fetchAddPlaceProvince(selectedAddPlaceRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //province
        ArrayAdapter<String> adapterAddPlaceProvince = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_item, provinceList);
        adapterAddPlaceProvince.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAddPlaceProvince.setAdapter(adapterAddPlaceProvince);

        spAddPlaceProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddPlaceProvince = spAddPlaceProvince.getSelectedItem().toString();
                fetchAddPlaceCity(selectedAddPlaceProvince);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //city
        ArrayAdapter<String> adapterAddPlaceCity = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_item, cityList);
        adapterAddPlaceCity.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAddPlaceCity.setAdapter(adapterAddPlaceCity);

        spAddPlaceCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddPlaceCity = spAddPlaceCity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void fetchAddPlaceRegion()
    {

        final ArrayAdapter<String> adapterAddPlaceRegion = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_dropdown_item, regionList);
        spAddPlaceRegion.setAdapter(adapterAddPlaceRegion);

        String fetchLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/getRegion";

        StringRequest stringRequest = new StringRequest(fetchLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    if(response.length()>0)
                    {
                        JSONArray region = null;
                        JSONObject obj = new JSONObject(response);
                        region = obj.getJSONArray("region_list");

                        for(int i=0; i<region.length(); i++)
                        {

                            JSONObject jsonObject = region.getJSONObject(i);
                            regionList.add(jsonObject.getString("region"));

                            //Toast.makeText(getApplicationContext(),selectedAddPlaceRegion, Toast.LENGTH_LONG).show();

                            fetchAddPlaceProvince(selectedAddPlaceRegion);
                        }
                        adapterAddPlaceRegion.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No data", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"internet Connection not stable.", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchAddPlaceProvince(final String selectedRegion)
    {

        final ArrayAdapter<String> adapterAddPlaceProvince = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_dropdown_item, provinceList);
        spAddPlaceProvince.setAdapter(adapterAddPlaceProvince);

        String fetchLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/getProvince";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    provinceList.clear();
                    if(response.length()>0) {
                        JSONArray province = null;
                        JSONObject obj = new JSONObject(response);
                        province = obj.getJSONArray("province_list");

                        for (int i = 0; i < province.length(); i++)
                        {
                            JSONObject jsonObject = province.getJSONObject(i);
                            provinceList.add(jsonObject.getString("province"));

                            //Toast.makeText(getApplicationContext(),selectedAddPlaceRegion, Toast.LENGTH_LONG).show();

                            fetchAddPlaceCity(selectedAddPlaceProvince);

                        }
                        adapterAddPlaceProvince.notifyDataSetChanged();

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(SEL_REGION, selectedRegion);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void fetchAddPlaceCity(final String selectedProvince)
    {

        final ArrayAdapter<String> adapterAddPlaceCity = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.simple_spinner_dropdown_item, cityList);
        spAddPlaceCity.setAdapter(adapterAddPlaceCity);

        String fetchLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/getCity";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                try
                {
                    cityList.clear();
                    if(response.length()>0)
                    {

                        JSONArray city = null;
                        JSONObject obj = new JSONObject(response);
                        city = obj.getJSONArray("city_list");

                        for(int i=0; i<city.length(); i++)
                        {
                            JSONObject jsonObject = city.getJSONObject(i);
                            cityList.add(jsonObject.getString("city"));
                        }
                        adapterAddPlaceCity.notifyDataSetChanged();

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(SEL_PROVINCE, selectedProvince);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void radioButton() {
        /*
        * radiogroups and radiobutton part
        * */

        rgLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbLocation = (RadioButton) group.findViewById(checkedId);
                if (null != rbLocation && checkedId > -1) {
                    //Toast.makeText(getApplicationContext(), rbUsage.getText(), Toast.LENGTH_SHORT).show();

                    if (rbLocation.getText().toString().trim().equals("Enter Location")) {
                        lllocation.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), rbUsage.getText(), Toast.LENGTH_SHORT).show();
                    } else {
                        lllocation.setVisibility(View.GONE);
                    }

                }
            }
        });

        rgUsage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbUsage = (RadioButton) group.findViewById(checkedId);
                if (null != rbUsage && checkedId > -1) {
                    //Toast.makeText(getApplicationContext(), rbUsage.getText(), Toast.LENGTH_SHORT).show();

                    if (rbUsage.getText().toString().equals("Private")) {
                        llprivate.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), rbUsage.getText(), Toast.LENGTH_SHORT).show();
                    } else {
                        llprivate.setVisibility(View.GONE);
                    }

                }
            }
        });

        rgExistence.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbExistence = (RadioButton) group.findViewById(checkedId);
                /*if (null != rbAvailability && checkedId > -1) {
                    Toast.makeText(getApplicationContext(), rbExistence.getText(), Toast.LENGTH_SHORT).show();

                }*/
            }
        });

        rgAvailability.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbAvailability = (RadioButton) group.findViewById(checkedId);
                if (null != rbAvailability && checkedId > -1) {
                    //Toast.makeText(getApplicationContext(), rbAvailability.getText(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void cancelPrompt() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Cancel");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to cancel adding Nearby place?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        AddPlaceActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void assignID() {

        etBizzName = (EditText) findViewById(R.id.etBizzName);
        etSubCategory = (EditText) findViewById(R.id.etSubCategory);
        etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
        etTelephoneNumber = (EditText) findViewById(R.id.etTelephoneNumber);
        etLandmark = (EditText) findViewById(R.id.etLandmark);
//        etRegion = (EditText) findViewById(R.id.etRegion);
//        etCity = (EditText) findViewById(R.id.etCity);
        etStreet = (EditText) findViewById(R.id.etStreet);
        etBarangay = (EditText) findViewById(R.id.etBarangay);
        etTag = (EditText) findViewById(R.id.etTag);
        etLatitude = (EditText) findViewById(R.id.etLatitude);
        etLongitude = (EditText) findViewById(R.id.etLongitude);


        spAddPlaceCategory = (Spinner) findViewById(R.id.spAddPlaceCategory);
        spAddPlaceRegion = (Spinner) findViewById(R.id.spAddPlaceRegion);
        spAddPlaceProvince = (Spinner) findViewById(R.id.spAddPlaceProvince);
        spAddPlaceCity = (Spinner) findViewById(R.id.spAddPlaceCity);

        rgLocation = (RadioGroup) findViewById(R.id.rgLocation);

        rgUsage = (RadioGroup) findViewById(R.id.rgUsage);

        rgExistence = (RadioGroup) findViewById(R.id.rgExistence);

        rgAvailability = (RadioGroup) findViewById(R.id.rgAvailability);


        llprivate = (LinearLayout) findViewById(R.id.llprivate);
        lllocation = (LinearLayout) findViewById(R.id.lllocation);

    }

    //for currentLOcation
    GoogleApiClient mapBuilder;
    Location lastLocation;
    private GoogleMap mMap;

    double currentLatitude, currentLongitude;

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mapBuilder);

        if (lastLocation != null) {
            currentLatitude = lastLocation.getLatitude();
            currentLongitude = lastLocation.getLongitude();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            try {
                List<Address> currentLocation = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);


            } catch (IOException e) {
                e.printStackTrace();
                currentLocationNotFound();
            }

        }

    }

    public void addPlace() {

        final String bizzName,
                bizzSubCategory,
                bizzMobileNumber,
                bizzTelephoneNumber,
                bizzLandmark,
                bizzStreet,
                bizzBarangay,
                bizzTag,

                inputLatitude,
                inputLongitude;

        final String currLat, currLon;

        currLat = String.valueOf(currentLatitude);
        currLon = String.valueOf(currentLongitude);


        bizzName = etBizzName.getText().toString().trim();
        bizzSubCategory = etSubCategory.getText().toString().trim();
        bizzMobileNumber = etMobileNumber.getText().toString().trim();
        bizzTelephoneNumber = etTelephoneNumber.getText().toString().trim();
        bizzLandmark = etLandmark.getText().toString().trim();
//        bizzRegion = etRegion.getText().toString().trim();
//        bizzCity = etCity.getText().toString().trim();
        bizzStreet = etStreet.getText().toString().trim();
        bizzBarangay = etBarangay.getText().toString().trim();
        inputLatitude = etLatitude.getText().toString().trim();
        inputLongitude = etLongitude.getText().toString().trim();
        bizzTag = etTag.getText().toString().trim();

        RadioButton rbUSAGE = (RadioButton) rgUsage.findViewById(rgUsage.getCheckedRadioButtonId());
        RadioButton rbLoc = (RadioButton) rgLocation.findViewById(rgLocation.getCheckedRadioButtonId());
        RadioButton rbEXISTENCE = (RadioButton) rgExistence.findViewById(rgExistence.getCheckedRadioButtonId());
        RadioButton rbAVAILABILITY = (RadioButton) rgAvailability.findViewById(rgAvailability.getCheckedRadioButtonId());

        /*if ((llprivate.getVisibility() == View.VISIBLE) && (lllocation.getVisibility()==View.GONE)) {

            if(bizzTag.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter a tag.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), bizzName
                            + "\n" + selectedItem
                            + "\n" + bizzSubCategory
                            + "\n" + bizzMobileNumber
                            + "\n" + bizzTelephoneNumber
                            + "\n" + bizzLandmark
                            + "\n" + bizzRegion
                            + "\n" + bizzCity
                            + "\n" + bizzStreet
                            + "\n" + bizzBarangay
                            + "\n" + currLat
                            + "\n" + currLon
//                            + "\n" + rb.getText()
                            + "\n" + rbEXISTENCE.getText()
                            + "\n" + bizzTag
                            + "\n" + rbAVAILABILITY.getText()
                    , Toast.LENGTH_LONG).show();

                volleySave(bizzName, selectedAddPlaceCategory, bizzSubCategory, bizzMobileNumber, bizzTelephoneNumber, bizzLandmark, selectedAddPlaceRegion, selectedAddPlaceProvince, selectedAddPlaceCity, bizzStreet, bizzBarangay, currLat, currLon,rbUSAGE.getText(), rbEXISTENCE.getText(), bizzTag, rbAVAILABILITY.getText());
            }

        }*/
        if((llprivate.getVisibility()==View.GONE) && (lllocation.getVisibility()==View.VISIBLE))
        {
            if(inputLatitude.equals("") && inputLatitude.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter latitude and longitude.", Toast.LENGTH_LONG).show();
            }
            else
            {
                /*Toast.makeText(getApplicationContext(), bizzName
                                + "\n" + selectedItem
                                + "\n" + bizzSubCategory
                                + "\n" + bizzMobileNumber
                                + "\n" + bizzTelephoneNumber
                                + "\n" + bizzLandmark
                                + "\n" + bizzRegion
                                + "\n" + bizzCity
                                + "\n" + bizzStreet
                                + "\n" + bizzBarangay
                                + "\n" + inputLatitude
                                + "\n" + inputLongitude
                                + "\n" + rbUSAGE.getText()
                                + "\n" + rbAVAILABILITY.getText()
                        , Toast.LENGTH_LONG).show();*/
                if(bizzName.equals("")||bizzSubCategory.equals("")||
                        bizzMobileNumber.equals("")||bizzTelephoneNumber.equals("")||
                        bizzLandmark.equals("")||bizzStreet.equals("")||
                        bizzBarangay.equals(""))
                {
                    pleaseInput();
                }
                else
                {
                    volleySave(bizzName, selectedAddPlaceCategory, bizzSubCategory, bizzMobileNumber, bizzTelephoneNumber, bizzLandmark, selectedAddPlaceRegion, selectedAddPlaceProvince, selectedAddPlaceCity, bizzStreet, bizzBarangay, inputLatitude, inputLongitude,rbUSAGE.getText(), rbEXISTENCE.getText(), bizzTag, rbAVAILABILITY.getText());
                }

            }
        }
        /*else if((llprivate.getVisibility()==View.VISIBLE) && (lllocation.getVisibility()==View.VISIBLE))
        {
            if((inputLatitude.equals("") && inputLatitude.equals("")) || (bizzTag.equals("")))
            {
                Toast.makeText(getApplicationContext(), "Please fill up the needed details.", Toast.LENGTH_LONG).show();
            }
            else
            {
                *//*Toast.makeText(getApplicationContext(), bizzName
                                + "\n" + selectedItem
                                + "\n" + bizzSubCategory
                                + "\n" + bizzMobileNumber
                                + "\n" + bizzTelephoneNumber
                                + "\n" + bizzLandmark
                                + "\n" + bizzRegion
                                + "\n" + bizzCity
                                + "\n" + bizzStreet
                                + "\n" + bizzBarangay
                                + "\n" + inputLatitude
                                + "\n" + inputLongitude
                                + "\n" + rbEXISTENCE.getText()
                                + "\n" + bizzTag
                                + "\n" + rbAVAILABILITY.getText()
                        , Toast.LENGTH_LONG).show();*//*
                volleySave(bizzName, selectedAddPlaceCategory, bizzSubCategory, bizzMobileNumber, bizzTelephoneNumber, bizzLandmark, selectedAddPlaceRegion, selectedAddPlaceProvince, selectedAddPlaceCity, bizzStreet, bizzBarangay, inputLatitude, inputLongitude, rbUSAGE.getText(), rbEXISTENCE.getText(), bizzTag, rbAVAILABILITY.getText());
            }
        }*/
        else
        {
            /*Toast.makeText(getApplicationContext(), bizzName
                            + "\n" + selectedItem
                            + "\n" + bizzSubCategory
                            + "\n" + bizzMobileNumber
                            + "\n" + bizzTelephoneNumber
                            + "\n" + bizzLandmark
                            + "\n" + bizzRegion
                            + "\n" + bizzCity
                            + "\n" + bizzStreet
                            + "\n" + bizzBarangay
                            + "\n" + currLat
                            + "\n" + currLon
                            + "\n" + rbUSAGE.getText()
                            + "\n" + rbAVAILABILITY.getText()
                    , Toast.LENGTH_LONG).show();*/
            if(bizzName.equals("")||bizzSubCategory.equals("")||
                    bizzMobileNumber.equals("")||bizzTelephoneNumber.equals("")||
                    bizzLandmark.equals("")||bizzStreet.equals("")||
                    bizzBarangay.equals(""))
            {
                pleaseInput();
            }
            else
            {
                volleySave(bizzName, selectedAddPlaceCategory, bizzSubCategory, bizzMobileNumber, bizzTelephoneNumber, bizzLandmark, selectedAddPlaceRegion, selectedAddPlaceProvince, selectedAddPlaceCity, bizzStreet, bizzBarangay, currLat, currLon, rbUSAGE.getText(), rbEXISTENCE.getText(), bizzTag, rbAVAILABILITY.getText());
            }
        }

    }

    public void volleySave(final String name, final String category, final String subCategory, final String mobileNumber, final String telephoneNumber, final String landmark, final String region, final String province, final String city, final String street, final String barangay, final String latitude, final String longitude, final CharSequence usage ,final CharSequence existence, final String tag, final CharSequence availability)
    {

        String saveLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/establishment"; //saveLink

        StringRequest stringRequest = new StringRequest(Request.Method.POST, saveLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if(!response.trim().equals("success"))
                {
                    addingPlace();
                }
                else
                {
                    errorAddingPrompt();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            HashMap<String, String> user = sessionManager.getUserDetails();
            final String username = user.get(SessionManager.KEY_USERNAME);

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(NEARBY_NAME, name);
                map.put(NEARBY_CATEGORY, category);
                map.put(NEARBY_SUB_CATEGORY, subCategory);
                map.put(NEARBY_MOB_NUMBER, mobileNumber);
                map.put(NEARBY_TEL_NUMBER, telephoneNumber);
                map.put(NEARBY_LANDMARK, landmark);
                map.put(NEARBY_REGION, region);
                map.put(NEARBY_PROVINCE, province);
                map.put(NEARBY_CITY, city);
                map.put(NEARBY_STREET, street);
                map.put(NEARBY_BARANGAY, barangay);
                map.put(NEARBY_LATITUDE, latitude);
                map.put(NEARBY_LONGITUDE, longitude);
                //public static final String NEARBY_EXISTENCE = "nearby_existence";
                //public static final String NEARBY_TAG = "nearby_tag";
                //public static final String NEARBY_AVAILABILITY = "nearby_availability";
                map.put(NEARBY_USER, username);

                /*if(usage.equals("Private"))
                {
                    Toast.makeText(getApplicationContext(), name
                                    + "\n" + category
                                    + "\n" + subCategory
                                    + "\n" + mobileNumber
                                    + "\n" + telephoneNumber
                                    + "\n" + landmark
                                    + "\n" + region
                                    + "\n" + province
                                    + "\n" + city
                                    + "\n" + street
                                    + "\n" + barangay
                                    + "\n" + latitude
                                    + "\n" + longitude
                                    + "\n" + existence
                                    + "\n" + tag
                                    + "\n" + availability
                                    + "\n" + availability
                            , Toast.LENGTH_LONG).show();
                }
                else if(usage.equals("Public"))
                {
                    Toast.makeText(getApplicationContext(), name
                                    + "\n" + category
                                    + "\n" + subCategory
                                    + "\n" + mobileNumber
                                    + "\n" + telephoneNumber
                                    + "\n" + landmark
                                    + "\n" + region
                                    + "\n" + province
                                    + "\n" + city
                                    + "\n" + street
                                    + "\n" + barangay
                                    + "\n" + latitude
                                    + "\n" + longitude
                                    + "\n" + usage
                                    + "\n" + availability
                                    + "\n" + availability
                            , Toast.LENGTH_LONG).show();
                }*/

                return map;
            }
        };


        /*if(usage.equals("Private"))
        {
            Toast.makeText(getApplicationContext(), name
                            + "\n" + category
                            + "\n" + subCategory
                            + "\n" + mobileNumber
                            + "\n" + telephoneNumber
                            + "\n" + landmark
                            + "\n" + region
                            + "\n" + province
                            + "\n" + city
                            + "\n" + street
                            + "\n" + barangay
                            + "\n" + latitude
                            + "\n" + longitude
                            + "\n" + usage
                            + "\n" + existence
                            + "\n" + tag
                            + "\n" + availability
                            + "\n" + username
                    , Toast.LENGTH_LONG).show();
        }
        else if(usage.equals("Public"))
        {
            Toast.makeText(getApplicationContext(), name
                            + "\n" + category
                            + "\n" + subCategory
                            + "\n" + mobileNumber
                            + "\n" + telephoneNumber
                            + "\n" + landmark
                            + "\n" + region
                            + "\n" + province
                            + "\n" + city
                            + "\n" + street
                            + "\n" + barangay
                            + "\n" + latitude
                            + "\n" + longitude
                            + "\n" + usage
                            + "\n" + availability
                            + "\n" + username
                    , Toast.LENGTH_LONG).show();
        }*/

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        mapBuilder.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mapBuilder.disconnect();
        super.onStop();
    }

    public void currentLocationNotFound()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Location");

        // set dialog message
        alertDialogBuilder
                .setMessage("Could not get current address.")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        AddPlaceActivity.this.finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void addingPlace()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Nearby");

        // set dialog message
        alertDialogBuilder
                .setMessage("Nearby place added!")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        AddPlaceActivity.this.finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void errorAddingPrompt()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Warning");

        // set dialog message
        alertDialogBuilder
                .setMessage("Nearby place not added!")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void pleaseInput()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Warning");

        // set dialog message
        alertDialogBuilder
                .setMessage("Please fill up the form.")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        cancelPrompt();
    }
}
