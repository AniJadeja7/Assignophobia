package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.jsibbold.zoomage.ZoomageView;

public class FullScreenImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Get the image URI from the intent
        String imageUri = getIntent().getStringExtra("imageUri");

        // Load the image into the ImageView
        ZoomageView imageView = findViewById(R.id.full_screen_image);
        Glide.with(this).load(imageUri).into(imageView);
    }
}