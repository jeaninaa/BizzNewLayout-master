package com.example.trestanity.bizznewlayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactInformationFragment extends Fragment {

    TextView tvMobileNumber, tvTelephoneNumber;

    MainDBHelper mainDBHelper;

    SessionManager sessionManager;

    public ContactInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact_information, container, false);

        mainDBHelper = new MainDBHelper(getActivity());
        sessionManager = new SessionManager(getActivity());

        tvMobileNumber = (TextView) rootView.findViewById(R.id.tvMobileNumber);
        tvTelephoneNumber = (TextView) rootView.findViewById(R.id.tvTelephoneNumber);

        fetchInformation();

        return rootView;
    }

    public void fetchInformation()
    {

        HashMap<String, String> bizz = sessionManager.getBizz();
        String bizzName = bizz.get(SessionManager.KEY_BIZZ_NAME);

        tvMobileNumber.setText(mainDBHelper.getBizzCell(bizzName));
        tvTelephoneNumber.setText(mainDBHelper.getBizzTel(bizzName));

    }

}
