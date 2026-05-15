package com.monika.gramasethu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText mobileInput, passwordInput;
    Button loginButton;
    TextView registerText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileInput =
                findViewById(R.id.mobileInput);

        passwordInput =
                findViewById(R.id.passwordInput);

        loginButton =
                findViewById(R.id.loginButton);

        registerText =
                findViewById(R.id.registerText);

        // Firebase Reference
        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("Users");

        loginButton.setOnClickListener(v -> {

            String mobile =
                    mobileInput.getText().toString().trim();

            String password =
                    passwordInput.getText().toString().trim();

            // Empty Validation
            if(mobile.isEmpty() || password.isEmpty()) {

                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();

            }

            else {

                databaseReference.child(mobile)
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {

                                    @Override
                                    public void onDataChange(
                                            @NonNull DataSnapshot snapshot) {

                                        // User Exists
                                        if(snapshot.exists()) {

                                            String firebasePassword =
                                                    snapshot.child("password")
                                                            .getValue(String.class);

                                            // Password Match
                                            if(password.equals(firebasePassword)) {

                                                Toast.makeText(
                                                        LoginActivity.this,
                                                        "Login Successful",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                Intent intent =
                                                        new Intent(
                                                                LoginActivity.this,
                                                                HomeActivity.class
                                                        );

                                                startActivity(intent);

                                                finish();

                                            }

                                            else {

                                                Toast.makeText(
                                                        LoginActivity.this,
                                                        "Wrong Password",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                            }

                                        }

                                        else {

                                            Toast.makeText(
                                                    LoginActivity.this,
                                                    "User Not Found",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(
                                            @NonNull DatabaseError error) {

                                        Toast.makeText(
                                                LoginActivity.this,
                                                "Database Error",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    }

                                });

            }

        });

        // Open Register Screen
        registerText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(LoginActivity.this,
                            RegisterActivity.class);

            startActivity(intent);

        });

    }
}