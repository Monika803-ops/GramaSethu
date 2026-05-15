package com.monika.gramasethu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    EditText bridgeNameInput,
            villageInput,
            notesInput;

    Spinner statusSpinner;

    Button submitReportButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("BridgeReports");

        bridgeNameInput =
                findViewById(R.id.bridgeNameInput);

        villageInput =
                findViewById(R.id.villageInput);

        statusSpinner =
                findViewById(R.id.statusSpinner);

        notesInput =
                findViewById(R.id.notesInput);

        submitReportButton =
                findViewById(R.id.submitReportButton);

        // Spinner Status List
        String[] statusList = {
                "Select Status",
                "🟢 Open",
                "🟡 Damaged",
                "🔴 Submerged"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        statusList
                );

        statusSpinner.setAdapter(adapter);

        submitReportButton.setOnClickListener(v -> {

            String bridge =
                    bridgeNameInput.getText().toString().trim();

            String village =
                    villageInput.getText().toString().trim();

            String status =
                    statusSpinner.getSelectedItem().toString();

            String notes =
                    notesInput.getText().toString().trim();

            // Current Date & Time
            String currentDateTime =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault())
                            .format(new Date());

            if (bridge.isEmpty()
                    || village.isEmpty()
                    || status.equals("Select Status")) {

                Toast.makeText(this,
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT).show();

            }

            else {

                String reportId =
                        databaseReference.push().getKey();

                databaseReference.child(reportId)
                        .child("bridgeName")
                        .setValue(bridge);

                databaseReference.child(reportId)
                        .child("village")
                        .setValue(village);

                databaseReference.child(reportId)
                        .child("status")
                        .setValue(status);

                databaseReference.child(reportId)
                        .child("notes")
                        .setValue(notes);

                databaseReference.child(reportId)
                        .child("updatedTime")
                        .setValue(currentDateTime);

                Toast.makeText(this,
                        "Report Saved to Firebase",
                        Toast.LENGTH_SHORT).show();

                // Clear Fields
                bridgeNameInput.setText("");
                villageInput.setText("");
                notesInput.setText("");

            }

        });

    }
}