package com.example.friendstor.feature.homepage.newsfeed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.PostRepository;
import com.example.friendstor.data.UserRepository;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.friend.FriendResponse;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;

import java.util.Map;

public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private MutableLiveData<FriendResponse> friends = null;

    public MainViewModel(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public MutableLiveData<FriendResponse> loadFriends(String uid) {
        if (friends == null) {
            friends = userRepository.loadFriends(uid);
        }
        return friends;
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return userRepository.performAction(performAction);
    }

    public LiveData<PostResponse> getNewsFeed(Map<String, String> params) {
        return postRepository.getNewsFeed(params);
    }

    public LiveData<ReactResponse> performReaction(Util.PerformReaction performReaction) {
        return postRepository.performReaction(performReaction);
    }
}
