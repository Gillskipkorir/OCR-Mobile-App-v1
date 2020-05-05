package com.kip.gillz.meter_readings;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public interface RegisterAPI
{

    @FormUrlEncoded
    @POST("/tal/newclient.php")
    public void insertUser(
            @Field("fname") String fname,
            @Field("phone") String phone,
            @Field("meterno") String meterno,
            @Field("inst_date") String inst_date,
            @Field("plot") String plot,
            @Field("remark") String remark,

            Callback<Response> callback);
}
