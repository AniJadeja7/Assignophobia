package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;

import java.util.Random;

public class InviteActivity extends AppCompatActivity {

    TextView inviteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteView = findViewById(R.id.invite_code);
        inviteView.append(getRandomNumberString());

        findViewById(R.id.back_button).setOnClickListener(view -> {
           onBackPressed();
        });

    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}