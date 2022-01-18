package com.ewelion85.flagsquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        startButton = findViewById(R.id.start_button);

        transparentActionBar();

        /* Hides NavigationBar */
        MainActivity.hideNavigationBar(SettingsActivity.this);

        /* Registers a listener for <startButton> */
        setButtonListener(startButton, SettingsActivity.this);


    }

    /* Sets a listener for a button */
    private void setButtonListener(Button button, Activity activity) {

        button.setOnClickListener(v -> {
            MainActivity.regions = PreferenceManager.getDefaultSharedPreferences(getApplication()).getStringSet(MainActivity.REGIONS, null);

            if (MainActivity.regions != null && MainActivity.regions.size() > 0) {
                Intent settingsActivityIntent = new Intent(activity, QuizActivity.class);
                startActivity(settingsActivityIntent);
            } else {
                Toast.makeText(activity, R.string.warning_one_region, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void transparentActionBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}