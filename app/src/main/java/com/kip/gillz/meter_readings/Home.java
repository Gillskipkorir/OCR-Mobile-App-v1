package com.kip.gillz.meter_readings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kip.gillz.meter_readings.utils.Tools;
import com.kip.gillz.meter_readings.utils.ViewAnimation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class Home extends AppCompatActivity {
    UserSessionManager session;
    SharedPreferences prf;
    private View parent_view;
    private View back_drop;
    private boolean rotate = false;
    private View lyt_mic;
    private View lyt_call;
    CardView cardView1,cardView2;
    private EditText editTextfName;
    private EditText editTextplot;    ;
    private EditText editTextphone;
    private EditText editTextmeterno;
    private EditText editTextremark;
    private TextView textviewinst;
    private ImageButton btdismis;
    private EditText editTextlName;
    String fname,lname,phone,meter,inst_date,plot,remarkk;

    String Username;
    public static final String ROOT_URL = "https://tal.co.ke/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent_view = findViewById(android.R.id.content);
        back_drop = findViewById(R.id.back_drop);
        cardView1 = findViewById(R.id.cardscan);
        cardView2 = findViewById(R.id.cardreg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome To Tal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);

        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin()) {
            finish();
        }

        // checks if there is an active session
        else{

            session = new UserSessionManager(getApplicationContext());

            HashMap<String, String> user = session.getUserDetails();
            Username = user.get(UserSessionManager.KEY_ADMIN_USERNAME);
            ctoast("Welcome  "+Username+"  ");
            final FloatingActionButton fab_mic =  findViewById(R.id.fab_mic);
            final FloatingActionButton fab_call = findViewById(R.id.fab_call);
            final FloatingActionButton fab_add =  findViewById(R.id.fab_add);
            lyt_mic = findViewById(R.id.lyt_mic);
            lyt_call = findViewById(R.id.lyt_call);
            ViewAnimation.initShowOut(lyt_mic);
            ViewAnimation.initShowOut(lyt_call);
            back_drop.setVisibility(View.GONE);

            fab_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFabMode(v);
                }
            });

            back_drop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFabMode(fab_add);
                }
            });

            fab_mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Help", Toast.LENGTH_SHORT).show();
                }
            });
            fab_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle(Username+",  are you Sure you want to Logout?");
                    builder.setIcon(R.drawable.ic_lock_outline_black_24dp);
                    builder.setPositiveButton("Yes Log Me Out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();
                            //end user Session
                            session.logoutUser();
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();


                    ;
                }
            });

            initComponent();
        }

    }
    private void toggleFabMode (View v){
        rotate = ViewAnimation.rotateFab(v, !rotate);
        if (rotate) {
            ViewAnimation.showIn(lyt_mic);
            ViewAnimation.showIn(lyt_call);
            back_drop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lyt_mic);
            ViewAnimation.showOut(lyt_call);
            back_drop.setVisibility(View.GONE);
        }
    }
    private void initComponent () {


        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (internetAvailable())
                {
                    Intent intent = new Intent(Home.this,ClientList.class);
                    startActivity(intent);
                }
                else
                {

                    new LovelyStandardDialog(Home.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent = new Intent(Home.this,NewClient.class);
                startActivity(intent);*/

                showCustomDialog();


            }
        });

    }
    @Override
    public void onBackPressed() {
        doExitApp();
    }
    private long exitTime = 0;
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_user, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            // finish();

        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            ctoast("You are Logged in as  "+Username+"  ");

        }
        return super.onOptionsItemSelected(item);
    }
    public void showCustomDialog() {
        final Dialog dialog = new Dialog(Home.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.register);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        editTextfName=dialog.findViewById(R.id.names);
        editTextlName=dialog.findViewById(R.id.lname);
        editTextphone=dialog.findViewById(R.id.phone);
        editTextmeterno=dialog.findViewById(R.id.meterno);
        textviewinst=dialog.findViewById(R.id.isnt);
        editTextplot=dialog.findViewById(R.id.plotno);
        editTextremark=dialog.findViewById(R.id.remarks);

        btdismis= dialog.findViewById(R.id.bt_dismiss);
        //gender=findViewById(R.id.sex);
        btdismis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textviewinst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cur_calender = Calendar.getInstance();
                DatePickerDialog datePicker = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                long date_ship_millis = calendar.getTimeInMillis();
                                ((TextView) dialog.findViewById(R.id.isnt)).setText(Tools.getFormattedDateSimple(date_ship_millis));
                            }
                        },
                        cur_calender.get(Calendar.YEAR),
                        cur_calender.get(Calendar.MONTH),
                        cur_calender.get(Calendar.DAY_OF_MONTH)


                );
                //set dark light
                datePicker.setThemeDark(true);
                datePicker.setTitle("Date From");
                datePicker.setCancelColor(getResources().getColor(R.color.green_400));
                datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
                datePicker.setMaxDate(cur_calender);
                cur_calender.add(Calendar.DAY_OF_MONTH,-30);
                datePicker.setMinDate(cur_calender);

                datePicker.show(getFragmentManager(), "Datepickerdialog");


            }
        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                fname=  editTextfName.getText().toString().trim();
                lname=  editTextlName.getText().toString().trim();
                phone=editTextphone.getText().toString().trim();
                meter= editTextmeterno.getText().toString().trim();
                inst_date= textviewinst.getText().toString().trim();
                plot= editTextplot.getText().toString().trim();
                remarkk= editTextremark.getText().toString().trim();

                if (TextUtils.isEmpty(fname)&&TextUtils.isEmpty(lname)&&TextUtils.isEmpty(meter)) {
                    editTextfName.setError("First Name is Required");
                    editTextlName.setError("Last Name is Required");
                    editTextmeterno.setError("Meter Number is Required");
                    return;
                }

                else if (TextUtils.isEmpty(fname))
                {
                    editTextfName.setError("Please Enter Client's First Name");
                    return;
                }

                else if (TextUtils.isEmpty(lname))
                {
                    editTextlName.setError("Please Enter Client's Last Name");
                    return;
                }

                else if (TextUtils.isEmpty(meter))
                {
                    editTextmeterno.setError("Please Enter Meter Number");
                    return;
                }
                else if (!(meter.length() ==12))
                {
                    editTextmeterno.setError("Meter Number Must be Exactly 12 didits");
                    errortoast("Meter Number Must be Exactly 12 didits");

                    return;
                }

                else if (inst_date.equals("Installation Date"))
                {
                    errortoast("Please Select Installation date");
                    return;
                }

                else if (!fname.matches("[a-zA-Z]+"))
                {
                    editTextfName.setError("Enter a Valid Client's First Name");
                    return;
                }

                else if (!lname.matches("[a-zA-Z]+"))
                {
                    editTextlName.setError("Enter a Valid Client's First Name");
                    return;
                }

                else
                {
                    editTextfName.setError(null);
                    editTextmeterno.setError(null);


                    final Dialog dialog = new Dialog(Home.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    dialog.setContentView(R.layout.confirm);
                    dialog.setCancelable(false);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    ((TextView) dialog.findViewById(R.id.firstname)).setText(fname+" "+lname);
                    ((TextView) dialog.findViewById(R.id.phone)).setText(phone);
                    ((TextView) dialog.findViewById(R.id.meterno)).setText(meter);
                    ((TextView) dialog.findViewById(R.id.ins)).setText(inst_date);
                    ((TextView) dialog.findViewById(R.id.plott)).setText(plot);
                    ((TextView) dialog.findViewById(R.id.rem)).setText(remarkk);

                    ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (internetAvailable())
                            {
                                insertUser();
                                dialog.dismiss();



                            }
                            else
                            {
                                new LovelyStandardDialog(Home.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.colorPrimary)
                                        .setButtonsColorRes(R.color.cyan_700)
                                        .setIcon(R.drawable.ic_help_outline_black_24dp)
                                        .setTitle("No Internet").setTitleGravity(Gravity.CENTER)
                                        .setMessage("Please Check Your Internet Connection and try again").setMessageGravity(Gravity.CENTER)
                                        .setCancelable(false)
                                        .setPositiveButton("Close", new View.OnClickListener() {
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
                            Toast.makeText(getApplicationContext(), "Client "+fname+" details was Dismissed", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);


                }

            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void insertUser(){
        session = new UserSessionManager(getApplicationContext());

        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(Home.this);
        progressDoalog.setMessage("Please Wait....");
        progressDoalog.setTitle("Registering new Client");
        progressDoalog.setCancelable(false);
        progressDoalog.setIndeterminate(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();


        //Creating object for our interface
        RegisterAPI api = adapter.create(RegisterAPI.class);
        //Defining the method insertuser of our interface


        String full= fname+" "+lname;
        api.insertUser(
                //Passing the values by getting it from editTexts
                full,
                editTextphone.getText().toString().trim(),
                editTextmeterno.getText().toString().trim(),
                inst_date,
                editTextplot.getText().toString().trim(),
                editTextremark.getText().toString().trim(),
                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {

                        progressDoalog.dismiss();

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
                        if (output.equals("New Client Successfully Registered"))
                        {
                            //Displaying the output as a toast
                            new LovelyStandardDialog(Home.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
                            new LovelyStandardDialog(Home.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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

                        //If any error occured displaying the error as toast
                        //Toast.makeText(Home.this, error.toString(),Toast.LENGTH_LONG).show();
                        errortoast(error.toString());

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
    public void ctoast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.customtoast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_client_black_24dp);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    public void errortoast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.errortoast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_close);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
