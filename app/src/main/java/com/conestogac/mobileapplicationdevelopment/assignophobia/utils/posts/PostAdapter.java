package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;

import java.net.ConnectException;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    Context context;
    List<Item> items;


    public PostAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(context).inflate(R.layout.item_view_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.assignmentText.setText(items.get(position).getAssignmentText());
        holder.userName.setText(items.get(position).getUserName());

        Glide.with(context).load(items.get(position).getAssignmentImage()).into(holder.assignmentImage);
        Glide.with(context).load(items.get(position).getProfile()).into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
