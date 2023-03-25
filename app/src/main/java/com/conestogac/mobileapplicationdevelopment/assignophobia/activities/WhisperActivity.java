package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WhisperActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_SEPARATOR = "/";
    private boolean isDelete;

    private static final String TAG = "WhisperActivity";
    private static String imageExtension;
    private TextInputEditText dateEditText;
    private TextInputLayout dateLayout;
    private RoundedImageView assignmentImagePicker;

    private TextInputEditText imageNameEditText;
    private TextInputEditText assignmentEditText;

    private StorageReference publicPostsStorageReference;

    private DatabaseReference publicPostsDatabaseReference;
    private FirebaseUser currentUser;

    private Uri assignmentImageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper);

        init();



    }

    private void init(){
        dateLayout = findViewById(R.id.date_picker_layout);
        dateEditText = findViewById(R.id.date_picker_edit_text);
        assignmentImagePicker = findViewById(R.id.upload_button);
        assignmentImagePicker.setOnClickListener(this);
        imageNameEditText = findViewById(R.id.image_name_edit_text);
        assignmentEditText = findViewById(R.id.assignment_edit_text);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.post_button).setOnClickListener(this);



        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        this.currentUser = currentUser;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        publicPostsStorageReference = storageReference.child("publicPosts");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        publicPostsDatabaseReference = databaseReference.child("publicPosts");
        DatabaseReference personalPostReference = databaseReference.child("users/" + Objects.requireNonNull(currentUser).getUid() + "/publicPosts");




    }

    @Override
    protected void onStart() {
        super.onStart();
        // Add click listener to end icon to open date picker
        dateLayout.setEndIconOnClickListener(v -> {
            // Create a Calendar instance
            Calendar calendar = Calendar.getInstance();

            // Get the current year, month, and day
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(WhisperActivity.this,
                    (view, year1, month1, dayOfMonth1) -> {
                        // Set the selected date on the EditText
                        dateEditText.setText(String.format("%02d/%02d/%04d", month1 +1, dayOfMonth1, year1));
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

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
                imageExtension = mime.getExtensionFromMimeType(type);
                // Do something with the URI and extension, such as load it into an ImageView

                assignmentImagePicker.setBorderColor(getColor(R.color.white_900));
                Glide.with(WhisperActivity.this)
                        .load(uri)
                        .circleCrop()
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                        .into(assignmentImagePicker);

                assignmentImageReference = uri;

            } else {
                createErrorSnackBar("Profile Picture missing..",true);
                Log.d(TAG, "onActivityResult: uri is set to a space");
            }

        }
    });

    private void selectAssignmentImage() {
        // Launch the image picker
        pickImageLauncher.launch("image/*");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.back_button)
            onBackPressed();
        else if (id == R.id.upload_button)
            selectAssignmentImage();
        else if(id == R.id.post_button){
            createPublicPost();
            createPostReference();
        }
    }

    private void createPostReference() {


    }

    private void createPublicPost() {
        UploadTask uploadTask = publicPostsStorageReference.child(
                        Objects.requireNonNull(imageNameEditText.getText()) +"."
                                +imageExtension)
                .putFile(assignmentImageReference);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "createPostReference => getDownloadUrl => onSuccess: ");

                // Will create an UniqueId under "publicPosts" and will upload data under it.

                DatabaseReference newPostRef = publicPostsDatabaseReference.push();
                Map<String,String> postData = new HashMap<>();
                postData.put("assignmentImageReference",uri.toString());
                postData.put("assignmentText", Objects.requireNonNull(assignmentEditText.getText()).toString());
                postData.put("displayUserName",currentUser.getDisplayName());
                postData.put("userProfileUrlReference", Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                newPostRef.setValue(postData);

            });

            Log.d(TAG, "createPostReference => onSuccess: Assignment Image Uploaded");
        });

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