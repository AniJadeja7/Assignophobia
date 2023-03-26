package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.Item;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.PostAdapter;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.user.UserModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final List<Item> items = new ArrayList<>();
    private NestedScrollView nestedScrollView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference publicPosts;
    private PostAdapter adapter;

    private TextView greetingsView;

    private static final String TAG = "MainActivity";


    private RoundedImageView profileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        checkUserStatus();
        fetchAssignments();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0));
    }

    private void checkUserStatus() {
        UserModel user = new UserModel();

        user.setUserId(currentUser.getUid());
        user.setUserDisplayName(currentUser.getDisplayName());
        user.setUserEmail(currentUser.getEmail());
        user.setUserPhotoUri(currentUser.getPhotoUrl());

        Log.d(TAG, "checkUserStatus: display name "+ user.getUserDisplayName());

        if (user.getUserDisplayName() == null || Objects.equals(user.getUserDisplayName(), ""))
        {
            Log.d(TAG, "checkUserStatus: display name is not set");
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        if (user.getUserPhotoUri()!=null)
        {
            Glide.with(this).load(user.getUserPhotoUri()).into(profileButton);
        }
        if (user.getUserDisplayName() != null)
        {
            greetingsView.setText("Hello, Mr. "+user.getUserDisplayName());
        }
    }

    private void fetchAssignments() {

    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser == null)
        {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

        publicPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                Log.d(TAG, "onDataChange: "+snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    // Get the data for each post
                    String postId = postSnapshot.getKey();
                    String name = postSnapshot.child("displayUserName").getValue(String.class);
                    String userPhotoUrl = postSnapshot.child("userProfileUrlReference").getValue(String.class);
                    String assignmentImageUrl = postSnapshot.child("assignmentImageReference").getValue(String.class);
                    String assignmentText = postSnapshot.child("assignmentText").getValue(String.class);



                    // Add data to the items
                    Item item = new Item(userPhotoUrl,assignmentImageUrl,name,assignmentText);
                        items.add(item);


                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        findViewById(R.id.logout_button).setOnClickListener(view -> {
            mAuth.signOut();
            // setting flags will remove all the activities from the backstack
            startActivity(new Intent(MainActivity.this,LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });


        recyclerView = findViewById(R.id.posts_recycler_view);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        FloatingActionButton whisperButton = findViewById(R.id.whisper_button);

        whisperButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,WhisperActivity.class)));

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,ProfileActivity.class)));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        publicPosts = databaseReference.child("publicPosts");

        adapter = new PostAdapter(getApplicationContext(),items);

        greetingsView = findViewById(R.id.greetings_view);
    }
}