package com.example.friendstor.feature.comment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.homepage.friends.FriendAdapter;
import com.example.friendstor.model.comment.CommentsItem;
import com.example.friendstor.utils.AgoDateParse;
import com.example.friendstor.utils.Util;

import java.text.ParseException;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentsItem> commentsItems;
    Util.IOnCommentRepliesAdded iOnCommentRepliesAdded;
    int postAdapterPosition;

    public CommentAdapter(Context context, List<CommentsItem> commentsItems,
                          Util.IOnCommentRepliesAdded iOnCommentRepliesAdded, int postAdapterPosition) {
        this.context = context;
        this.commentsItems = commentsItems;
        this.iOnCommentRepliesAdded = iOnCommentRepliesAdded;
        this.postAdapterPosition = postAdapterPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentsItem item = commentsItems.get(position);

        holder.commentPerson.setText(item.getName());
        holder.commentBody.setText((item.getComment()));

        try {
            holder.commentDate.setText(AgoDateParse.getTimeAgo(item.getCommentDate()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.replyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentReplyBottomSheet(true, item, postAdapterPosition, position);
            }
        });

        String profilePicture = item.getProfileUrl();

        if (Uri.parse(profilePicture).getAuthority() == null) {
            profilePicture = ApiClient.BASE_URL + profilePicture;
        }
        Glide.with(context).load(profilePicture).into(holder.commentProfile);

        if (item.getCommentOn().equals("post")) {
            holder.replyTxt.setVisibility(View.VISIBLE);
        } else {
            holder.replyTxt.setVisibility(View.GONE);
        }

        int totalCommentReplies = item.getTotalCommentReplies();
        if (totalCommentReplies >= 1) {
            holder.subCommentSection.setVisibility(View.VISIBLE);

            if (totalCommentReplies > 1) {
                holder.moreComments.setVisibility(View.VISIBLE);
            } else {
                holder.moreComments.setVisibility(View.GONE);
            }


            if (totalCommentReplies == 2) {
                holder.moreComments.setText("view 1 more comment");
            } else {
                holder.moreComments.setText("view " + totalCommentReplies + " more comments");
            }

            holder.moreComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCommentReplyBottomSheet(false, item, postAdapterPosition, position);
                }
            });

            if (item.getComments().size() >= 1) {
                CommentsItem latestComments = item.getComments().get(0);

                holder.subCommentPerson.setText(latestComments.getName());
                holder.subCommentBody.setText(latestComments.getComment());

                try {
                    holder.subCommentDate.setText(AgoDateParse.getTimeAgo(item.getCommentDate()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                String subProfilePicture = latestComments.getProfileUrl();
                if (Uri.parse(subProfilePicture).getAuthority() == null) {
                    subProfilePicture = ApiClient.BASE_URL + subProfilePicture;
                }
                Glide.with(context).load(subProfilePicture).into(holder.subCommentProfile);
            }

        } else {
            holder.subCommentSection.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return commentsItems.size();
    }

    private void openCommentReplyBottomSheet(boolean shouldOpenKeyboard,
                                             CommentsItem commentsItem,
                                             int postAdapterPosition, int adapterPosition) {

        CommentRepliesBottomSheet bottomSheet = new CommentRepliesBottomSheet(iOnCommentRepliesAdded);

        Bundle bundle = new Bundle();
        bundle.putString("postId", commentsItem.getCommentPostId() + "");
        bundle.putString("postUserId", commentsItem.getPostUserId());
        bundle.putString("commentOn", "comment");
        bundle.putString("commentUserId", commentsItem.getCommentBy());
        bundle.putString("parentId", commentsItem.getCid() + "");
        bundle.putInt("commentCounter", -1);
        bundle.putBoolean("shouldOpenKeyboard", shouldOpenKeyboard);
        bundle.putInt("postAdapterPosition", postAdapterPosition);
        bundle.putInt("adapterPosition", adapterPosition);

        bottomSheet.setArguments(bundle);

        FragmentActivity fragmentActivity = (FragmentActivity) context;
        bottomSheet.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // post replies views
        ImageView commentProfile;
        TextView commentPerson, commentBody, commentDate, replyTxt;

        // comment replies views
        LinearLayout subCommentSection;
        ImageView subCommentProfile;
        TextView moreComments, subCommentPerson, subCommentBody, subCommentDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentProfile = itemView.findViewById(R.id.comment_profile);
            commentPerson = itemView.findViewById(R.id.comment_person);
            commentBody = itemView.findViewById(R.id.comment_body);
            commentDate = itemView.findViewById(R.id.comment_date);
            replyTxt = itemView.findViewById(R.id.reply_txt);

            subCommentSection = itemView.findViewById(R.id.sub_comment_section);
            subCommentProfile = itemView.findViewById(R.id.sub_comment_profile);
            moreComments = itemView.findViewById(R.id.more_comments);
            subCommentPerson = itemView.findViewById(R.id.sub_comment_person);
            subCommentBody = itemView.findViewById(R.id.sub_comment_body);
            subCommentDate = itemView.findViewById(R.id.sub_comment_date);


        }
    }
}
