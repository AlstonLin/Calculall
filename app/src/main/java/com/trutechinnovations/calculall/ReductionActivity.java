package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by david on 1/22/2017.
 */

public class ReductionActivity extends ListViewActivity {
    public static final double CONSTANTS_IO_RATIO = 0.7; //The size of the output / input in the
    public static final String RREF = "RREF";

    ArrayList<Token[]> steps;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reduction_view);

        setUpListView(getIntent().getBooleanExtra(RREF,false));

        //Finds the ListView from the inflated consts XML so it could be manipulated
        ListView lv = (ListView) findViewById(R.id.reductionList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new ReductionAdapter());
    }

    public void setUpListView(boolean rref){
        steps = new ArrayList<>();

        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(
                    MatrixMode.getInstance().tokens
            ));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = ((Matrix) t).getEntriesAsDbls();
                ArrayList<Token> input = new ArrayList<>();
                ArrayList<Token> output = new ArrayList<>();
                if (rref) {
                    steps = MatrixUtils.knitSteps(a, MatrixUtils.getRREFSteps(a));
                    input.add(0, new StringToken("RREF of "));
                } else {
                    steps = MatrixUtils.knitSteps(a, MatrixUtils.getREFSteps(a));
                    input.add(0, new StringToken("REF of "));
                }
                if (steps.size() == 0) {
                    Token[] temp1 = new Token[2];
                    temp1[0] = new StringToken("No Steps to show");
                    temp1[1] = new StringToken("");
                    steps.add(temp1);
                }
                input.addAll(MatrixMode.getInstance().tokens);
                output.add(steps.get(steps.size() - 1)[0]);
                MatrixMode.getInstance().saveEquation(input, output, MatrixMode.getInstance().filename);
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to be row reducible");
            }
        } catch (Exception e) { //an error was thrown
            Log.d(ReductionActivity.class.getSimpleName(),e.getMessage());
        }
    }

    @Override
    public String getViewTitle() {
        return "Row Reduction Steps";
    }


    /**
     * The custom Adapter for the ListView in the row reduction steps.
     */
    private class ReductionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return steps.size();
        }

        @Override
        public Object getItem(int position) {
            return steps.get(position);
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
                //Inflates the XML file to get the View of the consts element
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reduction_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            OutputView intermediate = (OutputView) convertView.findViewById(R.id.input);
            OutputView step = (OutputView) convertView.findViewById(R.id.step);

            //Sets the font size of each OutputView
            intermediate.setFontSize(MatrixMode.getInstance().activity.getFontSize());
            step.setFontSize((int) (MatrixMode.getInstance().activity.getFontSize() * CONSTANTS_IO_RATIO));

            //Enters the appropriate expressions to the OutputView
            Token[] entry = steps.get(position);
            ArrayList<Token> temp = new ArrayList<>();
            temp.add(entry[0]);
            intermediate.display(temp);
            temp.clear();
            temp.add(entry[1]);
            step.display(temp);
            temp.clear();

            //To respond to user touches
            final Token INPUT = steps.get(position)[0]; //Makes a constant reference so that intermediate matrices can be accessed by an inner class
            convertView.findViewById(R.id.reduction_wrapper).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Token> input = new ArrayList<>();
                    //Removes any StringTokens
                    if (!(INPUT instanceof StringToken)) {
                        input.add(INPUT);
                    }
                    //Adds the input expression to the current tokens
                    MatrixMode.getInstance().tokens.addAll(input); //Adds the input of the entry
                    finish();
                    MatrixMode.getInstance().updateInput();
                }
            });
            return convertView;
        }
    }
}
