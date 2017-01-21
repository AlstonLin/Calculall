package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by david on 1/20/2017.
 */

public class ConstantsActivity extends ListViewActivity {
    ArrayList<Constant> arrayOfConstants = new ArrayList<Constant>();//Constants data

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constants_view);

        setUpArraylist();
        //Finds the ListView from the inflated History XML so it could be manipulated
        ListView lv = (ListView) findViewById(R.id.constantsList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new ConstantsAdapter());
    }

    @Override
    public String getViewTitle() {
        return "Constants";
    }

    private void setUpArraylist(){

        //Populate arraylist
        arrayOfConstants.add(ConstantFactory.makeSpeedOfLight());
        arrayOfConstants.add(ConstantFactory.makePlanck());
        arrayOfConstants.add(ConstantFactory.makeRedPlanck());
        arrayOfConstants.add(ConstantFactory.makeGravitational());
        arrayOfConstants.add(ConstantFactory.makeGasConst());
        arrayOfConstants.add(ConstantFactory.makeBoltzmann());
        arrayOfConstants.add(ConstantFactory.makeAvogadro());
        arrayOfConstants.add(ConstantFactory.makeStefanBoltzmann());
        arrayOfConstants.add(ConstantFactory.makeFaraday());
        arrayOfConstants.add(ConstantFactory.makeMagnetic());
        arrayOfConstants.add(ConstantFactory.makeElectric());
        arrayOfConstants.add(ConstantFactory.makeCoulomb());
        arrayOfConstants.add(ConstantFactory.makeElemCharge());
        arrayOfConstants.add(ConstantFactory.makeElectronVolt());
        arrayOfConstants.add(ConstantFactory.makeElectronMass());
        arrayOfConstants.add(ConstantFactory.makeProtonMass());
        arrayOfConstants.add(ConstantFactory.makeNeutronMass());
        arrayOfConstants.add(ConstantFactory.makeAtomicMass());
        arrayOfConstants.add(ConstantFactory.makeBohrMagneton());
        arrayOfConstants.add(ConstantFactory.makeBohrRadius());
        arrayOfConstants.add(ConstantFactory.makeRydberg());
        arrayOfConstants.add(ConstantFactory.makeFineStruct());
        arrayOfConstants.add(ConstantFactory.makeMagneticFluxQuantum());
        arrayOfConstants.add(ConstantFactory.makeEarthGrav());
        arrayOfConstants.add(ConstantFactory.makeEarthMass());
        arrayOfConstants.add(ConstantFactory.makeEarthRadius());
        arrayOfConstants.add(ConstantFactory.makeSolarMass());
        arrayOfConstants.add(ConstantFactory.makeSolarRadius());
        arrayOfConstants.add(ConstantFactory.makeSolarLuminosity());
        arrayOfConstants.add(ConstantFactory.makeAU());
        arrayOfConstants.add(ConstantFactory.makeLightYear());
        arrayOfConstants.add(ConstantFactory.makePhi());
        arrayOfConstants.add(ConstantFactory.makeEulerMascheroni());
    }


    private static String prettifyConstValue(double d) {
        String s = Double.toString(d);
        String[] parts = s.split("E");
        if (parts.length < 2) {
            String[] temp = s.split("\\.");
            if (temp.length < 2 || temp.length > 2) {
                return s;
            } else if (temp.length == 2) {
                if (temp[0].length() >= 4) {
                    temp[0] = spaceOutString(new StringBuilder(temp[0]).reverse().toString());
                    temp[0] = new StringBuilder(temp[0]).reverse().toString();
                }
                if (temp[1].length() >= 4) {
                    temp[1] = spaceOutString(temp[1]);
                }
                s = temp[0].concat("." + temp[1]);
                return s;
            }
        } else if (parts.length == 2) {
            String[] temp = parts[0].split("\\.");
            if (temp.length < 2 || temp.length > 2) {
                return parts[0].concat(" × 10<sup><small>" + parts[1] + "</small></sup>");
            } else if (temp.length == 2) {
                if (temp[0].length() >= 4) {
                    temp[0] = spaceOutString(new StringBuilder(temp[0]).reverse().toString());
                    temp[0] = new StringBuilder(temp[0]).reverse().toString();
                }
                if (temp[1].length() >= 4) {
                    temp[1] = spaceOutString(temp[1]);
                }
                parts[0] = temp[0].concat("." + temp[1]);
                return parts[0].concat(" × 10<sup><small>" + parts[1] + "</small></sup>");
            }
        }
        return s;
    }

    private static String spaceOutString(String s) {
        return s.replaceAll("(.{3})", "$1 ");
    }

    /**
     * The custom Adapter for the ListView in the consts list.
     */
    private class ConstantsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayOfConstants.size();
        }

        @Override
        public Constant getItem(int position) {
            return arrayOfConstants.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
            //Get the data item for this position
            Constant constant = getItem(position);
            if (convertView == null) { //For efficiency purposes so that it does not unnecessarily inflate Views
                //Inflates the XML file to get the View of the consts element
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.constants_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            TextView constantName = (TextView) convertView.findViewById(R.id.constantName);
            TextView constantSymbol = (TextView) convertView.findViewById(R.id.constantSymbol);
            TextView constantVal = (TextView) convertView.findViewById(R.id.constantVal);
            TextView constantUnits = (TextView) convertView.findViewById(R.id.constantUnits);


            constantName.setText(constant.getName());

            //Set the constant symbol to be the actual symbol, the symbol var of the constant
            //is the numeric value to be displayed in the user's input
            constantSymbol.setText(Html.fromHtml(constant.getHTML()));

            constantVal.setText(Html.fromHtml(prettifyConstValue(constant.getNumericValue())));

            constantUnits.setText(Html.fromHtml(constant.getUnits()));

            //To respond to user touches
            final Constant cnst = arrayOfConstants.get(position); //Makes a constant reference so that cnst can be accessed by an inner class
            convertView.findViewById(R.id.constantContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DisplayView displayView = Advanced.getInstance().activity.display;
                    Advanced.getInstance().tokens.add(
                            displayView.getRealCursorIndex(),
                            VariableFactory.makeConstantToken(cnst)); //Adds the token to the input
                    Advanced.getInstance().updateInput();
                    displayView.setCursorIndex(displayView.getCursorIndex() + 1); //Moves the cursor to the right of the constant
                    Advanced.getInstance().updateOutput();
                    finish();
                }
            });
            return convertView;
        }
    }
}
