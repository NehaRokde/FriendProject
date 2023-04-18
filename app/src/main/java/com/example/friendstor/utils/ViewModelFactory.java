package com.example.friendstor.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendstor.data.NotificationRepository;
import com.example.friendstor.data.PostRepository;
import com.example.friendstor.data.UserRepository;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.data.remote.ApiService;
import com.example.friendstor.data.remote.CommentRepository;
import com.example.friendstor.feature.auth.LoginViewModel;
import com.example.friendstor.feature.comment.CommentViewModel;
import com.example.friendstor.feature.homepage.newsfeed.MainViewModel;
import com.example.friendstor.feature.homepage.notifications.NotificationViewModel;
import com.example.friendstor.feature.postDetail.PostDetailViewModel;
import com.example.friendstor.feature.postupload.PostUploadViewModel;
import com.example.friendstor.feature.profile.ProfileViewModel;
import com.example.friendstor.feature.search.SearchViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final NotificationRepository notificationRepository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        userRepository = UserRepository.getRepository(apiService);
        postRepository = PostRepository.getRepository(apiService);
        commentRepository = CommentRepository.getRepository(apiService);
        notificationRepository = NotificationRepository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(userRepository, postRepository);
        } else if (modelClass.isAssignableFrom(PostUploadViewModel.class)) {
            return (T) new PostUploadViewModel(postRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userRepository, postRepository);
        } else if (modelClass.isAssignableFrom(CommentViewModel.class)) {
            return (T) new CommentViewModel(commentRepository);
        }else if (modelClass.isAssignableFrom(PostDetailViewModel.class)) {
            return (T) new PostDetailViewModel(postRepository);
        }else if (modelClass.isAssignableFrom(NotificationViewModel.class)) {
            return (T) new NotificationViewModel(notificationRepository);
        }
        throw new IllegalArgumentException("View Model not Found");

    }
}
