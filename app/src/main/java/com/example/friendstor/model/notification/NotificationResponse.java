package com.example.friendstor.model.notification;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("notifications")
	private List<NotificationsItem> notifications;

	@SerializedName("status")
	private int status;

	public NotificationResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setNotifications(List<NotificationsItem> notifications){
		this.notifications = notifications;
	}

	public List<NotificationsItem> getNotifications(){
		return notifications;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}