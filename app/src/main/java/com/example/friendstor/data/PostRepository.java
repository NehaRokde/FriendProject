package com.example.friendstor.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.friendstor.data.remote.ApiError;
import com.example.friendstor.data.remote.ApiService;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {

    private static PostRepository instance = null;
    private final ApiService apiService;

    private PostRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public static PostRepository getRepository(ApiService apiService) {
        if (instance == null) {
            instance = new PostRepository(apiService);
        }
        return instance;
    }


    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage){
        MutableLiveData<GeneralResponse> postUpload = new MutableLiveData<>();
        Call<GeneralResponse> call = null;
        if(isCoverOrProfileImage){
            call = apiService.uploadImage(multipartBody);
        }else {
            call = apiService.uploadPost(multipartBody);
        }

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if(response.isSuccessful()){
                    postUpload.postValue(response.body());
                }else{

                    Gson gson = new Gson();
                    GeneralResponse generalResponse = null;
                    try {
                        generalResponse = gson.fromJson(response.errorBody().string(), GeneralResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                    }
                    postUpload.postValue(generalResponse);
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                GeneralResponse authResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                postUpload.postValue(authResponse);
            }
        });


        return postUpload;


    }

    public LiveData<PostResponse> getNewsFeed(Map<String,String> params){
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.getNewsFeed(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try {
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                PostResponse authResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(authResponse);
            }
        });
        return  posts;
    }

    public LiveData<ReactResponse> performReaction(Util.PerformReaction performReaction){
        MutableLiveData<ReactResponse> reacts = new MutableLiveData<>();
        Call<ReactResponse> call = apiService.performReaction(performReaction);
        call.enqueue(new Callback<ReactResponse>() {
            @Override
            public void onResponse(Call<ReactResponse> call, Response<ReactResponse> response) {

                if(response.isSuccessful()){
                    reacts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    ReactResponse reactResponse = null;
                    try {
                        reactResponse = gson.fromJson(response.errorBody().string(), ReactResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        reactResponse = new ReactResponse(errorMessage.message, errorMessage.status);
                    }
                    reacts.postValue(reactResponse);
                }
            }

            @Override
            public void onFailure(Call<ReactResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                ReactResponse authResponse = new ReactResponse(errorMessage.message, errorMessage.status);
                reacts.postValue(authResponse);
            }
        });
        return  reacts;
    }

    public LiveData<PostResponse> loadProfilePosts(Map<String,String> params){
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.loadProfilePosts(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try {
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                PostResponse authResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(authResponse);
            }
        });
        return  posts;
    }

    public LiveData<PostResponse> postDetails(Map<String,String> params){
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.postDetail(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                if(response.isSuccessful()){
                    posts.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try {
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                PostResponse authResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(authResponse);
            }
        });
        return  posts;
    }
}
