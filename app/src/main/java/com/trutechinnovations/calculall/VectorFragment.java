package com.trutechinnovations.calculall;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Defines the UI ofr VectorMode.
 *
 * @author Alston Lin
 * @version Alpha 3.0
 */
public class VectorFragment extends BasicFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VectorMode.getInstance().setFragment(this);
        View v = inflater.inflate(R.layout.vector, container, false);
        setupButtons(v);
        return v;
    }

    /**
     * Sets up the buttons.
     *
     * @param v The view of the Fragment
     */
    private void setupButtons(View v) {
        MultiButton sinButton = (MultiButton) v.findViewById(R.id.sin_button);
        MultiButton cosButton = (MultiButton) v.findViewById(R.id.cos_button);
        MultiButton tanButton = (MultiButton) v.findViewById(R.id.tan_button);
        //Sets up the MultiButtons
        Spanned[] sinStrings = new Spanned[4];
        Spanned[] cosStrings = new Spanned[4];
        Spanned[] tanStrings = new Spanned[4];
        Command[] sinCommands = new Command[4];
        Command[] cosCommands = new Command[4];
        Command[] tanCommands = new Command[4];
        //Sets the strings
        sinStrings[0] = SpannedString.valueOf("sin");
        sinStrings[1] = Html.fromHtml(getString(R.string.arcsin));
        sinStrings[2] = SpannedString.valueOf("sinh");
        sinStrings[3] = Html.fromHtml(getString(R.string.arcsinh));

        cosStrings[0] = SpannedString.valueOf("cos");
        cosStrings[1] = Html.fromHtml(getString(R.string.arccos));
        cosStrings[2] = SpannedString.valueOf("cosh");
        cosStrings[3] = Html.fromHtml(getString(R.string.arccosh));

        tanStrings[0] = SpannedString.valueOf("tan");
        tanStrings[1] = Html.fromHtml(getString(R.string.arctan));
        tanStrings[2] = SpannedString.valueOf("tanh");
        tanStrings[3] = Html.fromHtml(getString(R.string.arctanh));
        //Sets the commands
        final VectorMode advanced = (VectorMode) VectorMode.getInstance();
        sinCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickSin();
                return null;
            }
        };
        sinCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickASin();
                return null;
            }
        };
        sinCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickSinh();
                return null;
            }
        };
        sinCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickASinh();
                return null;
            }
        };

        cosCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickCos();
                return null;
            }
        };
        cosCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickACos();
                return null;
            }
        };
        cosCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickCosh();
                return null;
            }
        };
        cosCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickACosh();
                return null;
            }
        };

        tanCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickTan();
                return null;
            }
        };
        tanCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickATan();
                return null;
            }
        };
        tanCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickTanh();
                return null;
            }
        };
        tanCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickATanh();
                return null;
            }
        };

        sinButton.setModeTexts(sinStrings);
        sinButton.setOnClicks(sinCommands);

        cosButton.setModeTexts(cosStrings);
        cosButton.setOnClicks(cosCommands);

        tanButton.setModeTexts(tanStrings);
        tanButton.setOnClicks(tanCommands);

        //Stores the MultiButtons to the back-end
        ArrayList<MultiButton> buttons = new ArrayList<>();
        buttons.add(sinButton);
        buttons.add(cosButton);
        buttons.add(tanButton);

        advanced.setMultiButtons(buttons);

        //Sets the Angle mode button the the appropriate text
        Button angleModeButton = (Button) v.findViewById(R.id.angle_mode_vector);
        switch (Function.angleMode) {
            case Function.DEGREE:
                angleModeButton.setText("DEG");
                break;
            case Function.RADIAN:
                angleModeButton.setText("RAD");
                break;
            case Function.GRADIAN:
                angleModeButton.setText("GRAD");
                break;
        }
        //Restores the toggle buttons to their states
        ToggleButton shift = (ToggleButton) v.findViewById(R.id.shift_button);
        ToggleButton mem = (ToggleButton) v.findViewById(R.id.mem_button_v);
        shift.setChecked(((VectorMode) VectorMode.getInstance()).isShift());
        mem.setChecked(((VectorMode) VectorMode.getInstance()).isMem());
    }

}
