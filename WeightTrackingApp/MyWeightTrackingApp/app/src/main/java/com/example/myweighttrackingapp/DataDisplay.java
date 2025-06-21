package com.example.myweighttrackingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class DataDisplay extends AppCompatActivity {

    // Sends SMS when goal is reached
    private void sendGoalSMS(String message) {
        String phoneNumber = "5551234567"; // Placeholder number (replace if needed)

        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    // UI elements
    private RecyclerView recyclerView;
    private EditText editTextDate, editTextWeight;
    private Button buttonAdd;

    // Helpers and data
    private DatabaseHelper dbHelper;
    private WeightData adapter;
    private List<WeightEntry> weightList;

    // User info
    private String username;

    // Weight goal for triggering SMS
    private static final double GOAL_WEIGHT = 200.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Get the username from the login screen (default to "demo" if missing)
        username = getIntent().getStringExtra("username");
        if (username == null) username = "demo";

        // Link UI elements to layout
        recyclerView = findViewById(R.id.recyclerViewGrid);
        editTextDate = findViewById(R.id.editTextDate);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonAdd = findViewById(R.id.buttonAddEntry);

        // Set up database and RecyclerView
        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData(); // Load saved weight entries

        // Add button click logic
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = editTextDate.getText().toString().trim();
                String weightStr = editTextWeight.getText().toString().trim();

                // Check if fields are filled
                if (date.isEmpty() || weightStr.isEmpty()) {
                    Toast.makeText(DataDisplay.this, "Enter date and weight", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double weight = Double.parseDouble(weightStr);
                    boolean inserted = dbHelper.addWeight(username, date, weight);

                    if (inserted) {
                        Toast.makeText(DataDisplay.this, "Entry added", Toast.LENGTH_SHORT).show();

                        // Clear fields after adding
                        editTextDate.setText("");
                        editTextWeight.setText("");

                        // Refresh list
                        loadData();

                        // If user reached or beat goal weight, send SMS
                        if (weight <= GOAL_WEIGHT) {
                            sendGoalSMS("Congrats! You've reached your goal weight of " + weight + " lbs.");
                        }

                    } else {
                        Toast.makeText(DataDisplay.this, "Insert failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DataDisplay.this, "Invalid weight value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Loads all weight entries and updates the list
    private void loadData() {
        weightList = dbHelper.getAllWeights(username);
        adapter = new WeightData(weightList, dbHelper);
        recyclerView.setAdapter(adapter);
    }
}