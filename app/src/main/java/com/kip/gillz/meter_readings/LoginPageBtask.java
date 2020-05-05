package com.kip.gillz.meter_readings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoginPageBtask extends AsyncTask<String,Void,String> {
    Context ctx;
    ProgressDialog progressDialog;
    Activity activity;
    AlertDialog.Builder builder;


    String upload_url="https://tal.co.ke/tal/tlogin.php";

    String Username="Username";
    String password="password";

    // User Session Manager Class
   UserSessionManager session;
    public LoginPageBtask(Context ctx){
        this.ctx=ctx;
        activity=(Activity)ctx;
    }
    @Override
    protected String doInBackground(String... params) {

        String field=params[0];
        String field2=params[1];

        HashMap<String, String> data1 = new HashMap<>();

        data1.put(Username,field);
        data1.put(password,field2);

        try{
            URL url=new URL(upload_url);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(10000);
            OutputStream outputStream=httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(getPostDataString(data1));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder=new StringBuilder();
            String line="";
            while((line=bufferedReader.readLine())!= null){
                stringBuilder.append(line+"\n");

            }
            httpURLConnection.disconnect();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString().trim();


        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }catch (IOException e){

            e.printStackTrace();
        }


        return null;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result= new StringBuilder();
        boolean first=true;
        for (Map.Entry<String,String>entry:params.entrySet()){
            if (first){
                first=false;
            }else{
                result.append("&");

            }
            result.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }

        return result.toString();

    }


    @Override
    protected void onPreExecute() {
        builder=new AlertDialog.Builder(ctx);
        builder=new AlertDialog.Builder(activity);
        progressDialog=new ProgressDialog(ctx);
        progressDialog.setTitle("Verifying your Login Credentials");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String json) {
        if (json == null) {

            progressDialog.dismiss();
            new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.red_400)
                    .setButtonsColorRes(R.color.red_400)
                    .setIcon(R.drawable.ic_lock_outline_black_24dp)
                    .setTitle("Error!!!!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                    .setMessage("Login Failed.").setMessageGravity(Gravity.CENTER)
                    .setCancelable(false)
                    .setPositiveButton("Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
       }
        if (json != null) {
            try {
             //Toast.makeText(ctx,json,Toast.LENGTH_LONG).show();

                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                JSONObject obj = jsonArray.getJSONObject(0);
                String code = obj.getString("code");
                String message = obj.getString("message");

                if (code.equals("login_true")) {

                    // Session Manager
                    //session = new UserSessionManager(ctx);
                   // session.createUserLoginSession(Username);
                   Intent intent = new Intent(ctx,Home.class);
                   ctx.startActivity(intent);


                }
                if (code.equals("login_suspend")) {

                    session = new UserSessionManager(ctx);
                    //Clear saved session data
                    session.Clearall();

                    new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.yellow_600)
                            .setButtonsColorRes(R.color.yellow_600)
                            .setIcon(R.drawable.ic_lock_outline_black_24dp)
                            .setTitle("Access Denied").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                            .setMessage(message).setMessageGravity(Gravity.CENTER)
                            .setCancelable(false)
                            .setPositiveButton("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();


                }
                else if (code.equals("login_false")) {

                        new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.red_400)
                            .setButtonsColorRes(R.color.red_400)
                            .setIcon(R.drawable.ic_lock_outline_black_24dp)
                            .setTitle("Failed!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                            .setMessage(message).setMessageGravity(Gravity.CENTER)
                            .setCancelable(false)
                            .setPositiveButton("Try Again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();

                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
