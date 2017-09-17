package com.example.trestanity.bizznewlayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginUsername, etLoginPassword;
    Button btnLogin, btnRegister;

    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";

    private String username;
    private String password;

    ProgressDialog progressDialog;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        assignID();

        //Toast.makeText(getApplicationContext(),  "User login status: " + sessionManager.isLoggedIn(), Toast.LENGTH_LONG).show();

        if(sessionManager.isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                userLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void assignID()
    {
        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    public void userLogin()
    {
        //Progress dialog
        progressDialog = ProgressDialog.show(LoginActivity.this,"Authenticating","Please wait...",true);
        progressDialog.setCancelable(true);

        String loginLink = "http://hris.ardentnetworks.com.ph/mobile_bizz/index.php/retrieve/login";

        username = etLoginUsername.getText().toString().trim();
        password = etLoginPassword.getText().toString().trim();

        // Check if username, password is filled
        if(username.trim().length() > 0 && password.trim().length() > 0){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginLink, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response.trim().equals("success"))
                    {
                        progressDialog.dismiss();
                        sessionManager.createLoginSession(username, password);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error, invalid email or password" ,Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<String, String>();

                    map.put(KEY_USERNAME, username);
                    map.put(KEY_PASSWORD, password);

                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

            // For testing puspose username, password is checked with sample data
            // username = test
            // password = test
//            if((username.equals("test") && password.equals("test"))||(username.equals("Sample1") && password.equals("samplelangto123"))){

            // Creating user login session
            // For testing i am stroing name, email as follow
            // Use user real data
//                sessionManager.createLoginSession(username.toString(), password.toString());
//
//                // Staring MainActivity
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//                finish();



//            }

//            else{
//                // username / password doesn't match
//                //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
//                Toast.makeText(getApplicationContext(), "Login failed.. Username/Password is incorrect", Toast.LENGTH_LONG).show();
//            }
        }
        else{
            // user didn't entered username or password
            // Show alert asking him to enter the details
            //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
            Toast.makeText(getApplicationContext(), "Login failed. Please enter username and password.", Toast.LENGTH_LONG).show();

        }

    }

}
