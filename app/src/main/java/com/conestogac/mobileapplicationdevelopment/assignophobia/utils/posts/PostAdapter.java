package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;

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
        holder.assignmentImage.setImageResource(items.get(position).getAssignmentImage());
        holder.profile.setImageResource(items.get(position).getProfile());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
