package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.user.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private FirebaseDatabase database;
    private StorageReference userDirectory;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;

    private UserModel user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init(){
        user = new UserModel();
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.profile_view).setOnClickListener(this);
        findViewById(R.id.save_button).setOnClickListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        userDirectory = storageReference.child("users/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.back_button)
            onBackPressed();
        if (id == R.id.profile_view)
            selectProfileImage();
        if (id == R.id.save_button)
            saveDataOnFirebase();
    }

    private void saveDataOnFirebase() {
        UploadTask photoUpload = userDirectory.putFile(user.getUserPhotoUri());

        photoUpload.addOnSuccessListener(taskSnapshot -> Log.d(TAG, "saveDataOnFirebase=>onSuccess: photoUploaded")).addOnFailureListener(e -> {
            Log.d(TAG, "saveDataOnFirebase=>onFailure: photoUploadFailed");
            Log.d(TAG, "saveDataOnFirebase=>onFailure: "+e.getMessage());
        });

    }

    private void setData(Uri profileUri, String displayName, String studentNumber, String college, String email){
        user.setUserEmail(email);
        user.setUserPhotoUri(profileUri);
        user.setUserDisplayName(displayName);
    }

    ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            // Do something with the URI, such as load it into an ImageView
            ImageView imageView = findViewById(R.id.profile_view_foreground);
            imageView.setVisibility(View.GONE);
            imageView = findViewById(R.id.profile_view);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(uri);
            user.setUserPhotoUri(uri);
        }
    });

    private void selectProfileImage() {
        // Launch the image picker
        pickImageLauncher.launch("image/*");
    }
}