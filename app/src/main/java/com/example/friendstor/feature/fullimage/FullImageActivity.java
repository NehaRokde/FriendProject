package com.example.friendstor.feature.fullimage;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.friendstor.R;
import com.example.friendstor.data.remote.ApiClient;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        String image = getIntent().getStringExtra("imageUrl");
        Uri imageUri = Uri.parse(image);
        PhotoView photoView = findViewById(R.id.photoview);
        if (imageUri.getAuthority() == null) {
            image = ApiClient.BASE_URL + image;
        }

        Glide.with(this).load(image).into(photoView);

    }

}