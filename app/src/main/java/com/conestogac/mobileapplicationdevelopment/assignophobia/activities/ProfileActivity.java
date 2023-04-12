package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import android.content.ContentResolver;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private StorageReference userDirectory;
    private String userProfileImageRef;
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
    private TextView whisperedNumberView;
    private TextView memedNumberView;

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


        profileView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String imageUri = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();


                Intent intent = new Intent(ProfileActivity.this, FullScreenImage.class);
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
                return true;
            }
        });
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        userReference = databaseReference.child("users/"+currentUser.getUid());

        displayNameEditText = findViewById(R.id.student_name_edit_text);
        emailEditText = findViewById(R.id.student_email_edit_text);
        studentNumberEditText = findViewById(R.id.student_number_edit_text);
        collegeEditText = findViewById(R.id.college_edit_text);

        profileView = findViewById(R.id.profile_view);
        emailView = findViewById(R.id.email_view);
        nameView = findViewById(R.id.name_view);
        profileViewForeground = findViewById(R.id.profile_view_foreground);

        whisperedNumberView = findViewById(R.id.whispered_number);
        memedNumberView = findViewById(R.id.memed_number);

        findViewById(R.id.invite_button).setOnClickListener(this);
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
        if (id == R.id.invite_button){
            startActivity(new Intent(ProfileActivity.this,InviteActivity.class));
        }

    }
    private void saveDataOnFirebase() {
        user.setUserEmail(getData("email"));

        if (!user.getUserPhotoUri().equals(currentUser.getPhotoUrl()) || !user.getUserPhotoUri().toString().equals("null"))
        {
            UploadTask photoUpload = userDirectory.child("ProfilePicture." + userProfileImageRef).putFile(user.getUserPhotoUri());
            photoUpload.addOnSuccessListener(taskSnapshot ->{

                Log.d(TAG, "saveDataOnFirebase=>onSuccess: photoUploaded");
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    user.setUserPhotoUri(uri);
                    currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build()).addOnSuccessListener(unused ->
                                    Log.d(TAG, "saveDataOnFirebase => photoUpload => setPhotoURI => onSuccess: photo uri set "+uri))
                            .addOnFailureListener(e -> Log.d(TAG, "saveDataOnFirebase => photoUpload => setPhotoURI => onFailure: "+e.getMessage()));
                });
            }).addOnFailureListener(e -> {
                Log.d(TAG, "saveDataOnFirebase=>onFailure: photoUploadFailed");
                Log.d(TAG, "saveDataOnFirebase=>onFailure: " + e.getMessage());
            });
        }
        else {
            createErrorSnackBar("Profile Picture is missing...",true);
            return;
        }
        currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(getData("displayName"))
                        .build()).addOnSuccessListener(unused ->
                        Log.d(TAG, "saveDataOnFirebase => onSuccess: Name Updated "))
                .addOnFailureListener(e -> Log.d(TAG, "saveDataOnFirebase => photoUpload => setPhotoURI => onFailure: "+e.getMessage()));

        updateDataOnDatabase();
    }

    private void updateDataOnDatabase(){
        userReference.child("Student Number").setValue(getData("studentNumber"));
        userReference.child("College").setValue(getData("College"));
    }

    private void retrieveDataFromFirebase(){


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                try {

                    // student number is null when user signs up. So, it returns null
                    studentNumberEditText.setText(Objects.requireNonNull(snapshot.child("Student Number").getValue()).toString());
                    collegeEditText.setText(Objects.requireNonNull(snapshot.child("College").getValue()).toString());
                    displayNameEditText.setText(currentUser.getDisplayName());
                    emailEditText.setText(currentUser.getEmail());


                    whisperedNumberView.setText(Objects.requireNonNull(snapshot.child("WhisperedNumber").getValue()).toString());
                    memedNumberView.setText(Objects.requireNonNull(snapshot.child("MemedNumber").getValue()).toString());
                }
                catch (NullPointerException ne)
                {
                    userReference.child("WhisperedNumber").setValue("0");
                    userReference.child("MemedNumber").setValue("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.d(TAG, "retrieveDataFromFirebase: Display Name : "+currentUser.getDisplayName());
        nameView.setText(currentUser.getDisplayName());
        Log.d(TAG, "retrieveDataFromFirebase: Email : "+currentUser.getEmail());
        emailView.setText(currentUser.getEmail());




        try {
            Uri photoUri = currentUser.getPhotoUrl();
            Log.d(TAG, "retrieveDataFromFirebase: Image URI : "+photoUri);
            user.setUserPhotoUri(photoUri);
            profileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profileViewForeground.setVisibility(View.GONE);
            Glide.with(this)
                    .load(Objects.requireNonNull(photoUri).toString())
                    .into(profileView);
        }
        catch (Exception e)
        {
            // what happens if the uri is not received from firebase.
            user.setUserPhotoUri(Uri.parse("null"));
            Log.d(TAG, "retrieveDataFromFirebase: Could not get URI from firebase "+e.getMessage());
        }




    }

    ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {

            Log.d(TAG, "onActivityResult: called..");

            if (uri != null ) {
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
                    createErrorSnackBar("College name must be at least 4 characters..",true);
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