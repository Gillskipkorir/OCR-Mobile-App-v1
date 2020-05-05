package com.kip.gillz.meter_readings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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


public class ScannerBTask extends AsyncTask<String,Void,String> {
Context ctx;
ProgressDialog progressDialog;
Activity activity;
AlertDialog.Builder builder;
String upload_url="https://tal.co.ke/tal/scanresult.php";
String Fname="Fname";
String Lname="Lname";
String meterno="meterno";
String readings="readings";
String Username="Username";
public ScannerBTask(Context ctx){
    this.ctx=ctx;
    activity=(Activity)ctx;
}
@Override
protected String doInBackground(String... params) {
    String field1=params[0];
    String field2=params[1];
    String field3=params[2];
    String field4=params[3];
    String field5=params[4];
    HashMap<String, String> data1 = new HashMap<>();
    data1.put(Fname,field1);
    data1.put(Lname,field2);
    data1.put(meterno,field3);
    data1.put(readings,field4);
    data1.put(Username,field5);
    try{
        URL url=new URL(upload_url);
        HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setConnectTimeout(5000);
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
            Thread.sleep(5000);
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
    progressDialog.setTitle("Please wait");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setMessage("Submitting your Readings....");
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
        builder.setTitle("Something went Wrong...");
        builder.setMessage("Sorry! An Internal Error Occurred..\n Please try again later.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();


        new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.red_500)
                .setButtonsColorRes(R.color.red_500)
                .setIcon(R.drawable.ic_close_black_24dp)
                .setTopTitle("Something went Wrong")
                .setTitle("Sorry!").setTitleGravity(Gravity.CENTER)
                .setMessage(" An Internal Error Occurred..\n Please try again later.").setMessageGravity(Gravity.CENTER)
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
            progressDialog.dismiss();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject obj = jsonArray.getJSONObject(0);
            String code = obj.getString("code");
            String message = obj.getString("message");
            if (code.equals("upload_true")) {
                new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green_500)
                        .setTopTitle("Server Response")
                        .setTitle("SUCCESS!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                        .setButtonsColorRes(R.color.green_500)
                        .setIcon(R.drawable.ic_done_black_24dp)
                        .setMessage(message).setMessageGravity(Gravity.CENTER)
                        .setCancelable(false)
                        .setPositiveButton("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
                }
                else if (code.equals("upload_false")) {

                new LovelyStandardDialog(ctx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.red_500)
                        .setButtonsColorRes(R.color.red_500)
                        .setIcon(R.drawable.ic_close_black_24dp)
                        .setTopTitle("Server Response")
                        .setTitle("FAILED!").setTitleGravity(Gravity.CENTER).setTopTitleColor(R.color.mdtp_white)
                        .setMessage(message).setMessageGravity(Gravity.CENTER)
                        .setCancelable(false)
                        .setPositiveButton("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
}