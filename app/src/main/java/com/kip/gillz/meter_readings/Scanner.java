package com.kip.gillz.meter_readings;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class Scanner extends AppCompatActivity {
    private Context mCtx;
    SurfaceView cameraView;
    TextView textView, texviewname, texviewmno;
    CameraSource cameraSource;
    TextRecognizer textRecognizer;
    final int RequestCameraPermissionID = 1001;
    String readings;
    String meterno;
    String Username;
    //String name;
    String Fname;
    String Lname;
    Button btnSend;
    private AppCompatButton show_dialog;
    private ProgressBar progress_bar;
    UserSessionManager session;
    public static final String ROOT_URL = "https://tal.co.ke/";
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        btnSend = findViewById(R.id.btnSend);
        cameraView = findViewById(R.id.surface_view);
        textView = findViewById(R.id.text_view);
        texviewname = findViewById(R.id.pushedname);
        texviewmno = findViewById(R.id.pushedmeterno);
        // Session class instance
        session = new UserSessionManager(getApplicationContext());
        if(session.checkLogin())
            finish();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // get username
        Username = user.get(UserSessionManager.KEY_ADMIN_USERNAME);
        // todo: get value from previouas Avtivity
        String meternoHolder = getIntent().getStringExtra("meternumber");
        String NameHolder = getIntent().getStringExtra("firstname");



        btnSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                try {
                    if (ActivityCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


        meterno = meternoHolder;
        Fname = NameHolder;

        texviewname.setText(NameHolder+ " ." );
        texviewmno.setText(meternoHolder);
        Maintask();
    }
    public void XXX(){
        Intent intent= new Intent(Scanner.this, Scanner.class);
        // sent value to the next activity
        //intent.putExtra("fullname", name );
        intent.putExtra("meternumber", meterno );
        intent.putExtra("firstname", Fname );
        startActivity(intent);
        finish();
    }
    public void Maintask() {

        textRecognizer = new TextRecognizer.Builder(getApplication()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("Scanner", "Not ready try again later");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Scanner.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
            //TODO: Captrure text blocks
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturetext();
                }
            });
        }
    }
    public void capturetext() {

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> itemns = detections.getDetectedItems();
                if (itemns.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < itemns.size(); i++) {
                                TextBlock item = itemns.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            readings = stringBuilder.toString();
                            //TODO: to temporarily stop the loop
                            cameraSource.stop();
                            if (readings != null) {
                                if (readings.trim().length() == 8 && readings.trim().matches("[0-9]+")) {
                                    SuccessCustomDialog();
                                    cameraSource.stop();
                                } else {
                                    FailedCustomDialog();
                                }
                            }

                        }

                    });
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
    private void SuccessCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.successscan);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_content)).setText(readings);

        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetAvailable()) {
                    /*ScannerBTask bTask = new ScannerBTask(Scanner.this);
                    bTask.execute(Fname,Lname,meterno,readings,Username);*/
                    insertreadings();
                    dialog.dismiss();

                } else {
                    new LovelyStandardDialog(Scanner.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColorRes(R.color.cyan_700)
                            .setIcon(R.drawable.ic_help_outline_black_24dp)
                            .setTitle("No Internet").setTitleGravity(Gravity.CENTER)
                            .setMessage("Please Check Your Internet Connection and try again").setMessageGravity(Gravity.CENTER)
                            .setCancelable(false)
                            .setPositiveButton("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }
            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Your Scan result was Dismissed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                XXX();

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void FailedCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.failedscan);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_content)).setText(readings);
        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                XXX();

            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void insertreadings(){
        // Session class instance
        session = new UserSessionManager(getApplicationContext());

        if(session.checkLogin())
            finish();


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get username
        Username = user.get(UserSessionManager.KEY_ADMIN_USERNAME);


        // get email
        String email = user.get(UserSessionManager.KEY_EMAIL);


        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(Scanner.this);
        progressDoalog.setMessage("Please Wait....");
        progressDoalog.setTitle("Submitting Your Scan Readings");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();

        //Creating object for our interface
        Submitreadings api = adapter.create(Submitreadings.class);

        //Defining the method insertuser of our interface
        api.inserreadings(
                Fname,
                Lname,
                meterno,
                readings,
                Username,

                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        progressDoalog.dismiss();

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
                       if (output.equals("Meter Readings Captured Successfully"))
                        {
                            //Displaying the output as a toast
                            new LovelyStandardDialog(Scanner.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green_500)
                                    .setTopTitle("Server Response")
                                    .setTitle("SUCCESS!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                                    .setButtonsColorRes(R.color.green_500)
                                    .setIcon(R.drawable.ic_done_black_24dp)
                                    .setMessage(output).setMessageGravity(Gravity.CENTER)
                                    .setCancelable(false)
                                    .setPositiveButton("Close", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .show();
                        }
                        else {
                            new LovelyStandardDialog(Scanner.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.red_400)
                                    .setButtonsColorRes(R.color.red_400)
                                    .setIcon(R.drawable.ic_close_black_24dp)
                                    .setTopTitle("Server Response")
                                    .setTitle("FAILED!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                                    .setMessage(output).setMessageGravity(Gravity.CENTER)
                                    .setCancelable(false)
                                    .setPositiveButton("Try Again", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .show();

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        progressDoalog.dismiss();

                        Toast.makeText(Scanner.this, error.toString(),Toast.LENGTH_LONG).show();
                        // CustomToast();
                    }
                }
        );
    }
}