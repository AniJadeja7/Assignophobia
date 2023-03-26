package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WhisperActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String API_KEY = "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";
    //private static final String API_URL = "https://api.openai.com/v1/engines/gpt-3.5-turbo/completions";
    // private static final String API_URL = "https://api.openai.com/v1/engines/gpt-3.5-turbo/chat";
    //private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final String TYPE = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_SEPARATOR = "/";
    private boolean isDelete;

    private static final String TAG = "WhisperActivity";
    private static final String TAG2 = "FlowWhisperActivity";
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

    private DatabaseReference databaseReference;
    private int whisperedNumber;

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType mediaType = MediaType.parse(TYPE);
    private static Request request;
    private static RequestBody body;

    private JSONObject requestBody;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper);

        init();


    }

    private void init() {
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
        databaseReference = database.getReference();
        publicPostsDatabaseReference = databaseReference.child("publicPosts");
        //DatabaseReference personalPostReference = databaseReference.child("users/" + Objects.requireNonNull(currentUser).getUid() + "/publicPosts");




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
                        dateEditText.setText(String.format("%02d/%02d/%04d", month1 + 1, dayOfMonth1, year1));
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    whisperedNumber = Integer.parseInt(Objects.requireNonNull(snapshot
                            .child("users/" + currentUser.getUid()).child("WhisperedNumber")
                            .getValue()).toString());
                    Log.d(TAG, "onDataChange: current whispered posts " + whisperedNumber);
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

    private String createPrompt() {
        Log.d(TAG2, "createPrompt: called..");
        String command = "Rephrase the following text : \n";
        String userInput = Objects.requireNonNull(assignmentEditText.getText()).toString();
       // String userInput = "\"" + "This is the final project of the Mobile Development Subject with subject code 8833102. " + "\"";
        Log.d(TAG2, "createPrompt: prompt created "+ command+userInput+"*");
       return command+userInput;
    }

    private void createPublicPost() {

        final String[] rephrasedAssignmentText = new String[1];
        getAIRephrasedText(new AICompletionCallback() {
            @Override
            public void onCompletion(String generatedText) {
                rephrasedAssignmentText[0] = generatedText;
            }
        });

        Log.d(TAG2, "createPublicPost: uploading Rephrased Text : "+rephrasedAssignmentText[0]);
        UploadTask uploadTask = publicPostsStorageReference.child(
                        Objects.requireNonNull(imageNameEditText.getText()) + "."
                                + imageExtension)
                .putFile(assignmentImageReference);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "createPostReference => getDownloadUrl => onSuccess: ");

                // Will create an UniqueId under "publicPosts" and will upload data under it.

                DatabaseReference newPostRef = publicPostsDatabaseReference.push();
                Map<String, String> postData = new HashMap<>();
                postData.put("assignmentImageReference", uri.toString());
                postData.put("assignmentText", rephrasedAssignmentText[0].trim());
                postData.put("displayUserName", currentUser.getDisplayName());
                postData.put("userProfileUrlReference", Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                newPostRef.setValue(postData);

                databaseReference.child("users/" + currentUser.getUid()).child("WhisperedNumber").setValue(whisperedNumber + 1);


            });

            Log.d(TAG, "createPostReference => onSuccess: Assignment Image Uploaded");
        });

    }

    private void getAIRephrasedText(AICompletionCallback callback) {
        final String[] generatedText = {""};

        // Define the request body for the API CALL...
        requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt",createPrompt());
            requestBody.put("temperature", 0);
            requestBody.put("max_tokens", 1500);
            requestBody.put("top_p", 1.0);
            requestBody.put("frequency_penalty", 0.2);
            requestBody.put("presence_penalty", 0.0);
            requestBody.put("stop", new JSONArray().put("*"));
            Log.d(TAG2, "init: requestBody initialized");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Define the request
        Log.d(TAG2, "init: request object Initialized");
        request = new Request.Builder()
                .url(API_URL)
                .addHeader(CONTENT_TYPE,TYPE)
                .addHeader(AUTHORIZATION, "Bearer " + API_KEY)
                .post(RequestBody.create(MediaType.parse(TYPE), requestBody.toString()))
                .build();


        try  {
            //requestBody.put("prompt", "Rephrase the statement:\n\nThis is the final project of the Mobile Application Development with subject code 8833102.*");

            Log.d(TAG2, "getAIRephrasedText: requestbody updated with promp");

            client.newCall(request).enqueue(new Callback() {;
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "getAIRephrased Text => onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseString = response.body().string();
                    Log.d(TAG2, "getAIRephrasedText => onResponse: "+responseString);
                    // Parse the response String as a JSONObject
                    JSONObject responseObject = null;
                    try {
                        responseObject = new JSONObject(responseString);
                    } catch (JSONException e) {
                        Log.d(TAG, "onResponse: "+e.getMessage());
                    }

                    // Get the generated text from the response
                    try {
                        generatedText[0] = responseObject.getJSONArray("choices").getJSONObject(0).getString("text");
                        generatedText[0] = generatedText[0].trim();
                        Log.d(TAG, "getAIRephrasedText: Returning Generated text"+generatedText[0]);
                        callback.onCompletion(generatedText[0]);
                        Log.d(TAG2, "getAIRephrasedText => onResponse: Receivied Text : \n"+ generatedText[0].trim());
                    } catch (JSONException e) {
                        Log.d(TAG2, "onResponse: "+e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void createErrorSnackBar(String msg, boolean isError) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green_500));
        if (isError) {
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red_500));
        }
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white_900));
        snackbar.show();

    }

    // Interface

    public interface AICompletionCallback {
        void onCompletion(String generatedText);
    }
}