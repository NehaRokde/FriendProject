package com.example.friendstor.model;

import com.google.gson.annotations.SerializedName;

public class GeneralResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	@SerializedName("extra")
	private String extra;

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public GeneralResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage(){
		return message;
	}

	public int getStatus(){
		return status;
	}
}