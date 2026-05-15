package com.monika.gramasethu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText fullNameInput,
            mobileRegisterInput,
            villageInput,
            passwordRegisterInput,
            confirmPasswordInput;

    Button registerButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameInput =
                findViewById(R.id.fullNameInput);

        mobileRegisterInput =
                findViewById(R.id.mobileRegisterInput);

        villageInput =
                findViewById(R.id.villageInput);

        passwordRegisterInput =
                findViewById(R.id.passwordRegisterInput);

        confirmPasswordInput =
                findViewById(R.id.confirmPasswordInput);

        registerButton =
                findViewById(R.id.registerButton);

        // Firebase Reference
        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("Users");

        registerButton.setOnClickListener(v -> {

            String fullName =
                    fullNameInput.getText().toString().trim();

            String mobile =
                    mobileRegisterInput.getText().toString().trim();

            String village =
                    villageInput.getText().toString().trim();

            String password =
                    passwordRegisterInput.getText().toString().trim();

            String confirmPassword =
                    confirmPasswordInput.getText().toString().trim();

            // Empty Fields Check
            if(fullName.isEmpty()
                    || mobile.isEmpty()
                    || village.isEmpty()
                    || password.isEmpty()
                    || confirmPassword.isEmpty()) {

                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();

            }

            // Mobile Number Check
            else if(mobile.length() != 10) {

                Toast.makeText(this,
                        "Enter valid 10-digit mobile number",
                        Toast.LENGTH_SHORT).show();

            }

            // Password Validation
            else if(password.length() < 6
                    || !password.matches(".*[A-Z].*")
                    || !password.matches(".*[0-9].*")) {

                Toast.makeText(this,
                        "Password must contain 6 characters, 1 uppercase letter and 1 number",
                        Toast.LENGTH_LONG).show();

            }

            // Confirm Password Check
            else if(!password.equals(confirmPassword)) {

                Toast.makeText(this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT).show();

            }

            else {

                // Create User Data
                HashMap<String, String> userMap =
                        new HashMap<>();

                userMap.put("fullName", fullName);
                userMap.put("mobile", mobile);
                userMap.put("village", village);
                userMap.put("password", password);

                // Save Data to Firebase
                databaseReference.child(mobile)
                        .setValue(userMap)
                        .addOnCompleteListener(task -> {

                            if(task.isSuccessful()) {

                                Toast.makeText(this,
                                        "Registration Successful",
                                        Toast.LENGTH_SHORT).show();

                                // Go To Login Screen
                                Intent intent =
                                        new Intent(RegisterActivity.this,
                                                LoginActivity.class);

                                startActivity(intent);

                                finish();

                            }
                            else {

                                Toast.makeText(this,
                                        "Registration Failed",
                                        Toast.LENGTH_SHORT).show();

                            }

                        });

            }

        });

    }
}