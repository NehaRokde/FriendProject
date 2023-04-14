package com.example.friendstor.utils.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.reactbutton.FbReactions;
import com.amrdeveloper.reactbutton.ReactButton;
import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.comment.PostCommentRepliesBottomSheet;
import com.example.friendstor.feature.homepage.MainActivity;
import com.example.friendstor.feature.homepage.friends.FriendRequestAdapter;
import com.example.friendstor.model.post.PostsItem;
import com.example.friendstor.model.reaction.Reaction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context con;
    private List<PostsItem> postsItems;

    public interface IUpdateUserReaction {
        void updateUserReaction(String uid,
                                int postId,
                                String postOwnerId,
                                String previousReactionType,
                                String newReactionType,
                                int adpaterPosition);
    }

    IUpdateUserReaction iUpdateUserReaction;

    public PostAdapter(Context context, List<PostsItem> postsItems) {
        this.con = context;
        this.postsItems = postsItems;
        if (context instanceof IUpdateUserReaction) {
            iUpdateUserReaction = (IUpdateUserReaction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IUpdateUserReaction");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PostsItem postsItem = postsItems.get(position);
        holder.peopleName.setText(postsItem.getName());
        holder.date.setText(postsItem.getStatusTime());

        if (postsItem.getPrivacy() == 0) {
            holder.privacyIcon.setImageResource(R.drawable.icon_friends);
        } else if (postsItem.getPrivacy() == 1) {
            holder.privacyIcon.setImageResource(R.drawable.icon_only_me);
        } else if (postsItem.getPrivacy() == 2) {
            holder.privacyIcon.setImageResource(R.drawable.icon_public);
        }



        String profileImage = postsItem.getProfileUrl();
        if (!postsItem.getProfileUrl().isEmpty()) {
            if (Uri.parse(postsItem.getProfileUrl()).getAuthority() == null) {
                profileImage = ApiClient.BASE_URL + postsItem.getProfileUrl();
            }

            Glide.with(con).load(profileImage).placeholder(R.drawable.default_profile_placeholder).into(holder.peopleImage);
        }

        if (!postsItem.getStatusImage().isEmpty()) {
            holder.statusImage.setVisibility(View.VISIBLE);
            Glide.with(con).load(ApiClient.BASE_URL + postsItem.getStatusImage()).placeholder(R.drawable.default_profile_placeholder).into(holder.statusImage);
        } else {
            holder.statusImage.setVisibility(View.GONE);
        }

        if (postsItem.getPost().isEmpty()) {
            holder.post.setVisibility(View.GONE);
        } else {
            holder.post.setVisibility(View.VISIBLE);
            holder.post.setText(postsItem.getPost());
        }

        int reactionCountValue =0;
        reactionCountValue = postsItem.getLikeCount()+
                postsItem.getLikeCount()+
                postsItem.getCareCount()+
                postsItem.getHahaCount()+
                postsItem.getWowCount()+
                postsItem.getSadCount()+
                postsItem.getAngryCount();

        if(reactionCountValue == 0 || reactionCountValue == 1){
            holder.reactionCounter.setText(reactionCountValue + " Reaction");
        }else{
            holder.reactionCounter.setText(reactionCountValue + " Reactions");
        }


        holder.reactButton.setCurrentReaction(FbReactions.getReaction(postsItem.getReactionType()));

        holder.commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCommentRepliesBottomSheet bottomSheet =  new PostCommentRepliesBottomSheet();
                FragmentActivity fragmentActivity = (FragmentActivity) con;
                bottomSheet.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsItems.size();
    }


    public void updatePostAfterReaction(int adapterPosition, Reaction reaction) {
        PostsItem item = postsItems.get(adapterPosition);

        item.setLikeCount(reaction.getLikeCount());
        item.setLoveCount(reaction.getLoveCount());
        item.setCareCount(reaction.getCareCount());
        item.setHahaCount(reaction.getHahaCount());
        item.setWowCount(reaction.getWowCount());
        item.setSadCount(reaction.getSadCount());
        item.setAngryCount(reaction.getAngryCount());
        item.setReactionType(reaction.getReactionType());

        postsItems.set(adapterPosition, item);
        notifyItemChanged(adapterPosition, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView peopleImage, privacyIcon, statusImage;
        TextView post, date, peopleName, reactionCounter;
        ReactButton reactButton;
        LinearLayout commentSection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            peopleImage = itemView.findViewById(R.id.people_image);
            privacyIcon = itemView.findViewById(R.id.privacy_icon);
            statusImage = itemView.findViewById(R.id.status_image);
            reactionCounter = itemView.findViewById(R.id.reactionCounter);
            commentSection = itemView.findViewById(R.id.comment_section);

            post = itemView.findViewById(R.id.post);
            date = itemView.findViewById(R.id.date);
            peopleName = itemView.findViewById(R.id.people_name);

            reactButton = itemView.findViewById(R.id.reaction);
            reactButton.setReactClickListener(this);
            reactButton.setReactDismissListener(this);


        }

        @Override
        public void onClick(View v) {
            onReactionChanged(v);

        }

        @Override
        public boolean onLongClick(View v) {
            onReactionChanged(v);
            return false;
        }

        private void onReactionChanged(View v) {

            String previousReactionType = postsItems.get(getAdapterPosition()).getReactionType();
            String newReactionType = ((ReactButton) v).getCurrentReaction().getReactType();

            if (!previousReactionType.contentEquals(newReactionType)) {
                iUpdateUserReaction.updateUserReaction(
                        FirebaseAuth.getInstance().getUid(),
                        postsItems.get(getAdapterPosition()).getPostId(),
                        postsItems.get(getAdapterPosition()).getPostUserId(),
                        previousReactionType,
                        newReactionType,
                        getAdapterPosition()
                );
            }
        }


    }


}
