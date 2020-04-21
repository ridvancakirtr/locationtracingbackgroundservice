package com.example.myapplication.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSetting {
    public static Retrofit get(){
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://iptvserverim.com/location/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
