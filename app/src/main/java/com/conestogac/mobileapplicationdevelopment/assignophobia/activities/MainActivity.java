package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.Item;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.PostAdapter;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.user.UserModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Item> items = new ArrayList<>();
    NestedScrollView nestedScrollView;

    private FloatingActionButton whisperButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        checkUserStatus();
        fetchAssignments();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PostAdapter(getApplicationContext(),items));
        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0));
    }

    private void checkUserStatus() {
        UserModel user = new UserModel();

        user.setUserId(currentUser.getUid());
        user.setUserDisplayName(currentUser.getDisplayName());
        user.setUserEmail(currentUser.getEmail());
        user.setUserPhotoUri(currentUser.getPhotoUrl());

        if (user.getUserDisplayName() == null)
        {
            Log.d(TAG, "checkUserStatus: display name is not set");
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
    }

    private void fetchAssignments() {

    }

    private void addItems(Bitmap profilePic, Bitmap assignmentImage, String username, String assignmentText){
        Item item = new Item(profilePic,assignmentImage,username,assignmentText);
        items.add(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser == null)
        {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

    }

    private void init(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // setting flags will remove all the activities from the backstack
                startActivity(new Intent(MainActivity.this,LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });


        recyclerView = findViewById(R.id.posts_recycler_view);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        whisperButton = findViewById(R.id.whisper_button);

        whisperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class
                ));
            }
        });

    }
}