package com.conestogac.mobileapplicationdevelopment.assignophobia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.conestogac.mobileapplicationdevelopment.assignophobia.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WhisperActivity extends AppCompatActivity {

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_SEPARATOR = "/";
    private boolean isDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper);

        TextInputLayout dateLayout = findViewById(R.id.date_picker_layout);
        TextInputEditText dateEditText = findViewById(R.id.date_picker_edit_text);


        // Add click listener to end icon to open date picker
        dateLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Calendar instance
                Calendar calendar = Calendar.getInstance();

                // Get the current year, month, and day
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(WhisperActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date on the EditText
                                dateEditText.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });



    }
}