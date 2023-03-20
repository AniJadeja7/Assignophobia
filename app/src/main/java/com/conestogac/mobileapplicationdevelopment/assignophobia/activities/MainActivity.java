package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.Item;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.PostAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Item> items = new ArrayList<>();


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        recyclerView = findViewById(R.id.posts_recycler_view);

        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PostAdapter(getApplicationContext(),items));

        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0));

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
}