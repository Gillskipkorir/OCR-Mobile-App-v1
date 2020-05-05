package com.kip.gillz.meter_readings;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

public class Login extends AppCompatActivity {

    AlertDialog.Builder builder;
    EditText username,password;
    Button btnl;
    // User Session Manager Class
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Session Manager
        session = new UserSessionManager(getApplicationContext());

        btnl= findViewById(R.id.btnlogin);

        username= findViewById(R.id.usr);
        password= findViewById(R.id.psw);
        btnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Username,Password;
                Username= username.getText().toString().trim();
                Password= password.getText().toString().trim();


                if (TextUtils.isEmpty(Username)&&TextUtils.isEmpty(Password)) {

                    username.setError("Username is Required");
                    password.setError("Your Password Required");

                    return;
                }

                else if (TextUtils.isEmpty(Username))
                {
                    username.setError("Please Enter Your Username");
                    return;
                }
                else if (TextUtils.isEmpty(Password))
                {
                    password.setError("Please Enter Your password");
                    return;
                }

                else {
                    password.setError(null);
                    username.setError(null);
                    if (internetAvailable())
                    {
                        //saving username on the session
                        session.createUserLoginSession(Username);

                        LoginPageBtask loginPageBtask = new LoginPageBtask(Login.this);
                        loginPageBtask.execute(username.getText().toString(), password.getText().toString());
                    }

                    else
                    {

                        new LovelyStandardDialog(Login.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
        });

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

}
