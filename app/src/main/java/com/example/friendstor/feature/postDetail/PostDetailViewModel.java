package com.example.friendstor.feature.postDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.PostRepository;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;

import java.util.Map;

public class PostDetailViewModel extends ViewModel {

    private final PostRepository postRepository;

    public PostDetailViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public LiveData<ReactResponse> performReaction(Util.PerformReaction performReaction) {
        return postRepository.performReaction(performReaction);
    }

    public LiveData<PostResponse> postDetails(Map<String,String> params) {
        return postRepository.postDetails(params);
    }
}
