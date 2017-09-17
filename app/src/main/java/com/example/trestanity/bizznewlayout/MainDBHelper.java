package com.example.trestanity.bizznewlayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

/**
 * Created by Trestanity on 05/03/2017.
 */
public class MainDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "newupbizz.db";

    public static final String TABLE_NAME_P = "tbl_place";

    public static final String C_COL_1 = "bizz_name";
    public static final String C_COL_2 = "street";
    public static final String C_COL_3 = "telno";
    public static final String C_COL_4 = "celno";
    //email
    //website
    public static final String C_COL_5 = "landmark";
    public static final String C_COL_6 = "region";
    public static final String C_COL_7 = "city";
    public static final String C_COL_8 = "brgy";
    public static final String C_COL_9 =  "category";
    public static final String C_COL_10 = "sub_category";
    public static final String C_COL_11 = "latitude";
    public static final String C_COL_12 = "longitude";
    public static final String C_COL_13 = "status";
    public static final String C_COL_14 = "date_added";
    public static final String C_COL_15 = "added_by";

    public static final String TABLE_NAME_CATEGORY = "tbl_category";

    public static final String CATEG_COL_1 = "category";

    SessionManager sessionManager;

    //static Context context;


    public MainDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        sessionManager = new SessionManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME_P + "(bizz_name TEXT,street TEXT,telno TEXT,celno TEXT,landmark TEXT,region TEXT,city TEXT, brgy TEXT,category TEXT,sub_category TEXT,latitude REAL,longitude REAL,status TEXT,date_added TEXT,added_by TEXT)");
        db.execSQL("create table " + TABLE_NAME_CATEGORY + "(category TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME_P);
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME_CATEGORY);


        onCreate(db);

    }

    public void deleteAllNearbyCategory()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_CATEGORY, null, null);
        db.close();
    }

    public boolean insertNearbyCategory(String nearbyCategory)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(CATEG_COL_1, nearbyCategory);

        long result1 = db.insert(TABLE_NAME_CATEGORY, null, contentValues);
        db.close();
        if(result1 ==  -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void deleteAllNearbyData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_P, null, null);
        db.close();
    }

    public boolean insertNearbyData(String bizz_name, String street, String telno, String celno, String landmark, String region, String city, String brgy, String category, String sub_category, double latitude, double longitude, String status, String date_added, String added_by)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(C_COL_1, bizz_name);
        contentValues.put(C_COL_2, street);
        contentValues.put(C_COL_3, telno);
        contentValues.put(C_COL_4, celno);
        contentValues.put(C_COL_5, landmark);
        contentValues.put(C_COL_6, region);
        contentValues.put(C_COL_7, city);
        contentValues.put(C_COL_8, brgy);
        contentValues.put(C_COL_9, category);
        contentValues.put(C_COL_10, sub_category);
        contentValues.put(C_COL_11, latitude);
        contentValues.put(C_COL_12, longitude);
        contentValues.put(C_COL_13, status);
        contentValues.put(C_COL_14, date_added);
        contentValues.put(C_COL_15, added_by);

        long result1 = db.insert(TABLE_NAME_P, null, contentValues);
        db.close();
        if(result1 ==  -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public List<String> getNearbyCategory()
    {
        List<String> bizz_cat =  new ArrayList<String>();

        String query = "SELECT * FROM " + TABLE_NAME_CATEGORY ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_cat.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return bizz_cat;
    }

    public List<BizzData> getAllSearchedDataWhere(String search, String selectedCity)
    {
        List<BizzData> modelList = new ArrayList<BizzData>() {} ;

        String wildcardSearchName = "%" + search + "%";
        String wildcardSearchCity = "%" + search + "%";

        //query
        //String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " LIKE ? OR " + C_COL_7 + " LIKE ? ";
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_7 + " = '" + selectedCity + "' AND " + C_COL_1 + " LIKE ? " ;

        String[] selectionArgs = new String[] {wildcardSearchName};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if(cursor.moveToFirst()){
            do{
                BizzData model = new BizzData();
                model.setBizz_name(cursor.getString(0));
                model.setLandmark(cursor.getString(4));
                model.setCelno(cursor.getString(3));

                modelList.add(model);
            }while(cursor.moveToNext());
        }
        return modelList;
    }

    public List<BizzData> getAllNearbyDataWhere(String category, double latNorth, double lonNorth,
                                                            double latSouth, double lonSouth,
                                                            double latEast, double lonEast,
                                                            double latWest, double lonWest)
    {
        List<BizzData> modelList = new ArrayList<BizzData>();

        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_9 + " = '" + category + "' "
                + " AND " + C_COL_11 + " BETWEEN '" + latSouth + "' AND '" + latNorth + "' AND " + C_COL_12 + " BETWEEN '" + lonSouth + "' AND '" + lonNorth + "'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{

                BizzData model = new BizzData();
                model.setBizz_name(cursor.getString(0));
                model.setLandmark(cursor.getString(4));
                model.setCelno(cursor.getString(3));

                modelList.add(model);
            }while(cursor.moveToNext());
        }
        return modelList;
    }

    public List<BizzData> getAllDataWhere(String sel_cat)
    {
        List<BizzData> modelList = new ArrayList<BizzData>();

        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_9 + " = '" + sel_cat + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                BizzData model = new BizzData();
                model.setBizz_name(cursor.getString(0));
                model.setLandmark(cursor.getString(4));
                model.setCelno(cursor.getString(3));

                modelList.add(model);
            }while(cursor.moveToNext());
        }
        return modelList;
    }

    public List<BizzData> getAllData()
    {
        List<BizzData> modelList = new ArrayList<BizzData>();

        String query = "SELECT * FROM " + TABLE_NAME_P;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                BizzData model = new BizzData();
                model.setBizz_name(cursor.getString(0));
                model.setLandmark(cursor.getString(4));
                model.setCelno(cursor.getString(3));

                modelList.add(model);
            }while(cursor.moveToNext());
        }
        return modelList;
    }

    public String getBizzName(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(0);
            } while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzCat(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(8);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzSubCat(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(9);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzTel(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(2);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzCell(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(3);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzLandmark(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(4);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzStreet(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(1);
            } while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzBrgy(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(6);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzCity(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(5);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzRegion(String store_name)
    {
        String bizz_name = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_name = cursor.getString(5);
            }while(cursor.moveToNext());
        }
        return bizz_name;
    }

    public String getBizzLat(String store_name)
    {
        String bizz_lat = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_lat = cursor.getString(10);
            }while(cursor.moveToNext());
        }
        return bizz_lat;
    }

    public String getBizzLon(String store_name)
    {
        String bizz_lon = null;
        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " = '" + store_name + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                bizz_lon = cursor.getString(11);
            }while(cursor.moveToNext());
        }
        return bizz_lon;
    }

    public List<BizzData> getAllDataSearch(String search)
    {
        List<BizzData> modelList = new ArrayList<BizzData>();

        String query = "SELECT * FROM " + TABLE_NAME_P + " WHERE " + C_COL_1 + " OR " + C_COL_5 + " = '" + search + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                BizzData model = new BizzData();
                model.setBizz_name(cursor.getString(0));
                model.setLandmark(cursor.getString(4));
                model.setCelno(cursor.getString(3));

                modelList.add(model);
            }while(cursor.moveToNext());
        }
        return modelList;
    }


    /**
     * A simple {@link Fragment} subclass.
     */
    public static class PostFragment extends Fragment {

        Spinner spPostCategory;
        RecyclerView rvPostIt;
        FloatingActionButton fabPostIt;

        //DBHelper
        MainDBHelper mainDBHelper;

        //for spinner
        List<String> postCategory;

        //mapkey
        public static final String KEY_CATEGORY = "post_category";

        //for post data
        private ArrayList<PostDetail> postList =  new ArrayList<PostDetail>();

        public PostFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView =  inflater.inflate(R.layout.fragment_post, container, false);

            getActivity().setTitle("Post it");

            mainDBHelper = new MainDBHelper(getActivity());

            postCategory = mainDBHelper.getNearbyCategory();

            //assigning components
            spPostCategory = (Spinner) rootView.findViewById(R.id.spPostCategory);
            rvPostIt = (RecyclerView) rootView.findViewById(R.id.rvPostIt);
            fabPostIt = (FloatingActionButton) rootView.findViewById(R.id.fabPostIt);

            fetchPosts();
            addPost();

            return rootView;
        }

        public void fetchPosts()
        {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, postCategory);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spPostCategory.setAdapter(adapter);

            spPostCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selectedCategory = spPostCategory.getSelectedItem().toString();

                    //Toast.makeText(getActivity(), selectedCategory, Toast.LENGTH_LONG).show();
                    fetchNearbyPost(selectedCategory);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public void fetchNearbyPost(final String category)
        {

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvPostIt.setLayoutManager(llm);

            final PostDetailRVAdapter postDetailRVAdapter = new PostDetailRVAdapter(getContext(), postList);
            rvPostIt.setAdapter(postDetailRVAdapter);

            String fetchLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/establishment/getPostIt";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLink, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {

                    try
                    {
                        if(response.length()>0)
                        {
                            JSONArray postRecords = null;
                            JSONObject obj = new JSONObject(response);
                            postRecords = obj.getJSONArray("post_list");

                            postList.clear();
                            for(int i = 0; i<postRecords.length(); i++)
                            {

                                JSONObject jsonObject = postRecords.getJSONObject(i);

                                PostDetail postDetail = new PostDetail();
                                if(!jsonObject.isNull("bizz_name"))
                                {
                                    postDetail.setPostPlaceName(jsonObject.getString("bizz_name"));
                                }
                                if(!jsonObject.isNull("message"))
                                {
                                    postDetail.setPostMessage(jsonObject.getString("message"));
                                }
                                if(!jsonObject.isNull("time_stamp"))
                                {
                                    postDetail.setPostDate(jsonObject.getString("time_stamp"));
                                }
                                postList.add(i, postDetail);
                            }
                            postDetailRVAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "No data fetched. Check your internet connection.", Toast.LENGTH_LONG).show();
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

                    map.put(KEY_CATEGORY, category);

                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

        }

        public void addPost()
        {

            fabPostIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    startActivity(intent);
                }
            });

        }

    }
}
