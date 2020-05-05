package com.kip.gillz.meter_readings;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface Submitreadings {

    @FormUrlEncoded
    @POST("/tal/scanreadings.php")
    public void inserreadings(
            @Field("Fname") String Fname,
            @Field("Lname") String Lname,
            @Field("meterno") String readings,
            @Field("readings") String meterno,
            @Field("Username") String Username,
            Callback<Response> callback);
}