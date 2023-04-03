package com.example.friendstor.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.Repository;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.profile.ProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private Repository repository;

    public ProfileViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        return repository.fetchProfileInfo(params);
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody body, Boolean isCoverOrProfileImage) {
        return this.repository.uploadPost(body, isCoverOrProfileImage);
    }
}
