package com.fahad.tech.venture.FahadTechVentureDemoLogin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.AppDatabase;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.User;
import com.github.ybq.android.spinkit.SpinKitView; // Import SpinKitView

public class SignupScreen extends AppCompatActivity {

    private EditText etUsername, etFirstName, etLastName, etEmail, etPassword;
    private Button btnSignup;
    private SpinKitView spinKit; // Declare SpinKitView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        // Initialize all fields
        etUsername = findViewById(R.id.etUsername);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        spinKit = findViewById(R.id.spin_kit); // Initialize SpinKitView

        // Set click listener for the Sign Up button
        btnSignup.setOnClickListener(v -> {
            if (validateFields()) {
                saveUser();
            }
        });
    }

    private boolean validateFields() {
        String username = etUsername.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Username validation
        if (username.isEmpty() || username.length() < 4) {
            Toast.makeText(this, "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // First Name validation
        if (firstName.isEmpty() || !firstName.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "First Name must contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Last Name validation
        if (lastName.isEmpty() || !lastName.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "Last Name must contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Email validation
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password validation
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Additional checks can be added as needed
        return true; // All validations passed
    }

    private void saveUser() {
        // Disable button and edit text fields
        toggleInteraction(false);

        // Show the SpinKit progress bar
        spinKit.setVisibility(View.VISIBLE);

        User newUser = new User(
                etUsername.getText().toString().trim(),
                etFirstName.getText().toString().trim(),
                etLastName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString().trim(),
                false, // Regular user
                System.currentTimeMillis()
        );

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.userDao().insertUser(newUser);

            runOnUiThread(() -> {
                // Delay for 2 seconds
                new Handler().postDelayed(() -> {
                    // Hide the SpinKit progress bar
                    spinKit.setVisibility(View.GONE);

                    // Enable button and edit text fields
                    toggleInteraction(true);

                    Toast.makeText(SignupScreen.this, "User signed up successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close SignupActivity and return to LoginActivity
                }, 2000); // 2000 milliseconds = 2 seconds
            });
        }).start();
    }

    private void toggleInteraction(boolean enable) {
        // Enable or disable the button
        btnSignup.setEnabled(enable);

        // Enable or disable the EditText fields
        etUsername.setEnabled(enable);
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etPassword.setEnabled(enable);

        // Optionally, change the button opacity for a better user experience
        btnSignup.setAlpha(enable ? 1f : 0.5f);
    }


}
