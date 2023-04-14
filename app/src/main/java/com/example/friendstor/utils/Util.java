package com.example.friendstor.utils;

public class Util {

    public static class PerformReaction{
        String userId, postId, postOwnerId, previousReactionType, newReactionType;

        public PerformReaction(String userId, String postId, String postOwnerId, String previousReactionType, String newReactionType) {
            this.userId = userId;
            this.postId = postId;
            this.postOwnerId = postOwnerId;
            this.previousReactionType = previousReactionType;
            this.newReactionType = newReactionType;
        }
    }

    public static class PostComment{
        String comment, commentBy, commentPostId, postUserId, commentOn, commentUserId, parentId;

        public PostComment(String comment, String commentBy, String commentPostId, String postUserId, String commentOn, String commentUserId, String parentId) {
            this.comment = comment;
            this.commentBy = commentBy;
            this.commentPostId = commentPostId;
            this.postUserId = postUserId;
            this.commentOn = commentOn;
            this.commentUserId = commentUserId;
            this.parentId = parentId;
        }
    }
}
