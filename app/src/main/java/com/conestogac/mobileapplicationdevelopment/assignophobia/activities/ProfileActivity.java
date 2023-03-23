package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.conestogac.mobileapplicationdevelopment.assignophobia.utils.user.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private FirebaseDatabase database;
    private StorageReference userDirectory;
    private String userProfileImageRef;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;

    private UserModel user;

    private TextInputEditText displayNameEditText;
    private TextInputEditText studentNumberEditText;
    private TextInputEditText collegeEditText;
    private TextInputEditText emailEditText;
    private ImageView profileView;
    private ImageView profileViewForeground;
    private TextView nameView;
    private TextView emailView;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveDataFromFirebase();
    }

    private void init() {
        user = new UserModel();
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.profile_view).setOnClickListener(this);
        findViewById(R.id.save_button).setOnClickListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        userDirectory = storageReference.child("users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        displayNameEditText = findViewById(R.id.student_name_edit_text);
        emailEditText = findViewById(R.id.student_email_edit_text);
        studentNumberEditText = findViewById(R.id.student_number_edit_text);
        emailEditText = findViewById(R.id.student_email_edit_text);

        profileView = findViewById(R.id.profile_view);
        emailView = findViewById(R.id.email_view);
        nameView = findViewById(R.id.name_view);
        profileViewForeground = findViewById(R.id.profile_view_foreground);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.back_button)
            onBackPressed();
        if (id == R.id.profile_view)
            selectProfileImage();
        if (id == R.id.save_button) {
            saveDataOnFirebase();
        }
    }

    private void saveDataOnFirebase() {



        user.setUserDisplayName(getData("displayName"));
        user.setUserEmail(getData("email"));

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getUserDisplayName())
                .setPhotoUri(user.getUserPhotoUri())
                .build();

        currentUser.updateProfile(profileUpdates).addOnFailureListener(e -> Log.d(TAG, "saveDataOnFirebase => currentUser.updateProfile => onFailure: "+e.getMessage())).addOnSuccessListener(unused -> {
            Log.d(TAG, "saveDataOnFirebase => currentUser.updateProfile => onSuccess: Profile Updated");
            createErrorSnackBar("Profile Update Success",false);
        });


        UploadTask photoUpload = userDirectory.child("ProfilePicture." + userProfileImageRef).putFile(user.getUserPhotoUri());

        photoUpload.addOnSuccessListener(taskSnapshot ->{

            Log.d(TAG, "saveDataOnFirebase=>onSuccess: photoUploaded");
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                user.setUserPhotoUri(uri);
                currentUser.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(uri).build()).addOnFailureListener(e -> Log.d(TAG, "saveDataOnFirebase => photoUpload => setPhotoURI => onFailure: "+e.getMessage()));
            });
        }).addOnFailureListener(e -> {
            Log.d(TAG, "saveDataOnFirebase=>onFailure: photoUploadFailed");
            Log.d(TAG, "saveDataOnFirebase=>onFailure: " + e.getMessage());
        });

    }

    private void retrieveDataFromFirebase(){
        Log.d(TAG, "retrieveDataFromFirebase: Display Name : "+currentUser.getDisplayName());
        nameView.setText(currentUser.getDisplayName());
        Log.d(TAG, "retrieveDataFromFirebase: Email : "+currentUser.getEmail());
        emailView.setText(currentUser.getEmail());
        Log.d(TAG, "retrieveDataFromFirebase: Image URI : "+currentUser.getPhotoUrl());
        profileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileViewForeground.setVisibility(View.GONE);
        Glide.with(this)
                .load(Objects.requireNonNull(currentUser.getPhotoUrl()).toString())
                .into(profileView);


    }

    ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {

            Log.d(TAG, "onActivityResult: called..");

            if (uri != null) {
                Log.d(TAG, "onActivityResult: uri is no null " + uri);
                // Do something with the URI, such as load it into an ImageView
                ContentResolver cR = getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = cR.getType(uri);
                userProfileImageRef = mime.getExtensionFromMimeType(type);
                // Do something with the URI and extension, such as load it into an ImageView

                ImageView imageView = findViewById(R.id.profile_view_foreground);
                imageView.setVisibility(View.GONE);
                imageView = findViewById(R.id.profile_view);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(uri);
                user.setUserPhotoUri(uri);
            } else {
                createErrorSnackBar("Profile Picture missing..",true);
                Log.d(TAG, "onActivityResult: uri is set to a space");
            }

        }
    });

    private void selectProfileImage() {
        // Launch the image picker
        pickImageLauncher.launch("image/*");
    }


    private String getData(String data) {

        String returnData = "";

        switch (data) {
            case "displayName":
                try {
                    returnData = Objects.requireNonNull(displayNameEditText.getText()).toString();
                    if (returnData.length() < 4 )
                        throw new NullPointerException();
                }catch (Exception e)
                {
                    // This does not cover all the spaces input and first name last name
                    createErrorSnackBar("Display Name must be at least 4 characters..",true);
                }

                break;
            case "studentNumber":
                try {
                    returnData = Objects.requireNonNull(studentNumberEditText.getText()).toString();
                    if (returnData.length() < 4 )
                        throw new NullPointerException();
                }catch (Exception e)
                {
                    // This does not cover all the spaces input and character input
                    createErrorSnackBar("Student Number must be at least 4 characters..",true);
                }
                break;
            case "College":
                try {
                    returnData = Objects.requireNonNull(collegeEditText.getText()).toString();
                    if (returnData.length() < 4 )
                        throw new NullPointerException();
                }catch (Exception e)
                {
                    // This does not cover all the spaces input and numeric input
                    createErrorSnackBar("Student Number must be at least 4 characters..",true);
                }
                break;
            case "email":
                try {
                    returnData = Objects.requireNonNull(emailEditText.getText()).toString();
                    if (returnData.length() < 4 )
                        throw new NullPointerException();
                }catch (Exception e)
                {
                    // This does not cover all the spaces input and numeric input
                    createErrorSnackBar("provide correct email format",true);
                }
                break;

        }


        return returnData;
    }

    private void createErrorSnackBar(String msg,boolean isError){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green_500));
        if (isError){
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red_500));
        }
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white_900));
        snackbar.show();

    }
}