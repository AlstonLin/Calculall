package com.trutechinnovations.calculall;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Defines the UI for FunctionMode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class FunctionFragment extends BasicFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.function, container, false);
        MultiButton expButton = (MultiButton) v.findViewById(R.id.power_button);
        expButton.setText(Html.fromHtml(getString(R.string.exponent)));
        setupButtons(v);
        FunctionMode.getInstance().setFragment(this);
        return v;
    }

    /**
     * Programatically sets the texts that can't be defined with XML.
     *
     * @param layout The view of this fragment
     */
    public void setupButtons(View layout) {
        //View layout = getView();
        MultiButton sinButton = (MultiButton) layout.findViewById(R.id.sin_button);
        MultiButton cosButton = (MultiButton) layout.findViewById(R.id.cos_button);
        MultiButton tanButton = (MultiButton) layout.findViewById(R.id.tan_button);
        MultiButton expButton = (MultiButton) layout.findViewById(R.id.power_button);
        MultiButton rootButton = (MultiButton) layout.findViewById(R.id.sqrt_button);
        MultiButton squareButton = (MultiButton) layout.findViewById(R.id.square_button_function);
        //Sets up the MultiButtons
        Spanned[] sinStrings = new Spanned[4];
        Spanned[] cosStrings = new Spanned[4];
        Spanned[] tanStrings = new Spanned[4];
        Spanned[] expStrings = new Spanned[2];
        Spanned[] rootStrings = new Spanned[2];
        Spanned[] squareStrings = new Spanned[2];
        Command[] sinCommands = new Command[4];
        Command[] cosCommands = new Command[4];
        Command[] tanCommands = new Command[4];
        Command[] expCommands = new Command[2];
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

        expStrings[0] = Html.fromHtml(activity.getString(R.string.exponent));
        expStrings[1] = Html.fromHtml(activity.getString(R.string.var_root));

        rootStrings[0] = Html.fromHtml(activity.getString(R.string.sqrt));
        rootStrings[1] = Html.fromHtml(activity.getString(R.string.cbrt));

        squareStrings[0] = Html.fromHtml(activity.getString(R.string.square));
        squareStrings[1] = Html.fromHtml(activity.getString(R.string.cube));
        //Sets the commands
        final FunctionMode functionMode = (FunctionMode) FunctionMode.getInstance();
        sinCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickSin();
                return null;
            }
        };
        sinCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickASin();
                return null;
            }
        };
        sinCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickSinh();
                return null;
            }
        };
        sinCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickASinh();
                return null;
            }
        };

        cosCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickCos();
                return null;
            }
        };
        cosCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickACos();
                return null;
            }
        };
        cosCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickCosh();
                return null;
            }
        };
        cosCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickACosh();
                return null;
            }
        };

        tanCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickTan();
                return null;
            }
        };
        tanCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickATan();
                return null;
            }
        };
        tanCommands[2] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickTanh();
                return null;
            }
        };
        tanCommands[3] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickATanh();
                return null;
            }
        };

        expCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickExponent();
                return null;
            }
        };
        expCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickVarRoot();
                return null;
            }
        };

        rootCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickSqrt();
                return null;
            }
        };
        rootCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickCbrt();
                return null;
            }
        };

        squareCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickSquare();
                return null;
            }
        };
        squareCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                functionMode.clickCube();
                return null;
            }
        };

        sinButton.setModeTexts(sinStrings);
        sinButton.setOnClicks(sinCommands);

        cosButton.setModeTexts(cosStrings);
        cosButton.setOnClicks(cosCommands);

        tanButton.setModeTexts(tanStrings);
        tanButton.setOnClicks(tanCommands);

        expButton.setModeTexts(expStrings);
        expButton.setOnClicks(expCommands);

        rootButton.setModeTexts(rootStrings);
        rootButton.setOnClicks(rootCommands);

        squareButton.setModeTexts(squareStrings);
        squareButton.setOnClicks(squareCommands);

        //Stores the MultiButtons to the back-end
        ArrayList<MultiButton> buttons = new ArrayList<>();
        buttons.add(sinButton);
        buttons.add(cosButton);
        buttons.add(tanButton);
        buttons.add(expButton);
        buttons.add(rootButton);
        buttons.add(squareButton);
        functionMode.setMultiButtons(buttons);

        //Restores the toggle buttons to their states
        ToggleButton shift = (ToggleButton) layout.findViewById(R.id.shift_button);
        ToggleButton hyp = (ToggleButton) layout.findViewById(R.id.hyp_button);
        shift.setChecked(((FunctionMode) FunctionMode.getInstance()).isShift());
        hyp.setChecked(((FunctionMode) FunctionMode.getInstance()).isHyperbolic());
        ((FunctionMode) FunctionMode.getInstance()).updateMultiButtons();
    }
}
