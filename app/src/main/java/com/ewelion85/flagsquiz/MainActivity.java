package com.ewelion85.flagsquiz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button startButton;
    protected static int currentApiVersion = android.os.Build.VERSION.SDK_INT;
    TextView currentRegion;
    TextView currentNumberOfChoices;
    SharedPreferences mSharedPreferences;
    protected static Set<String> regions;
    String numberOfChoices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_button);
        currentRegion = findViewById(R.id.currentRegion);
        currentNumberOfChoices = findViewById(R.id.currentNumberOfChoices);


        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        transparentActionBar();


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        regions = mSharedPreferences.getStringSet("pref_regionsToInclude", null);
        if (regions != null && regions.size() > 0) {
            currentRegion.setText(regions.toString());
        }

        numberOfChoices = mSharedPreferences.getString("pref_numberOfChoices", null);
        currentNumberOfChoices.setText(numberOfChoices);

        mSharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regions != null && regions.size() > 0) {
                    Intent settingsActivityIntent = new Intent(MainActivity.this, QuizActivity.class);
                    startActivity(settingsActivityIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Please choose at least one region", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    protected void transparentActionBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.close_app:
                closeApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeApp() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_regionsToInclude")) {

                /* Pobranie listy wybranych obszarow... */
                regions = sharedPreferences.getStringSet("pref_regionsToInclude", null);
                currentRegion.setText(regions.toString());

            }

            if (key.equals("pref_numberOfChoices")) {

                /* Pobranie listy wybranych obszarow... */
                numberOfChoices = sharedPreferences.getString("pref_numberOfChoices", null);
                currentNumberOfChoices.setText(numberOfChoices);

            }
        }
    };


}