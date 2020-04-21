package com.example.myapplication.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JSONPlaceHolderAPI {
    @POST("update.php")
    Call<Post> createPost(@Body Post post);
}

