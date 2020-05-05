package com.kip.gillz.meter_readings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kip.gillz.meter_readings.utils.Tools;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewClient extends AppCompatActivity {
    //Declaring views
    private EditText editTextfName;
    private EditText editTextlName;    ;
    private EditText editTextphone;
    private EditText editTextmeterno;
    private EditText editTextEmail;
    private Button reg;

    //This is our root url
    public static final String ROOT_URL = "https://bahaconstruction.co.ke/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.pink_900);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
/*


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        //Initializing Views
        editTextfName = (EditText) findViewById(R.id.firstname);
        editTextlName = (EditText) findViewById(R.id.lastname);
        editTextphone = (EditText) findViewById(R.id.phone);
        editTextEmail = (EditText) findViewById(R.id.mail);
        editTextmeterno = findViewById(R.id.meternumber);

        reg= findViewById(R.id.bt_close);
        reg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
               Validate();

            }
        });
    }

    private void insertUser(){
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        RegisterAPI api = adapter.create(RegisterAPI.class);

        //Defining the method insertuser of our interface
        api.insertUser(

                //Passing the values by getting it from editTexts
                editTextfName.getText().toString(),
                editTextlName.getText().toString(),
                editTextEmail.getText().toString(),
                editTextphone.getText().toString(),
                editTextmeterno.getText().toString(),


                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                        new LovelyStandardDialog(NewClient.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColorRes(R.color.cyan_700)
                                .setIcon(R.drawable.ic_help_outline_black_24dp)
                                .setTitle("Server Response").setTitleGravity(Gravity.CENTER)
                                .setMessage(output).setMessageGravity(Gravity.CENTER)
                                .setCancelable(false)
                                .setPositiveButton("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(NewClient.this, error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private boolean internetAvailable() {
        boolean haveWiFi = false;
        boolean haveMobile = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WiFi")) {
                if (networkInfo.isConnected()) {
                    haveWiFi = true;
                }
            }
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (networkInfo.isConnected()) {
                    haveMobile = true;
                }
            }
        }
        return haveWiFi || haveMobile;
    }
    public void Validate(){


            String fname,lname,mail,phone,meter;
            fname=  editTextfName.getText().toString().trim();
            lname=editTextlName.getText().toString().trim();
            mail= editTextEmail.getText().toString().trim();
            phone=editTextphone.getText().toString().trim();
            meter= editTextmeterno.getText().toString().trim();


            if (TextUtils.isEmpty(fname)&&TextUtils.isEmpty(lname)&&TextUtils.isEmpty(mail)
                    && TextUtils.isEmpty(phone)&&TextUtils.isEmpty(meter)) {

                editTextfName.setError("First name Required");
                editTextlName.setError("last name Required");
                editTextEmail.setError(" Required");
                editTextphone.setError("Required");
                editTextmeterno.setError(" Required");
                return;
            }

            else if (TextUtils.isEmpty(fname))
            {
                editTextfName.setError("Please Enter Client's First name");
                return;
            }
            else if (TextUtils.isEmpty(lname))
            {
                editTextlName.setError("Please Enter Client's Last name");
                return;
            }
            else if (TextUtils.isEmpty(mail))
            {
                editTextEmail.setError("Please Enter Client's Email");
                return;
            }
            else if (TextUtils.isEmpty(phone))
            {
                editTextphone.setError("Please Enter Client's Phone Number");
                return;
            }
            else if (TextUtils.isEmpty(meter))
            {
                editTextmeterno.setError("Please Enter Meter Number");
                return;
            }
            else if (!(meter.length() ==10))
            {
                editTextmeterno.setError("Meter Number Must be Exactly 10 didits");
                return;
            }
            else if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                editTextEmail.setError("Enter a Valid Email");
                return;
            }

            else if (!fname.matches("[a-zA-Z]+"))
            {
                editTextfName.setError("Enter a Valid Client's First Name");
                return;
            }
            else if (!lname.matches("[a-zA-Z]+"))
            {
                editTextlName.setError("Enter a Valid Client's Last Name");
                return;
            }



            else
            {
                editTextfName.setError(null);
                editTextlName.setError(null);
                editTextEmail.setError(null);
                editTextphone.setError(null);
                editTextmeterno.setError(null);

                if (internetAvailable())
                {

                    new LovelyStandardDialog(NewClient.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColorRes(R.color.cyan_700)
                            .setIcon(R.drawable.ic_help_outline_black_24dp)
                            .setTitle("You are About to Submit the following client Details:").setTitleGravity(Gravity.CENTER)
                            .setMessage("First Name:\t\t"+fname+"\n Last name:\t\t"+lname+"\n Email:\t\t"+mail+"\n Phone no.:\t\t"+phone+"\nMeter no:\t\t"+meter+"\n")
                            .setCancelable(false)
                            .setPositiveButton("Submit", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    insertUser();

                                }
                            }).setNegativeButton("Dismis", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                            .show();


                }
                else {
                    new LovelyStandardDialog(NewClient.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColorRes(R.color.cyan_700)
                            .setIcon(R.drawable.ic_help_outline_black_24dp)
                            .setTitle("No Internet").setTitleGravity(Gravity.CENTER)
                            .setMessage("Please Check Your Internet Connection and again").setMessageGravity(Gravity.CENTER)
                            .setCancelable(false)
                            .setPositiveButton("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }



        }
    }

}
*/
