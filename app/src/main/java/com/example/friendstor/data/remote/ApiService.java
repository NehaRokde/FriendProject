package com.example.friendstor.data.remote;

import com.example.friendstor.feature.auth.LoginActivity;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.auth.AuthResponse;
import com.example.friendstor.model.comment.CommentResponse;
import com.example.friendstor.model.friend.FriendResponse;
import com.example.friendstor.model.notification.NotificationResponse;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.profile.ProfileResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.model.search.SearchResponse;
import com.example.friendstor.utils.Util;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @POST("performReaction")
    Call<ReactResponse> performReaction(@Body Util.PerformReaction performReaction);

    @POST("postcomment")
    Call<CommentResponse> postComment(@Body Util.PostComment postComment);

    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);

    @POST("uploadImage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);

    @GET("postdetail")
    Call<PostResponse> postDetail(@QueryMap Map<String, String> params);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String, String> params);

    @POST("performaction")
    Call<GeneralResponse> performAction(@Body ProfileActivity.PerformAction body);

    @GET("loadfriends")
    Call<FriendResponse> loadFriends(@Query("uid") String uid);

    @GET("getnotification")
    Call<NotificationResponse> getNotification(@Query("uid") String uid);

    @GET("getpostcomments")
    Call<CommentResponse> getPostComments(@Query("postId") String postId, @Query("postUserId") String postUserId);

    @GET("getcommentreplies")
    Call<CommentResponse> getCommentReplies(@Query("postId") String postId, @Query("commentId") String commentId);

    @GET("getnewsfeed")
    Call<PostResponse> getNewsFeed(@QueryMap Map<String, String> params);

    @GET("loadProfilePosts")
    Call<PostResponse> loadProfilePosts(@QueryMap Map<String, String> params);
}
