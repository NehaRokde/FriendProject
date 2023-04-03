package com.example.friendstor.feature.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.friendstor.R;
import com.example.friendstor.feature.homepage.friends.FriendsFragment;
import com.example.friendstor.feature.homepage.newsfeed.NewsFeedFragment;
import com.example.friendstor.feature.postupload.PostUploadActivity;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.feature.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;

    private FloatingActionButton fab;

    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab = findViewById(R.id.fab);
        searchIcon = findViewById(R.id.toolbar_search);

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
}