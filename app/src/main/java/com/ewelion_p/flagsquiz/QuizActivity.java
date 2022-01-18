package com.ewelion_p.flagsquiz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Set;

public class QuizActivity extends AppCompatActivity {


    /* Is the app running on phone? */
    private boolean phoneDevice = true;

    /* Did we change the preferences? */
    private boolean preferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        /* Sets default values for the SharedPreferences */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        /* Adds a change listener for SharedPreferences */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        /* Gets screen size of the device */
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        /* If we runs the app on a tablet.. */
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false;
        }

        /* If we runs the app on a phoneDevice.. */
        if (phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /* Hides the NavigationBar */
        MainActivity.hideNavigationBar(QuizActivity.this);


        /* Customizes the back button */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);

        /* Shows the back button in action bar */
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Makes the ActionBar transparent */
        transparentActionBar();


    }


    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {

            /* Creates a new <QuizActivityFragment> if the preferences have been changed */
            QuizActivityFragment quizFragment = (QuizActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    private void transparentActionBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this, R.style.YourAlertDialogTheme);
                builder.setMessage(R.string.sure_to_terminate)
                        .setPositiveButton(R.string.OK, (dialog, which) -> {
                            Intent preferencesIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(preferencesIntent);
                        })
                        .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.cancel())
                        .create()
                        .show();

                return true;
            case R.id.restart_quiz:
                QuizActivityFragment quizFragment = (QuizActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
                quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
                quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
                quizFragment.resetQuiz();
                return true;
            case R.id.action_settings:

                AlertDialog.Builder builder2 = new AlertDialog.Builder(QuizActivity.this, R.style.YourAlertDialogTheme);
                builder2.setMessage(R.string.changing_settings_warning)
                        .setPositiveButton(R.string.OK, (dialog, which) -> {
                            Intent preferencesIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(preferencesIntent);
                        })
                        .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.cancel())
                        .create()
                        .show();

                return true;

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


    /* Registers a change listener for SharedPreferences */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = (sharedPreferences, key) -> {

        /* Preferences have been changed */
        preferencesChanged = true;

        /* Creates a new object of QuizActivityFragment */
        QuizActivityFragment quizFragment = (QuizActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);

        /* If the preferences have been changed... */
        if (key.equals(MainActivity.CHOICES)) {

            /* Updates amount of showed rows */
            quizFragment.updateGuessRows(sharedPreferences);

            /* Restarts the quiz */
            quizFragment.resetQuiz();

        } else if (key.equals(MainActivity.REGIONS)) {

            /* Gets a list (Set) of the selected regions */
            Set<String> regions = sharedPreferences.getStringSet(MainActivity.REGIONS, null);

            /* Updates the list of the regions */
            if (regions != null && regions.size() > 0) {
                quizFragment.updateRegions(sharedPreferences);
                quizFragment.resetQuiz();
            }


        }
    };
}