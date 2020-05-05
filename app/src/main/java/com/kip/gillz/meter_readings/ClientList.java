package com.kip.gillz.meter_readings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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


import com.google.android.gms.common.data.DataHolder;
import com.kip.gillz.meter_readings.adapter.AdapterListAnimation;
import com.kip.gillz.meter_readings.data.DataGenerator;
import com.kip.gillz.meter_readings.model.People;
import com.kip.gillz.meter_readings.utils.ItemAnimation;
import com.kip.gillz.meter_readings.utils.Tools;
import com.kip.gillz.meter_readings.utils.ViewAnimation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ClientList extends AppCompatActivity {
    private View parent_view;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private AdapterListAnimation mAdapter;
    private List<People> items = new ArrayList<>();

    CAdapter cAdapter;
    ArrayList<String> meter_no;

    private int animation_type = ItemAnimation.BOTTOM_UP;
    private View lyt_mic;
    private View lyt_call;
    private boolean rotate = false;
    private View back_drop;
    private EditText editTextfName;
    private EditText editTextphone;
    private EditText editTextmeterno;
    private EditText editTextplot;
    private EditText editTextremark;
    private TextView textviewinst;
    private EditText editTextlName;
    EditText search;
    String Username;
    String fname,lname,phone,meter,inst_date,plot,remarkk;

    private ImageButton btdismis;
    private Button reg;
    public static final String ROOT_URL = "https://tal.co.ke/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);
        parent_view = findViewById(android.R.id.content);
        back_drop = findViewById(R.id.back_drop);

        //search = findViewById(R.id.search);
        //new
        meter_no = new ArrayList<>();
        //new
        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin())
            finish();

        session = new UserSessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        Username = user.get(UserSessionManager.KEY_ADMIN_USERNAME);


        initToolbar();
       //search

        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
*/


        setAdapter();

        final FloatingActionButton addnewclient =  findViewById(R.id.fab_addclient);
        final FloatingActionButton info =  findViewById(R.id.fab_info);
        final FloatingActionButton fab_add = findViewById(R.id.fab_add);
        final FloatingActionButton refresh = findViewById(R.id.fab_refesh);
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
        addnewclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();

            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ClientList.this);
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

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAdapter();

            }
        });
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
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registered Clients");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }
    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        items = DataGenerator.getPeopleData(this);

        animation_type = ItemAnimation.FADE_IN;
        setAdapter();

        //showSingleChoiceDialog();
    }
    private void setAdapter() {
        animation_type = ItemAnimation.FADE_IN;
        Clientlistbacktask vaccdrugb =new Clientlistbacktask(this);
        vaccdrugb.execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_animation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //finish();
                break;
            case R.id.action_refresh:
                setAdapter();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private static final String[] ANIMATION_TYPE = new String[]{
            "Bottom Up", "Fade In", "Left to Right", "Right to Left"
    };
    public void showCustomDialog() {
        final Dialog dialog = new Dialog(ClientList.this);
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


                    final Dialog dialog = new Dialog(ClientList.this);
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
                                new LovelyStandardDialog(ClientList.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
        progressDoalog = new ProgressDialog(ClientList.this);
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
                            new LovelyStandardDialog(ClientList.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
                            new LovelyStandardDialog(ClientList.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
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


   /* private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<String> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (String s : names) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }*/
}
