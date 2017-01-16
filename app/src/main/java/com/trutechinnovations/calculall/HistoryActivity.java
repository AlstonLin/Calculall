package com.trutechinnovations.calculall;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by david on 1/15/2017.
 */

public class HistoryActivity extends AppCompatActivity {
    public static final double HISTORY_IO_RATIO = 0.7; //The size of the output / input in the history
    public static final String FILENAME = "FILENAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.setUpTheme(this,true);
        setContentView(R.layout.history_activity);

        String filename = getIntent().getStringExtra(FILENAME);

        getSupportActionBar().setTitle("Calculation History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Retrieves the user data from saved memory
        ArrayList<Object[]> history;
        try {
            FileInputStream stream = openFileInput(filename);
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            history = (ArrayList<Object[]>) objectStream.readObject();
            //Reverses the order so that the most recent is at the top
            Collections.reverse(history);
        } catch (FileNotFoundException e) { //No history
            history = new ArrayList<>();

            ArrayList<Token> list1 = new ArrayList<>();
            ArrayList<Token> list2 = new ArrayList<>();

            list1.add(new StringToken("No History to show"));
            list2.add(new StringToken(""));

            ArrayList<Token>[] message = new ArrayList[2];
            message[0] = list1;
            message[1] = list2;
            history.add(message);
        } catch (ClassNotFoundException e) {
            history = new ArrayList<>();

            ArrayList<Token> list1 = new ArrayList<>();
            ArrayList<Token> list2 = new ArrayList<>();

            list1.add(new StringToken("No History to show"));
            list2.add(new StringToken(""));

            ArrayList<Token>[] message = new ArrayList[2];
            message[0] = list1;
            message[1] = list2;
            history.add(message);
        } catch (IOException e) {
            history = new ArrayList<>();

            ArrayList<Token> list1 = new ArrayList<>();
            ArrayList<Token> list2 = new ArrayList<>();

            list1.add(new StringToken("No History to show"));
            list2.add(new StringToken(""));

            ArrayList<Token>[] message = new ArrayList[2];
            message[0] = list1;
            message[1] = list2;
            history.add(message);
        }

        //Finds the ListView from the inflated History XML so it could be manipulated
        ListView lv = (ListView) findViewById(R.id.historyList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new HistoryAdapter(history));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    /**
     * The custom Adapter for the ListView in the calculation history.
     */
    private class HistoryAdapter extends BaseAdapter {
        private GestureDetector gestureDetector;
        private ArrayList<Object[]> history; //The data that will be shown in the ListView

        public HistoryAdapter(ArrayList<Object[]> history) {
            this.history = history;
            gestureDetector = new GestureDetector(getApplicationContext(), new SingleTapUp());
        }

        @Override
        public int getCount() {
            return history.size();
        }

        @Override
        public Object getItem(int position) {
            return history.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Prepares the View of each item in the ListView that this Adapter will be attached to.
         *
         * @param position    The index of the item
         * @param convertView The old view that may be reused, or null if not possible
         * @param parent      The parent view
         * @return The newly prepared View that will visually represent the item in the ListView in the given position
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) { //For efficiency purposes so that it does not unnecessarily inflate Views
                //Inflates the XML file to get the View of the history element
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.history_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            OutputView input = (OutputView) convertView.findViewById(R.id.input);
            OutputView output = (OutputView) convertView.findViewById(R.id.output);

            //Sets the font size of each OutputView
            input.setFontSize(Basic.getInstance().activity.getFontSize());
            output.setFontSize((int) (Basic.getInstance().activity.getFontSize() * HISTORY_IO_RATIO));

            //Enters the appropriate expressions to the OutputView
            Object[] entry = history.get(position);
            input.display((ArrayList<Token>) entry[0]);
            output.display((ArrayList<Token>) entry[1]);

            //To respond to user touches
            final ArrayList<Token> INPUT = (ArrayList<Token>) history.get(position)[0]; //Makes a constant reference so that history can be accessed by an inner class
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        ArrayList<Token> input = new ArrayList<>();
                        //Removes any StringTokens
                        for (Token t : INPUT) {
                            if (!(t instanceof StringToken)) {
                                input.add(t);
                            }
                        }

                        //Adds the input expression to the current tokens
                        Basic.getInstance().tokens.addAll(Basic.getInstance().activity.display.getRealCursorIndex(), input); //Adds the input of the entry
                        int cursorChange = 0;
                        for (Token t : input) { //Determines the correct cursor position after the inserted expression
                            if (!(t instanceof Placeholder && (t.getType() == Placeholder.SUPERSCRIPT_BLOCK || t.getType() == Placeholder.BASE_BLOCK) ||
                                    (t instanceof Operator && t.getType() == Operator.VARROOT) ||
                                    (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.FRACTION_CLOSE) ||
                                    (t instanceof Operator && t.getType() == Operator.FRACTION))) {
                                cursorChange++;
                            }
                        }
                        Basic.getInstance().updatePlaceHolders();
                        Basic.getInstance().activity.display.setCursorIndex(Basic.getInstance().activity.display.getCursorIndex() + cursorChange);
                        finish(); //Exits history once an Item has been selected
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return convertView;
        }

        private class SingleTapUp extends GestureDetector.SimpleOnGestureListener { //CLASSCEPTION

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return true;
            }
        }

    }
}
