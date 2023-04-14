package com.example.friendstor.feature.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.friendstor.R;
import com.example.friendstor.feature.homepage.friends.FriendRequestAdapter;
import com.example.friendstor.feature.homepage.friends.FriendsFragment;
import com.example.friendstor.feature.homepage.newsfeed.MainViewModel;
import com.example.friendstor.feature.homepage.newsfeed.NewsFeedFragment;
import com.example.friendstor.feature.postupload.PostUploadActivity;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.feature.search.SearchActivity;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.model.friend.FriendResponse;
import com.example.friendstor.utils.ViewModelFactory;
import com.example.friendstor.utils.adapter.PostAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements FriendRequestAdapter.IPerformAction,
        PostAdapter.IUpdateUserReaction {

    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;

    private FloatingActionButton fab;

    private ImageView searchIcon;

    private ProgressBar progressBar;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelFactory()).get(MainViewModel.class);

        fab = findViewById(R.id.fab);
        searchIcon = findViewById(R.id.toolbar_search);
        progressBar = findViewById(R.id.progressBar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostUploadActivity.class));
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        bottomNavigationView = findViewById(R.id.navigation);
        friendsFragment = new FriendsFragment();
        newsFeedFragment = new NewsFeedFragment();
        setFragment(newsFeedFragment);
        setBottomNavigationView();


    }

    private void setBottomNavigationView() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.newsFeedFragment:
                        setFragment(newsFeedFragment);
                        return true;

                    case R.id.friendFragment:
                        setFragment(friendsFragment);
                        return true;

                    case R.id.profileActivity:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class)
                                .putExtra("uid", FirebaseAuth.getInstance().getUid()));
                        return false;

                }
                return true;
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void performAction(int position, String profileId, int operationType) {

        showProgressBar();

        viewModel.performAction(new ProfileActivity.PerformAction(operationType + "",
                FirebaseAuth.getInstance().getUid(),
                profileId)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                hideProgressBar();
                Toast.makeText(MainActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {

                    FriendResponse response = viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).getValue();
                    response.getResult().getRequests().remove(position);
                    viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).setValue(response);

                }
            }
        });
        Toast.makeText(MainActivity.this," generalResponse.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateUserReaction(String uid, int postId, String postOwnerId, String previousReactionType, String newReactionType, int adpaterPosition) {
        newsFeedFragment.updateUserReaction(uid, postId, postOwnerId, previousReactionType,newReactionType,adpaterPosition);
    }
}