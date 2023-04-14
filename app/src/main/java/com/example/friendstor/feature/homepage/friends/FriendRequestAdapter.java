package com.example.friendstor.feature.homepage.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.friend.Friends;
import com.example.friendstor.model.friend.Requests;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    Context context;
    List<Requests> requests;

    private IPerformAction iPerformAction;
    public FriendRequestAdapter(Context context, List<Requests> requests) {
        this.context = context;
        this.requests = requests;
        this.iPerformAction = (IPerformAction) context;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position) {
        Requests item = requests.get(position);
        holder.profileName.setText(item.getName());

        String image = "";
        if (Uri.parse(item.getProfileUrl()).getAuthority() == null) {
            image = ApiClient.BASE_URL + item.getProfileUrl();
        } else {
            image = item.getProfileUrl();
        }
        Glide.with(context).load(image).placeholder(R.drawable.default_profile_placeholder).into(holder.profileImage);

        holder.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", item.getUid()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage;
        TextView profileName;
        Button btnAcceptRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.friend_image);
            profileName = itemView.findViewById(R.id.profile_name);
            btnAcceptRequest = itemView.findViewById(R.id.btnAccept);

            btnAcceptRequest.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            iPerformAction.performAction(requests.indexOf(requests.get(getAdapterPosition())),
                    requests.get(getAdapterPosition()).getUid(),
                    3);
        }
    }

    public interface  IPerformAction{
        void performAction(int position, String profileId, int operationType);
    }
}

