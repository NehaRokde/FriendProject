package com.example.friendstor.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendstor.data.Repository;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.data.remote.ApiService;
import com.example.friendstor.feature.auth.LoginViewModel;
import com.example.friendstor.feature.postupload.PostUploadViewModel;
import com.example.friendstor.feature.profile.ProfileViewModel;
import com.example.friendstor.feature.search.SearchViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(repository);
        }else if(modelClass.isAssignableFrom(ProfileViewModel.class)){
            return (T) new ProfileViewModel(repository);
        }else if(modelClass.isAssignableFrom(PostUploadViewModel.class)){
            return (T) new PostUploadViewModel(repository);
        }else if(modelClass.isAssignableFrom(SearchViewModel.class)){
            return (T) new SearchViewModel(repository);
        }
        throw new IllegalArgumentException("View Model not Found");

    }
}
