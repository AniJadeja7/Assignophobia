package com.conestogac.mobileapplicationdevelopment.assignophobia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.Item;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts.PostAdapter;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        recyclerView = findViewById(R.id.posts_recycler_view);

        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));
        items.add(new Item(R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,"Hannah","Assignment"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PostAdapter(getApplicationContext(),items));

        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0));

    }
}