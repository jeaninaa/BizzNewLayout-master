package com.example.trestanity.bizznewlayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    Spinner spAddPostCategory, spAddPostPlace;
    Button btnSavePost;
    EditText etAddPostMessage;

    //dbhelper
    MainDBHelper mainDBHelper;

    //Session
    SessionManager sessionManager;

    //for category spinner
    List<String> postingCategory;
    String selectedPostCategory, selectedPlace;
    public ArrayList<String> placeList = new ArrayList<String>();

    //mapkeys
    public static final String KEY_CATEGORY = "cat";
    public static final String KEY_USERNAME = "entry";

    //mapkeys for posting
    public static final String KEY_BIZZ_NAME = "bizz_name";
    public static final String KEY_MESSAGE = "bizz_message";

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
        setContentView(R.layout.activity_add_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post it");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mainDBHelper = new MainDBHelper(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        //assigning data for category spinner
        postingCategory = mainDBHelper.getNearbyCategory();

        assignID();
        saveData();
        fetchSpinnerData();
    }

    public void assignID()
    {

        spAddPostCategory = (Spinner) findViewById(R.id.spAddPostCategory);
        spAddPostPlace = (Spinner) findViewById(R.id.spAddPostPlace);
        btnSavePost = (Button) findViewById(R.id.btnSavePost);
        etAddPostMessage = (EditText) findViewById(R.id.etAddPostMessage);

    }

    public void fetchSpinnerData()
    {

        // category
        ArrayAdapter<String> adapterAddPostNearbyCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, postingCategory);
        adapterAddPostNearbyCategory.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //adapter.notifyDataSetChanged();
        spAddPostCategory.setAdapter(adapterAddPostNearbyCategory);

        spAddPostCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPostCategory = spAddPostCategory.getSelectedItem().toString();

                fetchNearbyPlace(selectedPostCategory);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //places
        ArrayAdapter<String> adapterAddPostPlaces = new ArrayAdapter<String>(AddPostActivity.this, android.R.layout.simple_spinner_item, placeList);
        adapterAddPostPlaces.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAddPostPlace.setAdapter(adapterAddPostPlaces);

        spAddPostPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlace = spAddPostPlace.getSelectedItem().toString();

                //Toast.makeText(getApplicationContext(), selectedPlace, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fetchNearbyPlace(final String category)
    {

        final ArrayAdapter<String> adapterAddPostNearbyPlace = new ArrayAdapter<String>(AddPostActivity.this, android.R.layout.simple_spinner_dropdown_item, placeList);
        spAddPostPlace.setAdapter(adapterAddPostNearbyPlace);

        String fetchLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/getPostTenant";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                placeList.clear();
                try
                {
                    if(response.length()>0)
                    {
                        JSONArray place = null;
                        JSONObject obj = new JSONObject(response);
                        place = obj.getJSONArray("place_list");

                        for(int i=0; i<place.length(); i++)
                        {
                            JSONObject jsonObject = place.getJSONObject(i);
                            placeList.add(jsonObject.getString("bizz_name"));

                        }
                        adapterAddPostNearbyPlace.notifyDataSetChanged();

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

            HashMap<String, String> user = sessionManager.getUserDetails();
            final String username = user.get(SessionManager.KEY_USERNAME);

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(KEY_CATEGORY, category);
                map.put(KEY_USERNAME, username);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void saveData()
    {
        btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleySave(selectedPlace);
            }
        });

    }

    public void volleySave(final String bizzName)
    {
        String postLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/establishment/postIt";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, postLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if(response.trim().equals("success"))
                {
                    addingPost();
                }
                else
                {
                    errorAddingPost();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            final String bizzMessage = etAddPostMessage.getText().toString();

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(KEY_BIZZ_NAME, bizzName);
                map.put(KEY_MESSAGE, bizzMessage);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

//        final String bizzMessage = etAddPostMessage.getText().toString();
//
//        Toast.makeText(getApplicationContext(), bizzName + "\n" + bizzMessage , Toast.LENGTH_LONG).show();

    }

    public void cancelPrompt()
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Cancel");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to cancel posting?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        AddPostActivity.this.finish();
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

    @Override
    public void onBackPressed() {
        cancelPrompt();
    }

    public void addingPost()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Nearby");

        // set dialog message
        alertDialogBuilder
                .setMessage("Successfully posted!")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        AddPostActivity.this.finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void errorAddingPost()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Warning");

        // set dialog message
        alertDialogBuilder
                .setMessage("Error posting.")
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
}
