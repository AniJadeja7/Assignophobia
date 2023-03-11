package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.posts;

public class Item {

    int profile, assignmentImage;
    String userName, assignmentText;

    public Item(int profile, int assignmentImage, String userName, String assignmentText) {
        this.profile = profile;
        this.assignmentImage = assignmentImage;
        this.userName = userName;
        this.assignmentText = assignmentText;
    }

    public int getProfile() {
        return profile;
    }

    public int getAssignmentImage() {
        return assignmentImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getAssignmentText() {
        return assignmentText;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public void setAssignmentImage(int assignmentImage) {
        this.assignmentImage = assignmentImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAssignmentText(String assignmentText) {
        this.assignmentText = assignmentText;
    }
}
