package com.example.friendstor.model.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationsItem{

	@SerializedName("profileUrl")
	private String profileUrl;

	@SerializedName("notificationFrom")
	private String notificationFrom;

	@SerializedName("post")
	private String post;

	@SerializedName("notificationTime")
	private String notificationTime;

	@SerializedName("nid")
	private int nid;

	@SerializedName("name")
	private String name;

	@SerializedName("notificationTo")
	private String notificationTo;

	@SerializedName("postId")
	private String postId;

	@SerializedName("type")
	private String type;

	public void setProfileUrl(String profileUrl){
		this.profileUrl = profileUrl;
	}

	public String getProfileUrl(){
		return profileUrl;
	}

	public void setNotificationFrom(String notificationFrom){
		this.notificationFrom = notificationFrom;
	}

	public String getNotificationFrom(){
		return notificationFrom;
	}

	public void setPost(String post){
		this.post = post;
	}

	public String getPost(){
		return post;
	}

	public void setNotificationTime(String notificationTime){
		this.notificationTime = notificationTime;
	}

	public String getNotificationTime(){
		return notificationTime;
	}

	public void setNid(int nid){
		this.nid = nid;
	}

	public int getNid(){
		return nid;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setNotificationTo(String notificationTo){
		this.notificationTo = notificationTo;
	}

	public String getNotificationTo(){
		return notificationTo;
	}

	public void setPostId(String postId){
		this.postId = postId;
	}

	public String getPostId(){
		return postId;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}
}