package com.example.friendstor.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.Repository;
import com.example.friendstor.model.auth.AuthResponse;
import com.google.firebase.auth.AuthResult;

public class LoginViewModel extends ViewModel {
    private Repository repository;

    public LoginViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo) {
        return repository.login(userInfo);
    }


}
