package com.example.friendstor.model.friend;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("requests")
	private List<Requests> requests;

	@SerializedName("friends")
	private List<Friends> friends;

	public void setRequests(List<Requests> requests){
		this.requests = requests;
	}

	public List<Requests> getRequests(){
		return requests;
	}

	public void setFriends(List<Friends> friends){
		this.friends = friends;
	}

	public List<Friends> getFriends(){
		return friends;
	}
}