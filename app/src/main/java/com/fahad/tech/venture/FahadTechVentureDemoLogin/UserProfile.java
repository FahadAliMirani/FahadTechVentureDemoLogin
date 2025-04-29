package com.fahad.tech.venture.FahadTechVentureDemoLogin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.AppDatabase;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.User;
import com.github.ybq.android.spinkit.SpinKitView;

public class UserProfile extends AppCompatActivity {

    private TextView tvUsername, tvFirstName, tvLastName, tvEmail;
    private Button btnEdit, btnLogout;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);

        // Get the username from intent and fetch the user's details
        String username = getIntent().getStringExtra("username");
        fetchUserDetails(username);

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void fetchUserDetails(String username) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            currentUser = db.userDao().getUserByUsername(username);

            runOnUiThread(() -> {
                if (currentUser != null) {
                    tvUsername.setText(currentUser.username);
                    tvFirstName.setText(currentUser.firstName);
                    tvLastName.setText(currentUser.lastName);
                    tvEmail.setText(currentUser.email);
                } else {
                    tvUsername.setText("User not found");
                    tvFirstName.setText("");
                    tvLastName.setText("");
                    tvEmail.setText("");
                }
            });
        }).start();
    }

    private void showEditDialog() {
        // Dim the Edit button
        btnEdit.setAlpha(0.5f);
        btnEdit.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);
        builder.setTitle("Edit User Details");

        // Get references to the EditText fields in the custom layout
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        EditText etLastName = dialogView.findViewById(R.id.etLastName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        // Populate the fields with current user data
        etUsername.setText(currentUser.username);
        etFirstName.setText(currentUser.firstName);
        etLastName.setText(currentUser.lastName);
        etEmail.setText(currentUser.email);

        // Create and show the dialog
        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Restore the Edit button after the dialog is dismissed
            btnEdit.setAlpha(1.0f);
            btnEdit.setEnabled(true);
        });

        btnUpdate.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newFirstName = etFirstName.getText().toString().trim();
            String newLastName = etLastName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            // Update user details in the database
            updateUserDetails(newUsername, newFirstName, newLastName, newEmail);
            dialog.dismiss();

            // Restore the Edit button after update
            btnEdit.setAlpha(1.0f);
            btnEdit.setEnabled(true);
        });

        dialog.show(); // Show the dialog box
    }


    private void updateUserDetails(String username, String firstName, String lastName, String email) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Update currentUser properties
            currentUser.username = username; // Update username
            currentUser.firstName = firstName;
            currentUser.lastName = lastName;
            currentUser.email = email;

            db.userDao().updateUser(currentUser);

            runOnUiThread(() -> {
                Toast.makeText(UserProfile.this, "User details updated successfully!", Toast.LENGTH_SHORT).show();
                fetchUserDetails(currentUser.username); // Refresh the displayed data
            });
        }).start();
    }

    private void logoutUser() {
        // Dim the Logout button and disable buttons
        btnLogout.setAlpha(0.5f);
        btnLogout.setEnabled(false);
        btnEdit.setEnabled(false);

        // Show the progress bar
        SpinKitView spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);

        // Clear SharedPreferences and logout
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();

        // Start a new thread for the logout process
        new Thread(() -> {
            // Simulate logout delay
            try {
                Thread.sleep(2000); // Simulate a delay for logout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Navigate to LoginScreen on the UI thread
            runOnUiThread(() -> {
                Intent intent = new Intent(UserProfile.this, LoginScreen.class);
                startActivity(intent);
                finish(); // Close UserProfile

                // Hide the progress bar and restore buttons
                spinKitView.setVisibility(View.GONE);
                btnLogout.setAlpha(1.0f);
                btnLogout.setEnabled(true);
                btnEdit.setEnabled(true);
            });
        }).start();
    }

}