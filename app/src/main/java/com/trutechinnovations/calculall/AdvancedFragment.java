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
 * Handles the UI for Advanced Mode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class AdvancedFragment extends BasicFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.advanced, container, false);
        activity = (MainActivity) getActivity();
        Button recipButton = (Button) v.findViewById(R.id.reciprocal);
        MultiButton powButton = (MultiButton) v.findViewById(R.id.pow_ten_button);
        MultiButton expButton = (MultiButton) v.findViewById(R.id.power_button);
        powButton.setText(Html.fromHtml(getString(R.string.pow_of_ten)));
        expButton.setText(Html.fromHtml(getString(R.string.exponent)));
        if (recipButton != null) recipButton.setText(Html.fromHtml(getString(R.string.recip)));
        setupButtons(v);
        Advanced.getInstance().setFragment(this);
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
        MultiButton powButton = (MultiButton) v.findViewById(R.id.pow_ten_button);
        MultiButton expButton = (MultiButton) v.findViewById(R.id.power_button);
        MultiButton logButton = (MultiButton) v.findViewById(R.id.log_button);
        MultiButton rootButton = (MultiButton) v.findViewById(R.id.sqrt_button);
        MultiButton squareButton = (MultiButton) v.findViewById(R.id.square_button);
        //Sets up the MultiButtons
        Spanned[] sinStrings = new Spanned[4];
        Spanned[] cosStrings = new Spanned[4];
        Spanned[] tanStrings = new Spanned[4];
        Spanned[] powStrings = new Spanned[2];
        Spanned[] expStrings = new Spanned[2];
        Spanned[] logStrings = new Spanned[2];
        Spanned[] rootStrings = new Spanned[2];
        Spanned[] squareStrings = new Spanned[2];
        Command[] sinCommands = new Command[4];
        Command[] cosCommands = new Command[4];
        Command[] tanCommands = new Command[4];
        Command[] powCommands = new Command[2];
        Command[] expCommands = new Command[2];
        Command[] logCommands = new Command[2];
        Command[] rootCommands = new Command[2];
        Command[] squareCommands = new Command[2];
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

        powStrings[0] = Html.fromHtml(activity.getString(R.string.pow_of_ten));
        powStrings[1] = Html.fromHtml(activity.getString(R.string.pow_of_e));

        expStrings[0] = Html.fromHtml(activity.getString(R.string.exponent));
        expStrings[1] = Html.fromHtml(activity.getString(R.string.var_root));

        logStrings[0] = Html.fromHtml(activity.getString(R.string.log10));
        logStrings[1] = Html.fromHtml(activity.getString(R.string.ln));

        rootStrings[0] = Html.fromHtml(activity.getString(R.string.sqrt));
        rootStrings[1] = Html.fromHtml(activity.getString(R.string.cbrt));

        squareStrings[0] = Html.fromHtml(activity.getString(R.string.square));
        squareStrings[1] = Html.fromHtml(activity.getString(R.string.cube));
        //Sets the commands
        final Advanced advanced = (Advanced) Advanced.getInstance();
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

        powCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickPowerOfTen();
                return null;
            }
        };
        powCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickExp();
                return null;
            }
        };

        expCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickExponent();
                return null;
            }
        };
        expCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickVarRoot();
                return null;
            }
        };

        logCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickLog_10();
                return null;
            }
        };
        logCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickLn();
                return null;
            }
        };

        rootCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickSqrt();
                return null;
            }
        };
        rootCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickCbrt();
                return null;
            }
        };

        squareCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickSquare();
                return null;
            }
        };
        squareCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                advanced.clickCube();
                return null;
            }
        };

        sinButton.setModeTexts(sinStrings);
        sinButton.setOnClicks(sinCommands);

        cosButton.setModeTexts(cosStrings);
        cosButton.setOnClicks(cosCommands);

        tanButton.setModeTexts(tanStrings);
        tanButton.setOnClicks(tanCommands);

        powButton.setModeTexts(powStrings);
        powButton.setOnClicks(powCommands);

        expButton.setModeTexts(expStrings);
        expButton.setOnClicks(expCommands);

        logButton.setModeTexts(logStrings);
        logButton.setOnClicks(logCommands);

        rootButton.setModeTexts(rootStrings);
        rootButton.setOnClicks(rootCommands);

        squareButton.setModeTexts(squareStrings);
        squareButton.setOnClicks(squareCommands);

        //Stores the MultiButtons to the back-end
        ArrayList<MultiButton> buttons = new ArrayList<>();
        buttons.add(sinButton);
        buttons.add(cosButton);
        buttons.add(tanButton);
        buttons.add(powButton);
        buttons.add(expButton);
        buttons.add(logButton);
        buttons.add(rootButton);
        buttons.add(squareButton);
        advanced.setMultiButtons(buttons);

        //Sets the Angle mode button the the appropriate text
        Button angleModeButton = (Button) v.findViewById(R.id.angle_mode);
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
        ToggleButton mem = (ToggleButton) v.findViewById(R.id.hyp_button);
        ToggleButton hyp = (ToggleButton) v.findViewById(R.id.mem_button_a);
        shift.setChecked(((Advanced) Advanced.getInstance()).isShift());
        mem.setChecked(((Advanced) Advanced.getInstance()).isHyperbolic());
        hyp.setChecked(((Advanced) Advanced.getInstance()).isMem());
        ((Advanced) Advanced.getInstance()).updateMultiButtons();
    }
}
