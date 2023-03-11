package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;

public class PostHolder extends RecyclerView.ViewHolder {
    ImageView profile, assignmentImage;
    TextView userName, assignmentText;

    public PostHolder(@NonNull View itemView) {
        super(itemView);

        profile = itemView.findViewById(R.id.card_profile_view);
        assignmentImage = itemView.findViewById(R.id.card_assignment_image_view);
        userName = itemView.findViewById(R.id.card_username_view);
        assignmentText = itemView.findViewById(R.id.card_assignment_text_view);
    }
}
