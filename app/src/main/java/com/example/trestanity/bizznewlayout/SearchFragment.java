package com.example.trestanity.bizznewlayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    EditText etSearchPlaces;
    Button btnSearchPlaces;

    RecyclerView rvSearchPlaces;

    Spinner spCity;

    List<BizzData> dbList;

    MainDBHelper mainDBHelper;

    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //RecyclerView.Adapter mAdapter;
    BizzRVAdapter mAdapter;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        getActivity().setTitle("Search it");

        mainDBHelper = new MainDBHelper(getActivity());

        etSearchPlaces = (EditText) rootView.findViewById(R.id.etSearchPlaces);
        btnSearchPlaces = (Button) rootView.findViewById(R.id.btnSearchPlaces);
        rvSearchPlaces = (RecyclerView) rootView.findViewById(R.id.rvSearchPlaces);
        spCity = (Spinner) rootView.findViewById(R.id.spCity);

        fetchData();

        //spinner
        String []city = getResources().getStringArray(R.array.city);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //adapter.notifyDataSetChanged();
        spCity.setAdapter(adapter);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCity = spCity.getSelectedItem().toString();

                searchNearby(selectedCity);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return rootView;
    }

    public void searchNearby(final String selectedCity)
    {

        btnSearchPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlace(selectedCity);
            }
        });

    }

    public void fetchData()
    {

        dbList = new ArrayList<BizzData>();
        dbList = mainDBHelper.getAllData();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSearchPlaces.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BizzRVAdapter(getContext(), dbList);
        rvSearchPlaces.setAdapter(mAdapter);

        rvSearchPlaces.setHasFixedSize(true);
    }

    public void fetchSearchedData(String searchedPlace, String selectedCity)
    {

        dbList = new ArrayList<BizzData>();
        dbList = mainDBHelper.getAllSearchedDataWhere(searchedPlace, selectedCity);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSearchPlaces.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BizzRVAdapter(getContext(), dbList);
        rvSearchPlaces.setAdapter(mAdapter);

        rvSearchPlaces.setHasFixedSize(true);
    }

    public void searchPlace(String selectedCity)
    {
        String searchedPlace;

        searchedPlace = etSearchPlaces.getText().toString().trim();

        fetchSearchedData(searchedPlace, selectedCity);
    }

}
