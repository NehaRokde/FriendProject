package com.example.friendstor.feature.profile;

import static androidx.lifecycle.ViewModelProvider.Factory;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.example.friendstor.feature.fullimage.FullImageActivity;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.post.PostsItem;
import com.example.friendstor.model.profile.ProfileResponse;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;
import com.example.friendstor.utils.ViewModelFactory;
import com.example.friendstor.utils.adapter.PostAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener,
        SwipeRefreshLayout.OnRefreshListener, PostAdapter.IUpdateUserReaction {

    /*
     current_state
     1 = We are friends
     2 = we have sent friend request to that person
     3 = We have received friend request from that person
     4 = we are unknown
     5 = Our own profile
  */
    private int current_state = 0;
    private String uid = "", profileUrl = "", coverUrl = "";

    private Button profileOptionBtn;
    private ImageView profileImage, coverImage;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private ProfileViewModel viewModel;
    private Boolean isCoverImage = false;
    private ProgressDialog progressDialog;

    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<PostsItem> postsItems;
    private Boolean isFirstLoading = true;

    private int limit = 1;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this, (Factory) new ViewModelFactory()).get(ProfileViewModel.class);

        profileOptionBtn = findViewById(R.id.profile_action_button);
        profileImage = findViewById(R.id.profile_image);
        coverImage = findViewById(R.id.profile_cover);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

        postsItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerview);

        progressBar = findViewById(R.id.progressbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLastItemReached()) {
                    offset += limit;
                    getProfilePosts();
                }
            }
        });


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        uid = getIntent().getStringExtra("uid");

        if (uid.equals(FirebaseAuth.getInstance().getUid())) {
            current_state = 5;
            profileOptionBtn.setText("Edit Profile");
        } else {
            // find current state from backend
            profileOptionBtn.setText("Loading...");
            profileOptionBtn.setEnabled(false);
        }

        fetchProfileInfo();


    }

    private void fetchProfileInfo() {

        progressDialog.show();

        // call our api
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        if (current_state == 5) {
            params.put("current_state", current_state + "");
        } else {
            params.put("profileId", uid);
        }

        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {

                progressDialog.dismiss();
                if (profileResponse.getStatus() == 200) {

                    collapsingToolbarLayout.setTitle(profileResponse.getProfile().getName());
                    profileUrl = profileResponse.getProfile().getProfileUrl();
                    coverUrl = profileResponse.getProfile().getCoverUrl();
                    current_state = Integer.parseInt(profileResponse.getProfile().getState());

                    if (!profileUrl.isEmpty()) {
                        Uri profile_uri = Uri.parse(profileUrl);

                        //https://www.fb.com
                        //www.fb.com is authority
                        if (profile_uri.getAuthority() == null) {
                            profileUrl = ApiClient.BASE_URL + profileUrl;
                        }
                        Glide.with(ProfileActivity.this).load(profileUrl).into(profileImage);

                    }else{
                        profileUrl = R.drawable.default_profile_placeholder + "";
                    }

                    if (!coverUrl.isEmpty()) {
                        Uri cover_uri = Uri.parse(coverUrl);

                        if (cover_uri.getAuthority() == null) {
                            coverUrl = ApiClient.BASE_URL + coverUrl;
                            Log.i("coverUrl", coverUrl);
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(coverImage);
                    }else{
                        coverUrl = R.drawable.cover_picture_placeholder+"";
                    }

                    if (current_state == 0) {
                        profileOptionBtn.setText("Loading..");
                        profileOptionBtn.setEnabled(false);
                        return;
                    } else if (current_state == 1) {
                        profileOptionBtn.setText("You are Friends");
                    } else if (current_state == 2) {
                        profileOptionBtn.setText("Cancel Request");
                    } else if (current_state == 3) {
                        profileOptionBtn.setText("Accept Request");
                    } else if (current_state == 4) {
                        profileOptionBtn.setText("Send Request");
                    } else if (current_state == 5) {
                        profileOptionBtn.setText("Edit Profile");
                    }

                    profileOptionBtn.setEnabled(true);
                    loadProfileOptionButton();
                    getProfilePosts();

                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getProfilePosts() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("limit", limit + "");
        params.put("offset", offset + "");
        params.put("current_state", current_state + "");

        progressBar.setVisibility(View.VISIBLE);

        viewModel.loadProfilePosts(params).observe(this, new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                progressBar.setVisibility(View.GONE);
                if (postResponse.getStatus() == 200) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());

                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(ProfileActivity.this, postsItems);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        postAdapter.notifyItemRangeInserted(postsItems.size(), postResponse.getPosts().size());
                    }

                    if (postResponse.getPosts().size() == 0) {
                        offset -= limit;
                    }
                    isFirstLoading = false;
                } else {
                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(ProfileActivity.this, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isLastItemReached() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastCompletelyVisibleItemPosition();
        int noOfItems = postAdapter.getItemCount();
        return (position >= noOfItems - 1);

    }

    private void loadProfileOptionButton() {
        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileOptionBtn.setEnabled(false);

                if (current_state == 5) {
                    CharSequence[] options = new CharSequence[]{
                            "Change Cover Image", "Change Profile Image",
                            "View Cover Image", "View Profile Image"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                isCoverImage = true;
                                selectImage();
                            } else if (position == 1) {
                                isCoverImage = false;
                                selectImage();
                            } else if (position == 2) {
                                // cover Image
                                viewFullImage(coverImage, coverUrl);
                            } else if (position == 3) {
                                // profile Image
                                viewFullImage(coverImage, profileUrl);
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 4) {
                    CharSequence[] options = new CharSequence[]{
                            "Send Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 3) {
                    CharSequence[] options = new CharSequence[]{
                            "Accept Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 2) {
                    CharSequence[] options = new CharSequence[]{
                            "Cancel Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 1) {
                    CharSequence[] options = new CharSequence[]{
                            "UnFriend"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
            }
        });
    }

    private void performAction() {
        progressDialog.show();

        viewModel.performAction(new PerformAction(
                current_state + "",
                FirebaseAuth.getInstance().getUid(),
                uid)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {
                    profileOptionBtn.setEnabled(true);

                    if (current_state == 4) {
                        current_state = 2;
                        profileOptionBtn.setText("Cancel Request");
                    } else if (current_state == 3) {
                        current_state = 1;
                        profileOptionBtn.setText("You are Friends");
                    } else if (current_state == 2) {
                        current_state = 4;
                        profileOptionBtn.setText("Send Request");
                    } else if (current_state == 1) {
                        current_state = 4;
                        profileOptionBtn.setText("Send Request");
                    }
                } else {
                    profileOptionBtn.setEnabled(false);
                    profileOptionBtn.setText("Error");
                }
            }
        });
    }

    private void viewFullImage(ImageView imageView, String imageUrl) {
        Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl", imageUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(imageView, imageUrl);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }

    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);
            try {
                File compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                uploadImage(compressedImageFile);

            } catch (IOException e) {
                Toast.makeText(this, "Image Picker failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File compressedImageFile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", FirebaseAuth.getInstance().getUid() + "");
        builder.addFormDataPart("isCoverImage", isCoverImage + "");

        builder.addFormDataPart("file",
                compressedImageFile.getName(),
                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));

        progressDialog.show();

        viewModel.uploadPost(builder.build(), true).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {

                    if (isCoverImage) {
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).into(coverImage);
                    } else {
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).into(profileImage);
                    }
                }

            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        profileOptionBtn.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        offset = 0;
        postsItems.clear();
        isFirstLoading = true;
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        getProfilePosts();

    }

    @Override
    public void updateUserReaction(String uid, int postId, String postOwnerId, String previousReactionType, String newReactionType, int adpaterPosition) {

        viewModel.performReaction(new Util.PerformReaction(
                uid,
                postId+"",
                postOwnerId,
                previousReactionType,
                newReactionType
        )).observe(this, new Observer<ReactResponse>() {
            @Override
            public void onChanged(ReactResponse reactResponse) {
                if(reactResponse.getStatus() == 200){
                    postAdapter.updatePostAfterReaction(adpaterPosition, reactResponse.getReaction());
                }else {
                    Toast.makeText(ProfileActivity.this, reactResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class PerformAction {

        String operationType, uid, profileId;

        public PerformAction(String operationType, String uid, String profileId) {
            this.operationType = operationType;
            this.uid = uid;
            this.profileId = profileId;
        }
    }
}