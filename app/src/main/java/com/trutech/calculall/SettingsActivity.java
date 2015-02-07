package com.trutech.calculall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

/**
 * When the Settings Button has been pressed.
 */
public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {


    //Constants
    public static final int DAVID = 0, ALSTON = 1, PANDA = 2, TRAILBLAZER = 3;
    public static final int MIN_DIGITS = 5, MAX_DIGITS = 12;
    public static final String TRUTECH_URL = "http://www.trutechinnovations.com", REPORT_URL = "http://www.trutechinnovations.com/report", UPGRADE_URL = "http://www.trutechinnovations.com";
    //Default values
    public static final int DEFAULT_THEME = ALSTON;
    public static final int DEFAULT_ROUND = 6;
    public static final boolean DEFAULT_FEEDBACK = false;
    private int currentTheme;
    private PopupWindow popup;
    private ImageAdapter adapter;
    private SharedPreferences pref;
    private boolean feedbackOn;
    private int roundTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Reads prefereces
        pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        currentTheme = pref.getInt(getString(R.string.theme), DEFAULT_THEME);
        feedbackOn = pref.getBoolean(getString(R.string.haptic), DEFAULT_FEEDBACK);
        roundTo = pref.getInt(getString(R.string.round_to), DEFAULT_ROUND);
        //Sets up the current theme
        switch (currentTheme) {
            case DAVID:
                setTheme(R.style.david);
                break;
            case ALSTON:
                setTheme(R.style.alston);
                break;
            case PANDA:
                setTheme(R.style.panda);
                break;
            case TRAILBLAZER:
                setTheme(R.style.trailblazer);
                break;
            default:
                throw new IllegalStateException();
        }
        setContentView(R.layout.settings);
        setupSpinner();
        setupSwitch();
    }

    /**
     * Sets up the precision spinner.
     */
    public void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.decimal_spinner);
        List<Integer> list = new ArrayList<>();
        for (int i = MIN_DIGITS; i <= MAX_DIGITS; i++) {
            list.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(roundTo - MIN_DIGITS);
    }

    /**
     * Sets up the haptic feedback switch.
     */
    public void setupSwitch() {
        Switch switc = (Switch) findViewById(R.id.haptic_switch);
        switc.setOnCheckedChangeListener(this);
        switc.setChecked(feedbackOn);
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
        GridView gv = (GridView) layout.findViewById(R.id.themes_grid);
        adapter =new ImageAdapter(currentTheme);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTheme = position;
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(getString(R.string.theme), currentTheme);
                editor.apply();
            }
        });
    }


    /**
     * When user exits theme page.
     *
     * @param v Not Used
     */
    public void clickExitTheme(View v){
        if (adapter.currentTheme != currentTheme){ //Need to change themes
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(getString(R.string.theme), adapter.currentTheme);
            editor.apply();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else{
            popup.dismiss();
        }
    }

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

    /**
     * When an Item has been clicked on the decimal spinner.
     *
     * @param parent   Not Used
     * @param view     Not Used
     * @param position The position of the item clicked
     * @param id       Not Used
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        roundTo = position + MIN_DIGITS;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(getString(R.string.round_to), roundTo);
        editor.apply();
    }

    /**
     * Does nothing.
     *
     * @param parent Not Used
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int currentTheme;
        private int[] images = {R.drawable.david_theme, R.drawable.alston_theme, R.drawable.panda_theme, R.drawable.trailblazer_theme};
        private CheckBox[] boxes = new CheckBox[images.length];

        public ImageAdapter(int currentTheme) {
            this.currentTheme = currentTheme;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return images.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.theme_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            //Sets ID for better tracking
            holder.imageview.setId(position);

            //Sets up the ImageView portion
            holder.imageview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ViewGroup parent = (ViewGroup) v.getParent();
                    //Checks the new checkbox and unchecks old
                    boxes[currentTheme].setChecked(false);
                    currentTheme = v.getId();
                    boxes[currentTheme].setChecked(true);
                }
            });
            holder.imageview.setImageResource(images[position]);

            //Special case for if it is the default
            if (currentTheme == position){
                holder.checkbox.setChecked(true);
            }
            if (boxes[position] == null) { //Bug Fix
                boxes[position] = holder.checkbox;
            }
            //Disables checkbox
            holder.checkbox.setClickable(false);
            return convertView;
        }

    }
    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
    }

}

