package com.ewelion_p.flagsquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Set;


public class SettingsActivity extends AppCompatActivity {

    Button startButton;
    RadioGroup radioGroupChoices;
    RadioGroup radioGroupRegions;
    SharedPreferences mSharedPreferences;
    String numberOfChoices;
    protected static Set<String> regions = null;

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





        /* Disables ActionBar for landscape orientation */
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(false);
        }

        /* Initialization of 'user interface' */
        radioGroupChoices = findViewById(R.id.number_group);
        radioGroupRegions = findViewById(R.id.regions_group);


        /* Gets SharedPreferences */
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* Gets lists of regions and number preference from the current SharedPreferences */
        getRegions();
        getChoices();

        /* Sets listeners for the choices radioGroup */
        setChoicesRadioGroupListener();

        CheckBox africa = findViewById(R.id.btn_africa);
        CheckBox asia = findViewById(R.id.btn_asia);
        CheckBox europe = findViewById(R.id.btn_europe);
        CheckBox southAmerica = findViewById(R.id.btn_s_america);
        CheckBox oceania = findViewById(R.id.btn_oceania);
        CheckBox northAmerica = findViewById(R.id.btn_n_america);

        /* Sets listeners for the regions checkBoxes */
        setRegionsCheckBoxesListeners(africa, asia, europe, southAmerica, oceania, northAmerica);


    }

    /* Sets listeners for the regions checkBoxes */
    private void setRegionsCheckBoxesListeners(CheckBox africa, CheckBox asia, CheckBox europe, CheckBox southAmerica, CheckBox oceania, CheckBox northAmerica) {
        checkBoxListener(africa, "Africa");
        checkBoxListener(asia, "Asia");
        checkBoxListener(europe, "Europe");
        checkBoxListener(northAmerica, "North_America");
        checkBoxListener(oceania, "Oceania");
        checkBoxListener(southAmerica, "South_America");
    }

    /* Gets lists of regions from the current SharedPreferences */
    private void getRegions() {

        regions = mSharedPreferences.getStringSet(MainActivity.REGIONS, null);
        for (String region : regions) {
            switch (region) {
                case "Asia":
                    ((CheckBox) (findViewById(R.id.btn_asia))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_asia);
                    break;

                case "Africa":
                    ((CheckBox) (findViewById(R.id.btn_africa))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_africa);
                    break;

                case "Europe":
                    ((CheckBox) (findViewById(R.id.btn_europe))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_europe);
                    break;

                case "North_America":
                    ((CheckBox) (findViewById(R.id.btn_n_america))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_n_america);
                    break;

                case "Oceania":
                    ((CheckBox) (findViewById(R.id.btn_oceania))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_oceania);
                    break;

                case "South_America":
                    ((CheckBox) (findViewById(R.id.btn_s_america))).setChecked(true);
                    setTextColorCheckBox(R.id.btn_s_america);
                    break;


            }
        }

    }

    /* Gets a number of possible answers from the current SharedPreferences */
    private void getChoices() {
        numberOfChoices = mSharedPreferences.getString(MainActivity.CHOICES, null);

        switch (numberOfChoices) {

            case "2":
                ((RadioButton) (findViewById(R.id.rb_2))).setChecked(true);
                setTextColorRadioButtons(R.id.rb_2);
                break;

            case "4":
                ((RadioButton) (findViewById(R.id.rb_4))).setChecked(true);
                setTextColorRadioButtons(R.id.rb_4);
                break;

            case "6":
                ((RadioButton) (findViewById(R.id.rb_6))).setChecked(true);
                setTextColorRadioButtons(R.id.rb_6);
                break;

            case "8":
                ((RadioButton) (findViewById(R.id.rb_8))).setChecked(true);
                setTextColorRadioButtons(R.id.rb_8);
                break;
        }
    }

    /* Sets listeners for the choices radioGroup */
    private void setChoicesRadioGroupListener() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        /* Adds onCheckedChangeListener for the radioButtons (numbers of possible answers) */
        radioGroupChoices.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {

                case R.id.rb_2:
                    editor.putString(MainActivity.CHOICES, "2");
                    editor.apply();
                    setTextColorRadioButtons(R.id.rb_2);
                    break;

                case R.id.rb_4:
                    editor.putString(MainActivity.CHOICES, "4");
                    editor.apply();
                    setTextColorRadioButtons(R.id.rb_4);
                    break;

                case R.id.rb_6:
                    editor.putString(MainActivity.CHOICES, "6");
                    editor.apply();
                    setTextColorRadioButtons(R.id.rb_6);
                    break;

                case R.id.rb_8:
                    editor.putString(MainActivity.CHOICES, "8");
                    editor.apply();
                    setTextColorRadioButtons(R.id.rb_8);
                    break;

            }
        });
    }

    /* Sets gold color for selected number */
    private void setTextColorRadioButtons(int id) {

        for (int i = 0; i < 4; i++) {
            ((RadioButton) radioGroupChoices.getChildAt(i)).setTextColor(getResources().getColor(R.color.white, getTheme()));
        }

        ((RadioButton) findViewById(id)).setTextColor(getResources().getColor(R.color.gold, getTheme()));
    }

    /* Sets gold color for selected region */
    private void setTextColorCheckBox(int id) {

        ((CheckBox) findViewById(id)).setTextColor(getResources().getColor(R.color.gold, getTheme()));
    }


    /* Sets listeners for the checkBoxes with names of regions */
    private void checkBoxListener(CheckBox regionBox, String region) {

        regions = mSharedPreferences.getStringSet(MainActivity.REGIONS, null);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        regionBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isShown()) {
                if (buttonView.isChecked()) {
                    regions.add(region);
                    setTextColorCheckBox(regionBox.getId());
                } else {
                    regions.remove(region);
                    (regionBox).setTextColor(getResources().getColor(R.color.white, getTheme()));
                }
            }

        });

        editor.putStringSet(MainActivity.REGIONS, regions);
        editor.apply();
    }


    /* Sets a listener for a start button */
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