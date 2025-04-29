package com.fahad.tech.venture.FahadTechVentureDemoLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.Adapter.UserAdapter;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.AppDatabase;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.User;
import com.google.android.material.navigation.NavigationView;
import com.github.ybq.android.spinkit.SpinKitView; // Import SpinKitView

import java.util.List;

public class AdminScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private SpinKitView spinKit; // Declare SpinKitView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Panel");

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        recyclerView = findViewById(R.id.recyclerView);
        spinKit = findViewById(R.id.spin_kit); // Initialize SpinKitView

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch users and set adapter
        fetchUsers();

        // Handle navigation item clicks
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Check if the clicked item is nav_logout
            if (item.getItemId() == R.id.nav_logout) {
                logout();
                drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after selection
                return true;
            }
            // Handle other items similarly if needed
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchUsers() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<User> users = db.userDao().getAllUsers(); // Get all users

            runOnUiThread(() -> {
                userAdapter = new UserAdapter(this, users);
                recyclerView.setAdapter(userAdapter);
            });
        }).start();
    }

    public void refreshUserList(List<User> users) {
        userAdapter.updateUserList(users);
    }

    private void logout() {
        // Show the SpinKit progress bar
        spinKit.setVisibility(View.VISIBLE);

        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Delay for demonstration purposes (simulating network operation)
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate network operation delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // Hide the SpinKit progress bar
                spinKit.setVisibility(View.GONE);

                // Show a toast message
                Toast.makeText(AdminScreen.this, "Logout successfully", Toast.LENGTH_SHORT).show();

                // Navigate to LoginScreen
                Intent intent = new Intent(AdminScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}
