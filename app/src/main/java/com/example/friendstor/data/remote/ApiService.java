package com.example.friendstor.data.remote;

import com.example.friendstor.feature.auth.LoginActivity;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.auth.AuthResponse;
import com.example.friendstor.model.profile.ProfileResponse;
import com.example.friendstor.model.search.SearchResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {

    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);

    @POST("uploadImage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String, String> params);
}
