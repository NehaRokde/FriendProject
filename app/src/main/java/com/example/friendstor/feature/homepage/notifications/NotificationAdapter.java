package com.example.friendstor.feature.homepage.notifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.homepage.friends.FriendAdapter;
import com.example.friendstor.feature.postDetail.PostDetailActivity;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.notification.NotificationsItem;
import com.example.friendstor.model.profile.Profile;
import com.example.friendstor.utils.AgoDateParse;

import java.text.ParseException;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationsItem> items;

    public NotificationAdapter(Context context, List<NotificationsItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationsItem notificationsItem = items.get(position);

        if (notificationsItem.getType().equals("post-reaction")) {
            holder.title.setText(notificationsItem.getName() + " reacted on your post");
        } else if (notificationsItem.getType().equals("post-comment")) {
            holder.title.setText(notificationsItem.getName() + " commented on your post");
        } else if (notificationsItem.getType().equals("comment-reply")) {
            holder.title.setText(notificationsItem.getName() + "  replied on your comment");
        } else if (notificationsItem.getType().equals("request-accepted")) {
            holder.title.setText(notificationsItem.getName() + " accepted your friend request");
        } else if (notificationsItem.getType().equals("friend-request")) {
            holder.title.setText(notificationsItem.getName() + " send you friend request");
        } else {
            holder.title.setText("Unknown");
        }

        if (notificationsItem.getType().equals("request-accepted") ||
                notificationsItem.getType().equals("friend-request")) {

            holder.body.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ProfileActivity.class)
                            .putExtra("uid", notificationsItem.getNotificationFrom()));
                }
            });
        } else {
            if (notificationsItem.getPost() != null && !notificationsItem.getPost().isEmpty()) {
                holder.body.setVisibility(View.VISIBLE);
                holder.body.setText(notificationsItem.getPost());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, PostDetailActivity.class)
                            .putExtra("shouldLoadFromApi", true)
                            .putExtra("postId", notificationsItem.getPostId()));
                }
            });
        }

        String profileImage = notificationsItem.getProfileUrl();
        if (Uri.parse(profileImage).getAuthority() == null) {
            profileImage = ApiClient.BASE_URL + profileImage;
        }
        Glide.with(context).load(profileImage)
                .placeholder(R.drawable.default_profile_placeholder).into(holder.profileImage);

        try {
            holder.date.setText(AgoDateParse.getTimeAgo(notificationsItem.getNotificationTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView title, body, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.date);
        }
    }
}
