package com.example.friendstor.feature.postDetail;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amrdeveloper.reactbutton.FbReactions;
import com.amrdeveloper.reactbutton.ReactButton;
import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.comment.PostCommentRepliesBottomSheet;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.post.PostsItem;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.AgoDateParse;
import com.example.friendstor.utils.Util;
import com.example.friendstor.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity implements Util.IOCommentAdded {

    ImageView peopleImage, privacyIcon, statusImage;
    TextView post, date, peopleName, reactionCounter, commentCount;
    ReactButton reactButton;
    LinearLayout commentSection;
    Toolbar toolbar;
    ScrollView scrollview;
    int position = 0;

    String previousReactionType = "", newReactionType = "";
    int reactionCountValue = 0;
    int commentCounterValue = 0;
    PostDetailViewModel viewModel;
    PostsItem postsItem = null;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        peopleImage = findViewById(R.id.people_image);
        privacyIcon = findViewById(R.id.privacy_icon);
        statusImage = findViewById(R.id.status_image);
        reactionCounter = findViewById(R.id.reactionCounter);
        commentSection = findViewById(R.id.comment_section);
        commentCount = findViewById(R.id.comment_txt);
        toolbar = findViewById(R.id.toolbar);
        scrollview = findViewById(R.id.scrollview);

        post = findViewById(R.id.post);
        date = findViewById(R.id.date);
        peopleName = findViewById(R.id.people_name);

        reactButton = findViewById(R.id.reaction);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
//        reactButton.setReactClickListener(this);
//        reactButton.setReactDismissListener(this);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(PostDetailViewModel.class);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().getBooleanExtra("shouldLoadFromApi", false)) {


        } else {
            scrollview.setVisibility(View.VISIBLE);
            position = getIntent().getIntExtra("position", 0);
            postsItem = new Gson().fromJson(getIntent().getStringExtra("post"), PostsItem.class);
            showPost();
        }

    }

    private void showPost() {

        peopleName.setText(postsItem.getName());
        commentCounterValue = postsItem.getCommentCount();

        try {
            date.setText(AgoDateParse.getTimeAgo(postsItem.getStatusTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        reactButton.setEnabled(true);
        previousReactionType = postsItem.getReactionType();

        if (postsItem.getPrivacy() == 0) {
            privacyIcon.setImageResource(R.drawable.icon_friends);
        } else if (postsItem.getPrivacy() == 1) {
            privacyIcon.setImageResource(R.drawable.icon_only_me);
        } else if (postsItem.getPrivacy() == 2) {
            privacyIcon.setImageResource(R.drawable.icon_public);
        }


        String profileImage = postsItem.getProfileUrl();
        if (!postsItem.getProfileUrl().isEmpty()) {
            if (Uri.parse(postsItem.getProfileUrl()).getAuthority() == null) {
                profileImage = ApiClient.BASE_URL + postsItem.getProfileUrl();
            }

            Glide.with(PostDetailActivity.this).load(profileImage)
                    .into(peopleImage);
        }

        if (!postsItem.getStatusImage().isEmpty()) {
            statusImage.setVisibility(View.VISIBLE);
            Glide.with(PostDetailActivity.this).load(ApiClient.BASE_URL + postsItem.getStatusImage())
                    .into(statusImage);
        } else {
            statusImage.setVisibility(View.GONE);
        }

        if (postsItem.getPost().isEmpty()) {
            post.setVisibility(View.GONE);
        } else {
            post.setVisibility(View.VISIBLE);
            post.setText(postsItem.getPost());
        }

        if (postsItem.getCommentCount() == 0 || postsItem.getCommentCount() == 1) {
            commentCount.setText(postsItem.getCommentCount() + " comment");
        } else {
            commentCount.setText(postsItem.getCommentCount() + " comments");
        }

        reactionCountValue = postsItem.getLikeCount() +
                postsItem.getLikeCount() +
                postsItem.getCareCount() +
                postsItem.getHahaCount() +
                postsItem.getWowCount() +
                postsItem.getSadCount() +
                postsItem.getAngryCount();

        if (reactionCountValue == 0 || reactionCountValue == 1) {
            reactionCounter.setText(reactionCountValue + " Reaction");
        } else {
            reactionCounter.setText(reactionCountValue + " Reactions");
        }
        reactButton.setCurrentReaction(FbReactions.getReaction(postsItem.getReactionType()));

        reactButton.setReactClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReactionChanged((ReactButton) v, postsItem);
            }
        });
        reactButton.setReactDismissListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onReactionChanged((ReactButton) v, postsItem);
                return false;
            }
        });

        commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCommentRepliesBottomSheet bottomSheet = new PostCommentRepliesBottomSheet();

                Bundle bundle = new Bundle();
                bundle.putString("postId", postsItem.getPostId() + "");
                bundle.putString("postUserId", postsItem.getPostUserId());
                bundle.putString("commentOn", "post");
                bundle.putString("commentUserId", "-1");
                bundle.putString("parentId", "-1");
                bundle.putInt("commentCounter", postsItem.getCommentCount());
                bundle.putBoolean("shouldOpenKeyboard", false);
                bundle.putInt("postAdapterPosition", position);

                bottomSheet.setArguments(bundle);


                FragmentActivity fragmentActivity = (FragmentActivity) PostDetailActivity.this;
                bottomSheet.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
            }
        });

    }

    private void onReactionChanged(ReactButton v, PostsItem postsItem) {
        newReactionType = v.getCurrentReaction().getReactType() + "";
        if (previousReactionType.equals(newReactionType)) {
            v.setEnabled(false);
            //call api
            viewModel.performReaction(
                    new Util.PerformReaction(
                            FirebaseAuth.getInstance().getUid(),
                            postsItem.getPostId() + "",
                            postsItem.getPostUserId(),
                            previousReactionType,
                            newReactionType)).observe(this, new Observer<ReactResponse>() {
                @Override
                public void onChanged(ReactResponse reactResponse) {
                    v.setEnabled(true);
                    if (reactResponse.getStatus() == 200) {
                        previousReactionType = newReactionType;
                        reactionCountValue = reactResponse.getReaction().getLikeCount() +
                                reactResponse.getReaction().getLikeCount() +
                                reactResponse.getReaction().getCareCount() +
                                reactResponse.getReaction().getHahaCount() +
                                reactResponse.getReaction().getWowCount() +
                                reactResponse.getReaction().getSadCount() +
                                reactResponse.getReaction().getAngryCount();

                        if (reactionCountValue == 0 || reactionCountValue == 1) {
                            reactionCounter.setText(reactionCountValue + " Reaction");
                        } else {
                            reactionCounter.setText(reactionCountValue + " Reactions");
                        }
                    } else {
                        Toast.makeText(PostDetailActivity.this, reactResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }


    @Override
    public void onCommandAdded(int adapterPosition) {
        commentCounterValue++;
        postsItem.setCommentCount(commentCounterValue);
        if(commentCounterValue == 1){
            commentCount.setText(commentCounterValue + " comment");
        }else{
            commentCount.setText(commentCounterValue + " comments");
        }
    }
}