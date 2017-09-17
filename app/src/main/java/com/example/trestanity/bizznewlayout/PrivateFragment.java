package com.example.trestanity.bizznewlayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateFragment extends Fragment {

    SessionManager sessionManager;

    public PrivateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_private, container, false);

        getActivity().setTitle("Private");

        sessionManager = new SessionManager(getActivity());

        /*HashMap<String, String> location = sessionManager.getLocation();
        String lat = location.get(SessionManager.KEY_CURRENT_LAT);
        String lon = location.get(SessionManager.KEY_CURRENT_LON);

        Toast.makeText(getActivity(), lat + "   " + lon, Toast.LENGTH_LONG).show();*/

        return rootView;
    }

}
