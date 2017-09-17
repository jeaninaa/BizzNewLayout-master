package com.example.trestanity.bizznewlayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyInformationFragment extends Fragment {

    TextView tvAddress, tvLandmark, tvCategory, tvSubCategory;

    MainDBHelper mainDBHelper;

    SessionManager sessionManager;

    public NearbyInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_nearby_information, container, false);

        mainDBHelper = new MainDBHelper(getActivity());
        sessionManager = new SessionManager(getActivity());

        tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
        tvLandmark = (TextView) rootView.findViewById(R.id.tvLandmark);
        tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);
        tvSubCategory = (TextView) rootView.findViewById(R.id.tvSubCategory);

        fetchInformation();

        return rootView;
    }

    public void fetchInformation()
    {
        HashMap<String, String> bizz = sessionManager.getBizz();
        String bizzName = bizz.get(SessionManager.KEY_BIZZ_NAME);

        //Toast.makeText(getActivity(), bizzName, Toast.LENGTH_LONG).show();
        tvAddress.setText(mainDBHelper.getBizzStreet(bizzName) + ", " + mainDBHelper.getBizzBrgy(bizzName) + ", " + mainDBHelper.getBizzCity(bizzName) + ", " + mainDBHelper.getBizzRegion(bizzName));
        tvLandmark.setText(mainDBHelper.getBizzLandmark(bizzName));
        tvCategory.setText(mainDBHelper.getBizzCat(bizzName));
        tvSubCategory.setText(mainDBHelper.getBizzSubCat(bizzName));
    }

}
