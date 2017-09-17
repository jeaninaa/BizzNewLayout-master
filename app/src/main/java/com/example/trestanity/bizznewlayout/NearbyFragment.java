package com.example.trestanity.bizznewlayout;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    Spinner spNearbyCategory;

    List<String> nearbyCategory;

    MainDBHelper mainDBHelper;

    List<BizzData> dbList;
    RecyclerView rvNearbyPlaces;

    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //RecyclerView.Adapter mAdapter;
    BizzRVAdapter mAdapter;

    String sel_cat;

    ProgressDialog progressDialog;

    SwipeRefreshLayout mSwipeRefreshLayout;

    //for currentLOcation
    GoogleApiClient mapBuilder;
    Location lastLocation;
    private GoogleMap mMap;

    double currentLatitude, currentLongitude;

    //North
    double latNorth,lonNorth;

    //South
    double latSouth,lonSouth;

    //East
    double latEast,lonEast;

    //west
    double latWest, lonWest;

    double latNorthSouth = 0.009052;
    double lonNorthSouth = 0.009052;

    double latEastWest = 0.003210;
    double lonEastWest = 0.009790;

    /*double latNorthSouth = 0.000075;
    double lonNorthSouth = 0.000075;

    double latEastWest = 0.000075;
    double lonEastWest = 0.000075;*/

    SessionManager sessionManager;

    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_nearby, container, false);

        getActivity().setTitle("Nearby");

        mainDBHelper = new MainDBHelper(getActivity());
        sessionManager = new SessionManager(getActivity());
        setHasOptionsMenu(false);
        //sv_bizz = (SearchView) rootview.findViewById(R.id.sv_bizz);

        nearbyCategory = mainDBHelper.getNearbyCategory();

        rvNearbyPlaces = (RecyclerView) rootview.findViewById(R.id.rvNearbyPlaces);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });


        spNearbyCategory = (Spinner) rootview.findViewById(R.id.spNearbyCategory);
        /*recyclerView = (RecyclerView) rootview.findViewById(R.id.rv_bizz);
        recyclerView.setHasFixedSize(true);*/

        fetchLatestPrompt();

        //CURRENT LOCATION
        //map builder GOOGLE API get current location (coordinates)
        if (mapBuilder == null) {
            mapBuilder = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return rootview;
    }

    public void refreshItems() {
        mSwipeRefreshLayout.refreshDrawableState();
        getBizzData();
    }


    public void fetchLatestPrompt() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Update Nearby Place");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to update Nearby Places?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        getBizzData();
                        getSpinnerCategory();
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

    public void getSpinnerCategory() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, nearbyCategory);
        //adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spNearbyCategory.setAdapter(adapter);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String getCategory = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/getcategory";
        StringRequest stringRequest = new StringRequest(getCategory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.length() > 0) {
                        mainDBHelper.deleteAllNearbyCategory();
                        JSONArray cat = null;
                        JSONObject obj = new JSONObject(response);
                        cat = obj.getJSONArray("categoryList");

                        for (int i = 0; 1 < cat.length(); i++) {
                            JSONObject jsonObject = cat.getJSONObject(i);
                            boolean isInserted = mainDBHelper.insertNearbyCategory(jsonObject.getString("nb_category_name"));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        requestQueue.add(stringRequest);
    }

    public void getBizzData() {
        //mSwipeRefreshLayout.setRefreshing(true);
        progressDialog = ProgressDialog.show(getActivity(), "Loading Nearby Places", "Please wait...", true);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //link fetching data
        String getData = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/gettenant";

        StringRequest stringRequest = new StringRequest(getData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.length() > 0) {

                        mainDBHelper.deleteAllNearbyData();
                        JSONArray bizzdata = null;
                        JSONObject obj = new JSONObject(response);
                        bizzdata = obj.getJSONArray("tenantList");

                        //bizzDatas.clear();
                        for (int i = 0; i < bizzdata.length(); i++) {
                            JSONObject jsonObject = bizzdata.getJSONObject(i);

                            boolean isInserted = mainDBHelper.insertNearbyData(jsonObject.getString("bizz_name"), jsonObject.getString("street"), jsonObject.getString("telno"), jsonObject.getString("celno"), jsonObject.getString("landmark"), jsonObject.getString("region"), jsonObject.getString("city"), jsonObject.getString("brgy"), jsonObject.getString("category"), jsonObject.getString("sub_category"), jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"), jsonObject.getString("status"), jsonObject.getString("date_added"), jsonObject.getString("added_by"));
                        }
                        progressDialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onStart() {
        mapBuilder.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mapBuilder.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        } else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mapBuilder);

            if (lastLocation != null) {
                currentLatitude = lastLocation.getLatitude();
                currentLongitude = lastLocation.getLongitude();

                //for testing purpose
                /*String lat = String.valueOf(currentLatitude);
                String lon = String.valueOf(currentLongitude);
                Toast.makeText(getActivity(), lat + "  " + lon, Toast.LENGTH_LONG).show();*/

                sessionManager.createLocation(currentLatitude, currentLongitude);

                /*
                * Formula
                *
                * north = + +
                * south = - -
                * east =  - +
                * west =  + -
                *
                * */
                //North
                latNorth = currentLatitude + latNorthSouth;
                lonNorth = currentLongitude + lonNorthSouth;

                //South
                latSouth = currentLatitude - latNorthSouth;
                lonSouth = currentLongitude - lonNorthSouth;

                //East
                latEast = currentLatitude - latEastWest;
                lonEast = currentLongitude + lonEastWest;

                //west
                latWest = currentLatitude + latEastWest;
                lonWest = currentLongitude - lonEastWest;

                /*LatLng North = new LatLng(latNorth,lonNorth);
                LatLng South = new LatLng(latSouth,lonSouth);
                LatLng East = new LatLng(latEast,lonEast);
                LatLng West = new LatLng(latWest,lonWest);*/



                //for testing purpose
                /*String north = String.valueOf(North);
                Toast.makeText(getActivity(), north, Toast.LENGTH_LONG).show();*/
                //14.5921698,121.06142600000001

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, nearbyCategory);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spNearbyCategory.setAdapter(adapter);

                spNearbyCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        sel_cat = spNearbyCategory.getSelectedItem().toString();
                        //Toast.makeText(getActivity(), sel_cat, Toast.LENGTH_LONG).show();

                        spinnerData(sel_cat, latNorth, lonNorth,
                                latSouth, lonSouth,
                                latEast, lonEast,
                                latWest, lonWest);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

        }

    }

    public void spinnerData(String category, double latNorth, double lonNorth,
                            double latSouth, double lonSouth,
                            double latEast, double lonEast,
                            double latWest, double lonWest) {


        dbList = new ArrayList<BizzData>();
        dbList = mainDBHelper.getAllNearbyDataWhere(category, latNorth, lonNorth,
                                                        latSouth, lonSouth,
                                                        latEast, lonEast,
                                                        latWest, lonWest);

        /*dbList = mainDBHelper.getAllDataWhere(category);*/

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvNearbyPlaces.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BizzRVAdapter(getContext(), dbList);
        rvNearbyPlaces.setAdapter(mAdapter);


        rvNearbyPlaces.setHasFixedSize(true);

        LatLng North = new LatLng(latNorth,lonNorth);
        LatLng South = new LatLng(latSouth,lonSouth);
        LatLng East = new LatLng(latEast,lonEast);
        LatLng West = new LatLng(latWest,lonWest);

        String north = String.valueOf(North);
        String south = String.valueOf(South);
        String east = String.valueOf(East);
        String west = String.valueOf(West);

        /*Toast.makeText(getActivity(), north
                + "\n" + south
                + "\n" + east
                + "\n" + west
                , Toast.LENGTH_LONG).show();*/


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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}
