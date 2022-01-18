package com.ewelion_p.flagsquiz;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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


    protected static int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    /* Preferences keys */
    public static final String CHOICES = "pref_number_of_choices";
    public static final String REGIONS = "pref_regions_to_include";

    SharedPreferences mSharedPreferences;
    protected static Set<String> regions = null;
    String numberOfChoices;
    TextView mainTextView;
    Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_button);
        mainTextView = findViewById(R.id.textView2);


        /* Hides the NavigationBar */
        hideNavigationBar(MainActivity.this);

        /* Sets up transparent ActionBar*/
        transparentActionBar();

        /* Gets SharedPreferences */
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        regions = mSharedPreferences.getStringSet(REGIONS, null);
        numberOfChoices = mSharedPreferences.getString(CHOICES, null);

        /* Registers listener for Shared Preferences */
        mSharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        /* Sets the texts for <mainTextView> */
        mainTextView.setText(getString(R.string.settings_info, numberOfChoices, getText1(), convertToString(), getText2()));

        /* Registers a listener for <startButton> */
        setButtonListener(startButton, MainActivity.this);

    }

    /* Sets a listener for a button */
    private void setButtonListener(Button button, Activity activity) {
        button.setOnClickListener(v -> {
            MainActivity.regions = PreferenceManager.getDefaultSharedPreferences(getApplication()).getStringSet(MainActivity.REGIONS, null);
            if (MainActivity.regions != null && MainActivity.regions.size()>0) {
                Intent settingsActivityIntent = new Intent(activity, QuizActivity.class);
                startActivity(settingsActivityIntent);
            } else {
                Toast.makeText(activity, R.string.warning_one_region , Toast.LENGTH_SHORT).show();
            }

        });
    }

    protected static void hideNavigationBar(Activity activity) {
        currentApiVersion = Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    /* Gets 2nd part for the String for <mainTextView> */
    private String getText2() {

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "below.";
        } else {
            return "here " + "\u2794";
        }
    }

    /* Gets 4th part for the String for <mainTextView> */
    private String getText1() {
        if (regions.size() > 1) {
            return "s are";
        } else {
            return " is";
        }
    }

    /* Makes ActionBar transparent */
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

        if (item.getItemId() == R.id.close_app) {
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


    /* Converts Set<String> regions into Array and then into String/StringBuilder  */
    public String convertToString() {

        String[] mRegions = regions.toArray(new String[regions.size()]);
        if (mRegions.length == 1) {
            return mRegions[0].replace("_", " ");
        } else if (mRegions == null || mRegions.length == 0) {
            return "0";
        } else {
            StringBuilder myRegions = new StringBuilder(mRegions[0].replace("_", " "));
            for (int i = 1; i < mRegions.length; i++) {
                myRegions.append(", ").append(mRegions[i].replace("_", " "));
            }

            return myRegions.toString();
        }
    }

    /* Sets change listener for SharedPreferences */
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(REGIONS)) {

                /* Gets a list of selected regions */
                regions = sharedPreferences.getStringSet(REGIONS, null);

            }

            if (key.equals(CHOICES)) {

                /* Gets a list of selected number of choices */
                numberOfChoices = sharedPreferences.getString(CHOICES, null);

            }

            /* Sets text for the <mainTextView> */
            mainTextView.setText(getString(R.string.settings_info, numberOfChoices, getText1(), convertToString(), getText2()));
        }
    };


}