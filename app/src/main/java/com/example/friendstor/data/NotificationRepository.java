package com.example.friendstor.data;

import androidx.lifecycle.MutableLiveData;

import com.example.friendstor.data.remote.ApiError;
import com.example.friendstor.data.remote.ApiService;
import com.example.friendstor.data.remote.CommentRepository;
import com.example.friendstor.model.friend.FriendResponse;
import com.example.friendstor.model.notification.NotificationResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    private final ApiService apiService;
    private static NotificationRepository instance = null;

    public NotificationRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public static NotificationRepository getRepository(ApiService apiService) {
        if (instance == null) {
            instance = new NotificationRepository(apiService);
        }
        return instance;
    }

    public MutableLiveData<NotificationResponse> getNotification(String uid){
        MutableLiveData<NotificationResponse> notif = new MutableLiveData<>();
        Call<NotificationResponse> call = apiService.getNotification(uid);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {

                if(response.isSuccessful()){
                    notif.postValue(response.body());
                }else{
                    Gson gson = new Gson();
                    NotificationResponse notificationResponse = null;
                    try {
                        notificationResponse = gson.fromJson(response.errorBody().string(), NotificationResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        notificationResponse = new NotificationResponse(errorMessage.message, errorMessage.status);
                    }
                    notif.postValue(notificationResponse);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorFromThrowable(t);
                NotificationResponse authResponse = new NotificationResponse(errorMessage.message, errorMessage.status);
                notif.postValue(authResponse);
            }
        });
        return  notif;
    }




}
