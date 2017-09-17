package com.example.trestanity.bizznewlayout;


import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapInformationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    GoogleApiClient mapBuilder;
    Location lastLocation;

    MainDBHelper mainDBHelper;
    SessionManager sessionManager;

    ProgressDialog progressDialog;

    public MapInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_information, container, false);

        mainDBHelper = new MainDBHelper(getActivity());
        sessionManager = new SessionManager(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //CURRENT LOCATION
        //map builder GOOGLE API get current location (coordinates)
        if (mapBuilder == null) {
            mapBuilder = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        return rootView;
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
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mapBuilder);

        fetchMapLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        mapBuilder.connect();

        //Progress dialog
        progressDialog = ProgressDialog.show(getActivity(),"Loading location","Please wait...",true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        super.onStart();
    }

    @Override
    public void onStop() {
        mapBuilder.disconnect();
        super.onStop();
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

    public void fetchMapLocation()
    {
        //for testing purpose
        /*String forTestLat = String.valueOf(bizzLatitude);
        String forTestLon = String.valueOf(bizzLongitude);
        Toast.makeText(getActivity(), forTestLat + "  " + forTestLon, Toast.LENGTH_LONG).show()*/;

        HashMap<String, String> bizz = sessionManager.getBizz();
        String bizzName = bizz.get(SessionManager.KEY_BIZZ_NAME);

        double bizzLatitude = Double.parseDouble(mainDBHelper.getBizzLat(bizzName));
        double bizzLongitude = Double.parseDouble(mainDBHelper.getBizzLon(bizzName));

        LatLng nearbyLocation = new LatLng(bizzLatitude, bizzLongitude);

        String street, brgy, city, region,address;

        //address
        street = mainDBHelper.getBizzStreet(bizzName);
        brgy = mainDBHelper.getBizzBrgy(bizzName);
        city = mainDBHelper.getBizzCity(bizzName);
        region = mainDBHelper.getBizzRegion(bizzName);
        address = street + ", " + brgy + ", " + city + ", " + region;

        //map marker
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(nearbyLocation)
                .title(mainDBHelper.getBizzName(bizzName))
                .snippet(address)
                .icon(BitmapDescriptorFactory.defaultMarker());

        mMap.addMarker(markerOptions);

        progressDialog.dismiss();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearbyLocation, 13));


    }

}
