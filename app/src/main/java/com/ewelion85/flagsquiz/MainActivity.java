package com.ewelion85.flagsquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;



import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {



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

        setContentView(R.layout.activity_main);


        /* Przypisywanie domyslnych ustawien do obiektu SharedPreferences */
        PreferenceManager.setDefaultValues(this, R.xml.preferences,false);

        /* Rejestrowanie obiektu nasluchujacego zmian obiektu SharedPreferences */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        /* Pobranie rozmiaru ekranu urzadzenia */
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        /* Jezeli rozmiar ekranu jest typowy dla tabletu, to ... */
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            phoneDevice = false;
        }

        /* Jeseli uruchamiamy aplikacje na telefonie, to ... */
        if (phoneDevice){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged){
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Pobranie informacji o orientacji urzadzenia */
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            return false;
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);

        return super.onOptionsItemSelected(item);
    }


    /* Obiekt nasluchujacy zmian obiektu SharedPreferences */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            /* Uzytkownik zmienil ustawienia aplikacji */
            preferencesChanged= true;

            /* Inicjalizacja obiektu MainActivityFragment */
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);

            /* Instrukcja warunkowa dla rodzaju zmienionych ustawien */
            if(key.equals(CHOICES)){

                /* Aktualizacja liczby wyswietlanych wierszy z przyciskami odpowiedzi */
                quizFragment.updateGuessRows(sharedPreferences);

                /* Zresetowanie quizu */
                quizFragment.resetQuiz();

            } else if (key.equals(REGIONS)){

                /* Pobranie listy wybranych obszarow... */
                Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);

                /* Jezeli wybrano wiecej niz jeden obszar... */
                if(regions != null && regions.size()> 0){
                    quizFragment.updateRegions(sharedPreferences);
                    quizFragment.resetQuiz();
                }

                /* Jezeli nie wybrano zadnego obszaru... */
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    regions.add(getString(R.string.default_region));
                    editor.putStringSet(REGIONS, regions);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.default_region_message, Toast.LENGTH_SHORT).show();
                }

                /* Informowanie uzytkowanika o restarcie quizu */
                Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();

            }
        }
    };

}