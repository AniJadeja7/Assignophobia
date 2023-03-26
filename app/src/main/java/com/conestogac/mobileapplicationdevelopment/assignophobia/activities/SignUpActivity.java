package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton signUp;
    private String inviteCode;

    private TextInputEditText usernameEdiText;
    private TextInputEditText inviteCodeEdiText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;



    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();

        mAuth = FirebaseAuth.getInstance();


        signUp.setOnClickListener(this);
    }


    private void init(){
        signUp = findViewById(R.id.sign_up_button);
        usernameEdiText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        inviteCodeEdiText = findViewById(R.id.invite_code_edit_text);

        findViewById(R.id.login_text_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id==R.id.sign_up_button)
        {

            String username = Objects.requireNonNull(usernameEdiText.getText()).toString();
            String password;
            inviteCode = Objects.requireNonNull(inviteCodeEdiText.getText()).toString();

            if (Objects.requireNonNull(passwordEditText.getText()).toString().equals(Objects.requireNonNull(confirmPasswordEditText.getText()).toString()))
            {
                password = passwordEditText.getText().toString();
                createUser(username, password);

            }
            else {
                confirmPasswordEditText.setError("Passwords do not match..");
            }
        } else if (id == R.id.login_text_view) {
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            finish();
        }
    }

    private void createUser(String username,String password){

        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                navigateToLoginActivity();
            }
            else {
                Log.d("SignUpActivity", "onComplete: creating user task failed : "+ Objects.requireNonNull(task.getException()).getMessage());
            }
        }).addOnFailureListener(e -> Log.d("SignUpActivity", "onFail : user creation failed : "+ e.getMessage()));

    }

    /*
    * List all contents of the userDirectory
    *
    * StorageReference folderRef = storageRef.child("myFolder/");

    folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
    @Override
    public void onSuccess(ListResult listResult) {
        // The folder exists and contains at least one item
        }
    }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        // The folder does not exist or an error occurred
        }
    });
    *
    *
    *
    * */

    private void navigateToLoginActivity(){
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }
}