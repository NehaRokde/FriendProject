package com.example.friendstor.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.PostRepository;
import com.example.friendstor.data.UserRepository;
import com.example.friendstor.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {

    private PostRepository postRepository;

    public PostUploadViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody body, Boolean isCoverOrProfileImage) {
        return postRepository.uploadPost(body, isCoverOrProfileImage);
    }
}
