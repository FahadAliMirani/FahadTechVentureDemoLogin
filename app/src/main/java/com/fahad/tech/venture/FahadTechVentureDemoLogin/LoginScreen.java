package com.fahad.tech.venture.FahadTechVentureDemoLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.AppDatabase;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.User;
import com.github.ybq.android.spinkit.SpinKitView;

public class LoginScreen extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvSignup;
    private Button btnLogin;
    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String username = sharedPreferences.getString("username", null);
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            Intent intent;
            if (isAdmin) {
                intent = new Intent(LoginScreen.this, AdminScreen.class);
            } else {
                intent = new Intent(LoginScreen.this, UserProfile.class);
            }
            intent.putExtra("username", username);
            startActivity(intent);
            finish(); // Close LoginScreen
        }

        // Initialize fields
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        spinKitView = findViewById(R.id.spin_kit);

        // Set click listener for the Login button
        btnLogin.setOnClickListener(v -> {
            hideKeyboard();

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginScreen.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                disableLoginFields();
                loginUser(username, password);
            }
        });

        // Set click listener for the Sign Up button
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, SignupScreen.class);
            startActivity(intent);
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loginUser(String username, String password) {
        // Show the spinner
        spinKitView.setVisibility(View.VISIBLE);

        // Introduce a delay of 2 seconds
        new Handler().postDelayed(() -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                final User user;

                // Check for admin credentials
                if (username.equals("admin") && password.equals("admin123")) {
                    user = new User("admin", "", "", "", "", true,System.currentTimeMillis());
                } else {
                    user = db.userDao().login(username, password);
                }

                runOnUiThread(() -> {
                    // Hide the spinner
                    spinKitView.setVisibility(View.GONE);
                    enableLoginFields();

                    if (user != null) {
                        // Successful login
                        Toast.makeText(LoginScreen.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Save login state to SharedPreferences
                        saveLoginState(username, user.isAdmin);

                        // Navigate to the appropriate activity
                        Intent intent;
                        if (user.isAdmin) {
                            intent = new Intent(LoginScreen.this, AdminScreen.class);
                        } else {
                            intent = new Intent(LoginScreen.this, UserProfile.class);
                        }
                        intent.putExtra("username", user.username);
                        startActivity(intent);
                        finish(); // Close LoginScreen
                    } else {
                        // Login failed
                        Toast.makeText(LoginScreen.this, "Invalid credentials, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }, 2000); // 2000 milliseconds = 2 seconds
    }

    private void saveLoginState(String username, boolean isAdmin) {
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isAdmin", isAdmin); // Save admin state
        editor.apply();
    }

    private void disableLoginFields() {
        // Disable fields and show spinner while login is in progress
        btnLogin.setEnabled(false);
        tvSignup.setEnabled(false);
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);

        btnLogin.setAlpha(0.5f);

    }

    private void enableLoginFields() {
        // Re-enable fields after login process is complete
        btnLogin.setEnabled(true);
        tvSignup.setEnabled(true);
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);

        btnLogin.setAlpha(1f);

    }
}
