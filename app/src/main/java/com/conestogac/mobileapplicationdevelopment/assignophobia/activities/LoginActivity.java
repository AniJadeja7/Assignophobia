package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


private TextInputEditText usernameEditText;
private TextInputEditText passwordEditText;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    private void init(){

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        findViewById(R.id.login_button).setOnClickListener(this);
        if (mAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        findViewById(R.id.sign_up_text_view).setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            finish();
        });
    }

    private String[] getCredentials(){

        String tempUsername = Objects.requireNonNull(usernameEditText.getText()).toString();
        if (tempUsername.length() == 0){
            return null;
        }

        String tempPassword = Objects.requireNonNull(passwordEditText.getText()).toString();
        if (tempPassword.length() < 8)
            return null;
        return  new String[]{tempUsername,tempPassword};
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.login_button)
        {
            String[] credentials = getCredentials();
            if(credentials != null)
                login(credentials);

        }
    }

    private void login(String[] credentials) {

        mAuth.signInWithEmailAndPassword(credentials[0],credentials[1]).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
        });

    }
}