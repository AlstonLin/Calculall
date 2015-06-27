package com.trutechinnovations.calculall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
public class SettingsActivity extends Activity {


    //Constants
    public static final int DAVID = 0, ALSTON = 1, PANDA = 2, TRAILBLAZER = 3, HAWKS = 4, GEESE = 5, SUNSET = 6, FOREST = 7, MATERIAL = 8, OCEAN = 9;
    public static final int MIN_DIGITS = 5, MAX_DIGITS = 12;
    public static final Integer[] FONT_SIZES = {42, 48, 64, 72, 84, 96, 108, 120};
    public static final String TRUTECH_URL = "http://www.trutechinnovations.com", REPORT_URL = "http://www.trutechinnovations.com", UPGRADE_URL = "https://play.google.com/store/apps/details?id=com.trutechinnovations.calculallaf";
    //Default values
    public static final int DEFAULT_THEME = ALSTON;
    public static final int DEFAULT_ROUND = 6;
    public static final int DEFAULT_FONT_SIZE = 96;
    public static final boolean DEFAULT_FEEDBACK = false;
    public static final boolean DEFAULT_SWIPE = false;
    //Fields
    private int currentTheme;
    private PopupWindow popup;
    private ImageAdapter adapter;
    private SharedPreferences pref;
    private boolean feedbackOn, swipeOnly;
    private int roundTo;
    private int fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Reads prefereces
        pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        currentTheme = pref.getInt(getString(R.string.theme), DEFAULT_THEME);
        feedbackOn = pref.getBoolean(getString(R.string.haptic), DEFAULT_FEEDBACK);
        roundTo = pref.getInt(getString(R.string.round_to), DEFAULT_ROUND);
        fontSize = pref.getInt(getString(R.string.font_size), DEFAULT_FONT_SIZE);
        swipeOnly = pref.getBoolean(getString(R.string.mode_switch), DEFAULT_SWIPE);
        //Sets up the current theme
        switch (currentTheme) {
            case DAVID:
                setTheme(R.style.Theme1);
                break;
            case ALSTON:
                setTheme(R.style.Theme2);
                break;
            case PANDA:
                setTheme(R.style.Theme1);
                break;
            case TRAILBLAZER:
                setTheme(R.style.Theme1);
                break;
            case HAWKS:
                setTheme(R.style.Theme1);
                break;
            case GEESE:
                setTheme(R.style.Theme1);
                break;
            case SUNSET:
                setTheme(R.style.Theme1);
                break;
            case FOREST:
                setTheme(R.style.Theme1);
                break;
            case MATERIAL:
                setTheme(R.style.Theme1);
                break;
            case OCEAN:
                setTheme(R.style.Theme1);
                break;
            default:
                throw new IllegalStateException("Illegal Theme");
        }
        setContentView(R.layout.settings);
        setupDecimalSpinner();
        setupFontSpinner();
        setupSwitches();
    }

    /**
     * Sets up the precision spinner.
     */
    public void setupDecimalSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.decimal_spinner);
        List<Integer> list = new ArrayList<>();
        for (int i = MIN_DIGITS; i <= MAX_DIGITS; i++) {
            list.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        Spinner spinner = (Spinner) findViewById(R.id.font_spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, FONT_SIZES);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        getTheme().resolveAttribute(R.attr.displayTextColor, typedValue, true);
        int backgroundColor = typedValue.data;


        CheckBox haptic = (CheckBox) findViewById(R.id.haptic_switch);
        CheckBox swipe = (CheckBox) findViewById(R.id.swipe_only_switch);


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
        swipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * When the check has been changed.
             *
             * @param buttonView Not Used
             * @param isChecked  If it is now checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swipeOnly = isChecked;
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(getString(R.string.mode_switch), isChecked);
                editor.apply();
            }
        });

        haptic.setChecked(feedbackOn);
        swipe.setChecked(swipeOnly);
    }

    /**
     * When the back button has been clicked.
     *
     * @param v Not Used
     */
    public void clickBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_page, null, false);
        popup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popup.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0);
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
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.themes_select, null, false);
        popup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popup.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0);
        //Sets up the theme
        ListView gv = (ListView) layout.findViewById(R.id.themes_grid);
        adapter = new ImageAdapter(this);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTheme = position;
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(getString(R.string.theme), currentTheme);
                editor.apply();
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * When user exits theme page.
     *
     * @param v Not Used
     */
    public void clickExitTheme(View v) {
        popup.dismiss();
    }

    public class ImageAdapter extends BaseAdapter {
        LayoutInflater inflater;
        private Context mContext;
        private String[] theme_names = new String[]{"Blue Delight", "Orange on Black"};
        private int[] color1 = new int[]{R.color.color1David, R.color.color1Theme2};
        private int[] bg = new int[]{R.color.textDisplayColorDavid, R.color.backgroundTheme2};
        private int[] color2 = new int[]{R.color.numPadColorDavid, R.color.numPadColorTheme2};
        private int[] text = new int[]{R.color.textColorDavid, R.color.textColorTheme2};

        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return theme_names.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ThemeHolder mViewHolder;
            final int curr = position;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.theme_item, parent, false);
                mViewHolder = new ThemeHolder(convertView);
                convertView.setTag(mViewHolder);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentTheme = curr;
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt(getString(R.string.theme), currentTheme);
                        editor.apply();
                        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                        startActivity(intent);

                    }
                });
            } else {
                mViewHolder = (ThemeHolder) convertView.getTag();
            }


            ((TextView) convertView.findViewById(R.id.title)).setText(theme_names[position]);
            ((TextView) convertView.findViewById(R.id.sample_text)).setTextColor(getResources().getColor(text[position]));
            ((ImageButton) convertView.findViewById(R.id.color1)).setBackgroundColor(getResources().getColor(color1[position]));
            ((ImageButton) convertView.findViewById(R.id.color2)).setBackgroundColor(getResources().getColor(color2[position]));
            convertView.findViewById(R.id.theme_select).setBackgroundColor(getResources().getColor(bg[position]));

            return convertView;
        }

        private class ThemeHolder {
            TextView title;
            ImageView color1, color2;
            LinearLayout section;

            public ThemeHolder(View item) {
                title = (TextView) item.findViewById(R.id.title);
                color1 = (ImageView) item.findViewById(R.id.color1);
                color2 = (ImageView) item.findViewById(R.id.color2);
                section = (LinearLayout) item.findViewById(R.id.theme_select);
            }
        }

    }

}

