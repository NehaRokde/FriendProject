package com.example.friendstor.feature.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.remote.CommentRepository;
import com.example.friendstor.model.comment.CommentResponse;
import com.example.friendstor.utils.Util;

public class CommentViewModel extends ViewModel {

    public final CommentRepository commentRepository;

    public CommentViewModel (CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public LiveData<CommentResponse> postComment(Util.PostComment postComment){
        return commentRepository.postComment(postComment);
    }

    public LiveData<CommentResponse> getPostComments(String postId, String postUserId){
        return commentRepository.getPostComments(postId, postUserId);
    }

    public LiveData<CommentResponse> getCommentReplies(String postId, String commentId){
        return commentRepository.getCommentReplies(postId, commentId);
    }

}
