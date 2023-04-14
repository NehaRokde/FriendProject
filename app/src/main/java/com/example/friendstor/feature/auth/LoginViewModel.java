package com.example.friendstor.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.UserRepository;
import com.example.friendstor.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private UserRepository userRepository;

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo) {
        return userRepository.login(userInfo);
    }


}
