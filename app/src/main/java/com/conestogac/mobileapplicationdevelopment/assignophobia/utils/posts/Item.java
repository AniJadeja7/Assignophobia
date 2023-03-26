package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

import android.graphics.Bitmap;
import android.net.Uri;

public class Item {

    private String profile, assignmentImage;
    String userName, assignmentText;

    public Item(String profile, String assignmentImage, String userName, String assignmentText) {
        this.profile = profile;
        this.assignmentImage = assignmentImage;
        this.userName = userName;
        this.assignmentText = assignmentText;
    }

    public String getProfile() {
        return profile;
    }

    public String getAssignmentImage() {
        return assignmentImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getAssignmentText() {
        return assignmentText;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setAssignmentImage(String assignmentImage) {
        this.assignmentImage = assignmentImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAssignmentText(String assignmentText) {
        this.assignmentText = assignmentText;
    }
}
