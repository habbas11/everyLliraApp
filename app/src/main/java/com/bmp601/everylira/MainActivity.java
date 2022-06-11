package com.bmp601.everylira;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

// EveryLiraContentProvider
// This activity is used as a routing activity
// It will first check if a user is already logged in
// and will navigate to the Home activity
// else it will navigate to the Welcome activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition(() -> true);

        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


}
