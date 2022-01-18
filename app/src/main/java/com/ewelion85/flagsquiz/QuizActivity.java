package com.ewelion85.flagsquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;

import java.util.Set;

public class QuizActivity extends AppCompatActivity {

    /* Preferences keys */
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";

    /* Is the app running on phone? */
    private boolean phoneDevice = true;

    /* Did we change the preferences? */
    private boolean preferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        /* Przypisywanie domyslnych ustawien do obiektu SharedPreferences */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        /* Rejestrowanie obiektu nasluchujacego zmian obiektu SharedPreferences */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        /* Pobranie rozmiaru ekranu urzadzenia */
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        /* Jezeli rozmiar ekranu jest typowy dla tabletu, to ... */
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false;
        }

        /* Jeseli uruchamiamy aplikacje na telefonie, to ... */
        if (phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (MainActivity.currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        ActionBar actionBar = getSupportActionBar();

        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

     transparentActionBar();



    }


    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
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

//        /* Pobranie informacji o orientacji urzadzenia */
//        int orientation = getResources().getConfiguration().orientation;
//
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
//        } else {
//            return false;
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this, R.style.YourAlertDialogTheme);
                builder.setMessage("Are you sure you want to terminate the quiz?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent preferencesIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(preferencesIntent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
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
                builder2.setMessage("Changing the settings will restart the quiz.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent preferencesIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(preferencesIntent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
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


    /* Obiekt nasluchujacy zmian obiektu SharedPreferences */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Uzytkownik zmienil ustawienia aplikacji */
            preferencesChanged = true;

            /* Inicjalizacja obiektu MainActivityFragment */
            QuizActivityFragment quizFragment = (QuizActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);

            /* Instrukcja warunkowa dla rodzaju zmienionych ustawien */
            if (key.equals(CHOICES)) {

                /* Aktualizacja liczby wyswietlanych wierszy z przyciskami odpowiedzi */
                quizFragment.updateGuessRows(sharedPreferences);

                /* Zresetowanie quizu */
                quizFragment.resetQuiz();

            } else if (key.equals(REGIONS)) {

                /* Pobranie listy wybranych obszarow... */
                Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);

                /* Jezeli wybrano wiecej niz jeden obszar... */
                if (regions != null && regions.size() > 0) {
                    quizFragment.updateRegions(sharedPreferences);
                    quizFragment.resetQuiz();
                }

                /* Jezeli nie wybrano zadnego obszaru... */
                else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet(REGIONS, regions);
                    regions.add(getString(R.string.default_region));

                    editor.apply();



                    Toast.makeText(QuizActivity.this, R.string.default_region_message, Toast.LENGTH_SHORT).show();
                }



                /* Informowanie uzytkowanika o restarcie quizu */
                Toast.makeText(QuizActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();

            }
        }
    };
}