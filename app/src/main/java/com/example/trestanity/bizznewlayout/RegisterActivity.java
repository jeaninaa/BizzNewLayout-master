package com.example.trestanity.bizznewlayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegFirstName, etRegMiddleName, etRegLastName,
            etRegMobileNumber, etRegEmail, etRegPassword, etRegConfirmPassword;

    RadioGroup rgUserType, rgGender;
    RadioButton rbUserType, rbGender;

    Button btnSaveRegister;

    //mapkeys
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_MIDDLENAME = "middlename";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_MOBILE = "mobile_number";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    ArrayList<String> errorList = new ArrayList<String>();
    String eUser = "rbUserType";
    String eFName = "firstname";
    String eMName = "middlename";
    String eLName = "lastname";
    String eGender = "rbGender";
    String eMNumber = "mobilenumber";
    String eEmail = "email";
    String ePassword = "password";


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                promptExitRegistration();

                //this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setID();

       /* if(errorIndex > 0){
            btnSaveRegister.setEnabled(false);
        }*/

        btnSaveRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                registerAccount();

            }
        });

    }

    public void setID()
    {

        etRegFirstName = (EditText) findViewById(R.id.etRegFirstName);
        etRegMiddleName = (EditText) findViewById(R.id.etRegMiddleName);
        etRegLastName = (EditText) findViewById(R.id.etRegLastName);
        etRegMobileNumber = (EditText) findViewById(R.id.etRegMobileNumber);
        etRegEmail = (EditText) findViewById(R.id.etRegEmail);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        etRegConfirmPassword = (EditText) findViewById(R.id.etRegConfirmPassword);

        rgUserType = (RadioGroup) findViewById(R.id.rgUserType);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);

        btnSaveRegister = (Button) findViewById(R.id.btnSaveRegister);
    }

    public void registerAccount()
    {

        String firstname, middlename, lastname,
                mobilenumber, email, password, confirmpassword;

        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbUserType = (RadioButton)group.findViewById(checkedId);
            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbGender = (RadioButton)group.findViewById(checkedId);
            }
        });

        int userCheck = rgUserType.getCheckedRadioButtonId();
        int genderCheck = rgGender.getCheckedRadioButtonId();

        if (userCheck == -1){
            errorList.add(eUser);
        }

        if(genderCheck == -1){
            errorList.add(eGender);
        }

        firstname = etRegFirstName.getText().toString().trim();
        if (firstname.isEmpty()){
            errorList.add(eFName);
        } else
            firstname = firstname.substring(0, 1).toUpperCase() + firstname.substring(1);

        middlename = etRegMiddleName.getText().toString().trim();
        if (middlename.isEmpty()){
            errorList.add(eMName);
        } else
            middlename = middlename.substring(0, 1).toUpperCase() + middlename.substring(1);

        lastname = etRegLastName.getText().toString().trim();
        if (lastname.isEmpty()){
            errorList.add(eLName);
        } else
            lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1);

        mobilenumber = etRegMobileNumber.getText().toString().trim();
        for (int i=0; i<mobilenumber.length()-1; i++){
            if(Character.isLetter(mobilenumber.charAt(i))){
                //Toast.makeText(getApplicationContext(), "Invalid mobile number input", Toast.LENGTH_LONG).show();
                errorList.add("eMNumber");
            }
        }

        email = etRegEmail.getText().toString().trim();








        password = etRegPassword.getText().toString().trim();
        confirmpassword = etRegConfirmPassword.getText().toString().trim();

        RadioButton rbUserTypeSelected = (RadioButton) rgUserType.findViewById(rgUserType.getCheckedRadioButtonId());
        RadioButton rbGenderSelected = (RadioButton) rgGender.findViewById(rgGender.getCheckedRadioButtonId());

        //password match checker
        if(password.equals(confirmpassword))
        {
            //Toast.makeText(getApplicationContext(), "Password match", Toast.LENGTH_LONG).show();
            volleyRegister(rbUserTypeSelected.getText(), firstname, middlename, lastname, rbGenderSelected.getText(), mobilenumber, email, password);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_LONG).show();
            errorList.add(ePassword);
        }


    }

    public void volleyRegister(final CharSequence cUserType, final String firstName, final String middleName, final String lastName, final CharSequence cGender, final String mobileNumber, final String email, final String password)
    {

        final String userType = String.valueOf(cUserType);
        final String gender = String.valueOf(cGender);

        String registerLink = ""; //insert link

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){



            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(KEY_USER_TYPE, userType);
                map.put(KEY_FIRSTNAME, firstName);
                map.put(KEY_MIDDLENAME, middleName);
                map.put(KEY_LASTNAME, lastName);
                map.put(KEY_GENDER, gender);
                map.put(KEY_MOBILE, mobileNumber);
                map.put(KEY_EMAIL, email);
                map.put(KEY_PASSWORD, password);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

        Toast.makeText(getApplicationContext(), userType
                + " \n " + firstName
                + " \n " + middleName
                + " \n " + lastName
                + " \n " + gender
                + " \n " + mobileNumber
                + " \n " + email
                + " \n " + password
                ,Toast.LENGTH_LONG).show();

    }


    public void promptExitRegistration()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Warning");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to cancel Registration?")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
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

}
