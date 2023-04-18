package com.example.friendstor.feature.postupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.friendstor.R;
import com.example.friendstor.feature.auth.LoginActivity;
import com.example.friendstor.feature.homepage.MainActivity;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.feature.profile.ProfileViewModel;
import com.example.friendstor.model.GeneralResponse;
import com.example.friendstor.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUploadActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private AppCompatSpinner spinner;

    private PostUploadViewModel viewModel;
    private TextView postTxt;
    private TextInputEditText textInputEditText;

    private int privacy_level = 0;
    private ProgressDialog progressDialog;
    private Boolean isImageSelected = false;
    private ImageView addImage, previewImage;

    private File compressedImageFile = null;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        progressDialog = new ProgressDialog(PostUploadActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Uploading Post...Please wait");

        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelFactory()).get(PostUploadViewModel.class);

        postTxt = findViewById(R.id.postbtn);
        textInputEditText = findViewById(R.id.input_post);
        addImage = findViewById(R.id.addImage);
        previewImage = findViewById(R.id.imagePreview);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addImage.setOnClickListener(v ->
            selectImage()
        );
        previewImage.setOnClickListener(v ->
            selectImage()
        );

        spinner = findViewById(R.id.spinner_privacy);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) view;
                if (selectedTextView != null) {
                    selectedTextView.setTextColor(Color.WHITE);
                    selectedTextView.setTypeface(null, Typeface.BOLD);
                }
                privacy_level = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                privacy_level = 0;
            }
        });

        postTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = textInputEditText.getText().toString();
                String userId = FirebaseAuth.getInstance().getUid();

                if (status.trim().length() > 0 || isImageSelected) {

                    progressDialog.show();

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("post", status);
                    builder.addFormDataPart("postUserId", userId);
                    builder.addFormDataPart("privacy", privacy_level+"");

                    if(isImageSelected){
                        builder.addFormDataPart("file",
                                compressedImageFile.getName(),
                                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data"))
                        );
                    }

                    MultipartBody multipartBody = builder.build();

                    viewModel.uploadPost(multipartBody, false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                        @Override
                        public void onChanged(GeneralResponse generalResponse) {

                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.hide();
                            }

                            Log.i("TAG",  generalResponse.getMessage());
                            Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if(generalResponse.getStatus() == 200){
                                Intent intent =  new Intent(PostUploadActivity.this, MainActivity.class);
                                startActivity(intent);
//                                onBackPressed();
                            }
                        }
                    });

                } else {
                    Toast.makeText(PostUploadActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void selectImage() {
        ImagePicker.create(this).single().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ImagePicker.shouldHandle(requestCode,resultCode,data)){
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);
            try{
                compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                isImageSelected = true;
                addImage.setVisibility(View.GONE);
                previewImage.setVisibility(View.VISIBLE);
                Glide.with(PostUploadActivity.this)
                        .load(selectedImage.getPath())
                        .error(R.drawable.cover_picture_placeholder)
                        .placeholder(R.drawable.cover_picture_placeholder)
                        .into(previewImage);

//                uploadImage(compressedFile);
            } catch(IOException e){
                Toast.makeText(this, "Image Picker failed", Toast.LENGTH_SHORT).show();
                previewImage.setVisibility(View.GONE);
                addImage.setVisibility(View.VISIBLE);
            }
        }

    }
}