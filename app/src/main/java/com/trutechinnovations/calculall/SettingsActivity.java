/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * When the Settings Button has been pressed.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class SettingsActivity extends AppCompatActivity {

    //Constants
    public static final int MIN_DIGITS = 5, MAX_DIGITS = 12;
    public static final Integer[] FONT_SIZES = {42, 48, 64, 72, 84, 96, 108, 120};
    public static final String TRUTECH_URL = "http://www.trutechinnovations.com", REPORT_URL = "http://www.trutechinnovations.com", UPGRADE_URL = "https://play.google.com/store/apps/details?id=com.trutechinnovations.calculallaf";
    //Default values
    public static final int DEFAULT_ROUND = 6;
    public static final int DEFAULT_FONT_SIZE = 96;
    public static final boolean DEFAULT_FEEDBACK = false;
    public static final boolean DEFAULT_SWIPE = false;
    public static final boolean AUTOCALCULATE_ON = false;
    //Fields
    private int currentTheme  = -1;
    private PopupWindow popup;
    private SharedPreferences pref;
    private boolean feedbackOn, swipeOnly, autocalculateOn;
    private int roundTo;
    private int fontSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Reads preferences
        pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        currentTheme = pref.getInt(getString(R.string.theme), ThemeHelper.DEFAULT_THEME);
        feedbackOn = pref.getBoolean(getString(R.string.haptic), DEFAULT_FEEDBACK);
        autocalculateOn = pref.getBoolean(getString(R.string.autocalculate), AUTOCALCULATE_ON);
        roundTo = pref.getInt(getString(R.string.round_to), DEFAULT_ROUND);
        fontSize = pref.getInt(getString(R.string.font_size), DEFAULT_FONT_SIZE);
        swipeOnly = pref.getBoolean(getString(R.string.mode_switch), DEFAULT_SWIPE);
        currentTheme = ThemeHelper.setUpTheme(this,true);
        setContentView(R.layout.settings);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupDecimalSpinner();
        setupFontSpinner();
        setupSwitches();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int tmpTheme = ThemeHelper.setUpTheme(this,false);
        if(currentTheme!=-1 && tmpTheme!=currentTheme){
            finish();
            startActivity(getIntent());
        }
    }

    /**
     * Sets up the precision spinner.
     */
    public void setupDecimalSpinner() {
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.decimal_spinner);
        List<Integer> list = new ArrayList<>();
        for (int i = MIN_DIGITS; i <= MAX_DIGITS; i++) {
            list.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                roundTo = position + MIN_DIGITS;
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(getString(R.string.round_to), roundTo);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        spinner.setSelection(roundTo - MIN_DIGITS);
    }

    /**
     * Sets up the precision spinner.
     */
    public void setupFontSpinner() {
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.font_spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, FONT_SIZES);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                fontSize = FONT_SIZES[position];
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(getString(R.string.font_size), fontSize);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setSelection(Arrays.binarySearch(FONT_SIZES, fontSize));
    }

    /**
     * Sets up the switches.
     */
    public void setupSwitches() {
        //David's stuff
        TypedValue typedValue = new TypedValue();
        int backgroundColor = typedValue.data;

        CheckBox haptic = (CheckBox) findViewById(R.id.haptic_switch);
        CheckBox autocalculate = (CheckBox) findViewById(R.id.autocalculate_switch);

        haptic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * When the check has been changed.
             *
             * @param buttonView Not Used
             * @param isChecked  If it is now checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                feedbackOn = isChecked;
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(getString(R.string.haptic), isChecked);
                editor.apply();
            }
        });

        haptic.setChecked(feedbackOn);

        autocalculate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * When the check has been changed.
             *
             * @param buttonView Not Used
             * @param isChecked  If it is now checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autocalculateOn = isChecked;
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(getString(R.string.autocalculate), isChecked);
                editor.apply();
            }
        });

        autocalculate.setChecked(autocalculateOn);
    }

    /**
     * When the report button has been clicked.
     *
     * @param v Not Used
     */
    public void clickReport(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPORT_URL));
        startActivity(browserIntent);
    }

    /**
     * When the about button has been clicked.
     *
     * @param v Not Used
     */
    public void clickAbout(View v) {
        Intent intent  = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * When the user presses the Upgrade Button.
     *
     * @param v Not Used
     */
    public void clickUpgrade(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPGRADE_URL));
        startActivity(browserIntent);
    }

    /**
     * When the user presses to visit the TruTech website.
     *
     * @param v Not Used
     */
    public void clickVisit(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TRUTECH_URL));
        startActivity(browserIntent);
    }

    /**
     * When the user exits the currently showing PopupWindow
     *
     * @param v Not Used
     */
    public void clickExit(View v) {
        popup.dismiss();
    }

    /**
     * When the user presses the Change Theme button.
     *
     * @param v Not Used
     */
    public void clickTheme(View v) {
        Intent intent  = new Intent(this, ThemeSelectActivity.class);
        startActivity(intent);
    }

    /**
     * When user exits theme page.
     *
     * @param v Not Used
     */
    public void clickExitTheme(View v) {
        popup.dismiss();
    }


}

