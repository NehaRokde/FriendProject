package com.example.friendstor.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.PostRepository;
import com.example.friendstor.data.UserRepository;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.profile.ProfileResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private UserRepository userRepository;
    private PostRepository postRepository;

    public ProfileViewModel(UserRepository userRepository, PostRepository postRepository) {

        this.userRepository = userRepository;
        this.postRepository = postRepository;

    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        return userRepository.fetchProfileInfo(params);
    }

    public LiveData<PostResponse> loadProfilePosts(Map<String, String> params) {
        return postRepository.loadProfilePosts(params);
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody body, Boolean isCoverOrProfileImage) {
        return postRepository.uploadPost(body, isCoverOrProfileImage);
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return userRepository.performAction(performAction);
    }
    public LiveData<ReactResponse> performReaction(Util.PerformReaction performReaction) {
        return postRepository.performReaction(performReaction);
    }
}
