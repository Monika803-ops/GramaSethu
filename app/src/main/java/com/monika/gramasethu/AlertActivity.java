package com.monika.gramasethu;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlertActivity extends AppCompatActivity {

    TextView alertText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        alertText =
                findViewById(R.id.alertText);

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("BridgeReports");

        databaseReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        StringBuilder alerts =
                                new StringBuilder();

                        for(DataSnapshot dataSnapshot
                                : snapshot.getChildren()) {

                            String bridgeName =
                                    dataSnapshot.child("bridgeName")
                                            .getValue(String.class);

                            String village =
                                    dataSnapshot.child("village")
                                            .getValue(String.class);

                            String status =
                                    dataSnapshot.child("status")
                                            .getValue(String.class);

                            // SHOW ONLY DAMAGED & SUBMERGED
                            if(status.equalsIgnoreCase("Damaged")
                                    || status.equalsIgnoreCase("Submerged")) {

                                alerts.append("Bridge: ")
                                        .append(bridgeName)
                                        .append("\n");

                                alerts.append("Status: ")
                                        .append(status)
                                        .append("\n");

                                alerts.append("Village: ")
                                        .append(village)
                                        .append("\n\n");
                            }

                        }

                        if(alerts.length() == 0) {

                            alertText.setText(
                                    "No danger alerts found.");

                        }

                        else {

                            alertText.setText(alerts.toString());

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }

                });

    }
}