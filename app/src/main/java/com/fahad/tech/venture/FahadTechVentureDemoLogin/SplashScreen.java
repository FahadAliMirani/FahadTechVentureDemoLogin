package com.fahad.tech.venture.FahadTechVentureDemoLogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find the progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Hide the progress bar after 2 seconds and navigate to MainActivity
        new Handler().postDelayed(() -> {
            // Hide the progress bar before navigating
            progressBar.setVisibility(ProgressBar.GONE);

            // Start MainActivity
            Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        }, 2000); // 2000ms = 2 seconds
    }
}
