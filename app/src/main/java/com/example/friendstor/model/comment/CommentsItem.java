package com.example.friendstor.model.comment;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CommentsItem{

	@SerializedName("profileUrl")
	private String profileUrl;

	@SerializedName("userToken")
	private String userToken;

	@SerializedName("postUserId")
	private String postUserId;

	@SerializedName("comments")
	private List<CommentsItem> comments;

	@SerializedName("commentBy")
	private String commentBy;

	@SerializedName("commentDate")
	private String commentDate;

	@SerializedName("name")
	private String name;

	@SerializedName("comment")
	private String comment;

	@SerializedName("commentPostId")
	private int commentPostId;

	@SerializedName("commentOn")
	private String commentOn;

	@SerializedName("parentId")
	private int parentId;

	@SerializedName("cid")
	private int cid;

	@SerializedName("totalCommentReplies")
	private int totalCommentReplies;

	public CommentsItem(String profileUrl, String commentDate, String name, String comment) {
		this.profileUrl = profileUrl;
		this.commentDate = commentDate;
		this.name = name;
		this.comment = comment;
	}

	public int getTotalCommentReplies() {
		return totalCommentReplies;
	}

	public void setTotalCommentReplies(int totalCommentReplies) {
		this.totalCommentReplies = totalCommentReplies;
	}

	public void setProfileUrl(String profileUrl){
		this.profileUrl = profileUrl;
	}

	public String getProfileUrl(){
		return profileUrl;
	}

	public void setUserToken(String userToken){
		this.userToken = userToken;
	}

	public String getUserToken(){
		return userToken;
	}

	public void setPostUserId(String postUserId){
		this.postUserId = postUserId;
	}

	public String getPostUserId(){
		return postUserId;
	}

	public void setComments(List<CommentsItem> comments){
		this.comments = comments;
	}

	public List<CommentsItem> getComments(){
		return comments;
	}

	public void setCommentBy(String commentBy){
		this.commentBy = commentBy;
	}

	public String getCommentBy(){
		return commentBy;
	}

	public void setCommentDate(String commentDate){
		this.commentDate = commentDate;
	}

	public String getCommentDate(){
		return commentDate;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	public void setCommentPostId(int commentPostId){
		this.commentPostId = commentPostId;
	}

	public int getCommentPostId(){
		return commentPostId;
	}

	public void setCommentOn(String commentOn){
		this.commentOn = commentOn;
	}

	public String getCommentOn(){
		return commentOn;
	}

	public void setParentId(int parentId){
		this.parentId = parentId;
	}

	public int getParentId(){
		return parentId;
	}

	public void setCid(int cid){
		this.cid = cid;
	}

	public int getCid(){
		return cid;
	}
}