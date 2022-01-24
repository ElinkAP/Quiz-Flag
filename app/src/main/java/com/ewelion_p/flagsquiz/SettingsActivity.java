package com.ewelion_p.flagsquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {

    Button startButton;
    RadioGroup radioGroup;
    SharedPreferences mSharedPreferences;

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

        /* Gets SharedPreferences */
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        /* Registers listener for Shared Preferences */
//        mSharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        radioGroup = findViewById(R.id.number_group);

        setRadioGroupListener();

    }

    private void setRadioGroupListener() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){

                    case R.id.rb_2:
                        editor.putString(MainActivity.CHOICES, "2");
                        editor.apply();
                        break;

                    case R.id.rb_4:
                        editor.putString(MainActivity.CHOICES, "4");
                        editor.apply();
                        break;

                    case R.id.rb_6:
                        editor.putString(MainActivity.CHOICES, "6");
                        editor.apply();
                        break;

                    case R.id.rb_8:
                        editor.putString(MainActivity.CHOICES, "8");
                        editor.apply();
                        break;

                }
            }
        });
    }

//    /* Sets change listener for SharedPreferences */
//    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//
//
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
////            if (key.equals(MainActivity.REGIONS)) {
////
////                /* Gets a list of selected regions */
////                regions = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
////
////
////            }
//
//            if (key.equals(MainActivity.CHOICES)) {
//
//                /* Gets a list of selected number of choices */
//                numberOfChoices = sharedPreferences.getString(CHOICES, null);
//
//
//            }
//        }
//    };

//    public void addListenerOnButton() {
//
//        radioGroup = (RadioGroup) findViewById(R.id.radio);
//        radioButton2 = (RadioButton) findViewById(R.id.rb_2);
//
//        radioButton2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                // get selected radio button from radioGroup
//                int selectedId = radioGroup.getCheckedRadioButtonId();
//
//                // find the radiobutton by returned id
//                radioButton = (RadioButton) findViewById(selectedId);
//
//                Toast.makeText(SettingsActivity.this, "selected button is: " +
//                        radioButton.getText(), Toast.LENGTH_SHORT).show();
//
//            }
//
//        });
//
//    }

//    private void saveRadioChoice(){
//        SharedPreferences mSharedPref = getSharedPreferences(MainActivity.CHOICES, MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = mSharedPref.edit();
//
//// Initialize Radiogroup while saving choices
//        RadioGroup localRadioGroup = (RadioGroup) findViewById(R.id.number_group);
//        editor.putInt(String.valueOf(2), localRadioGroup.indexOfChild(findViewById(localRadioGroup.getCheckedRadioButtonId())));
//        editor.apply();
//    }
//
//    private void retrieveChoices(){
//
//        SharedPreferences sharedPref = getSharedPreferences(MainActivity.CHOICES,MODE_PRIVATE);
//        int i = sharedPref.getInt(String.valueOf(2),-1);
//        if( i >= 0){
//            ((RadioButton) ((RadioGroup)findViewById(R.id.rb_2))).getChildAt(i)).setChecked(true);
//        }
//
//
//    }



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