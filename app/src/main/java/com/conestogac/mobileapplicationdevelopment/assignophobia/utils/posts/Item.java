package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

import android.graphics.Bitmap;

public class Item {

    private Bitmap profile, assignmentImage;
    String userName, assignmentText;

    public Item(Bitmap profile, Bitmap assignmentImage, String userName, String assignmentText) {
        this.profile = profile;
        this.assignmentImage = assignmentImage;
        this.userName = userName;
        this.assignmentText = assignmentText;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public Bitmap getAssignmentImage() {
        return assignmentImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getAssignmentText() {
        return assignmentText;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public void setAssignmentImage(Bitmap assignmentImage) {
        this.assignmentImage = assignmentImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAssignmentText(String assignmentText) {
        this.assignmentText = assignmentText;
    }
}
