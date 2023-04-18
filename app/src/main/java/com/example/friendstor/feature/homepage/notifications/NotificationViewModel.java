package com.example.friendstor.feature.homepage.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendstor.data.NotificationRepository;
import com.example.friendstor.model.notification.NotificationResponse;

public class NotificationViewModel extends ViewModel {

    public NotificationRepository notificationRepository;

    public NotificationViewModel(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public LiveData<NotificationResponse> getNotification(String uid) {
        return notificationRepository.getNotification(uid);
    }
}
