package com.conestogac.mobileapplicationdevelopment.assignophobia.utils.user;

import android.net.Uri;

public class UserModel {

    private String userEmail;
    private String userDisplayName;
    private String userId;
    private Uri userPhotoUri;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Uri getUserPhotoUri() {
        return userPhotoUri;
    }

    public void setUserPhotoUri(Uri userPhotoUri) {
        this.userPhotoUri = userPhotoUri;
    }

}
