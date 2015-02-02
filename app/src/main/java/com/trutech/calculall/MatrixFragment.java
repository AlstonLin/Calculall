package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * Defines the UI for MatrixMode.
 *
 * @version Alpha 2.0
 * @author Alston Lin
 */
public class MatrixFragment extends BasicFragment{
    @Override
    public void onResume() {
        super.onResume();
        setupButtons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MatrixMode.getInstance().setFragment(this);
        return inflater.inflate(R.layout.activity_matrix, container, false);
    }

    /**
     * Programmaticly sets the texts that can't be defined with XML.
     */
    private void setupButtons() {
        Button transButton = (Button) getActivity().findViewById(R.id.transpose_button);
        Button powButton = (Button) getActivity().findViewById(R.id.pow_button);
        Button inverseButton = (Button) getActivity().findViewById(R.id.inverse_button);
        Button newButton = (Button) getActivity().findViewById(R.id.new_button);
        transButton.setText(Html.fromHtml(getString(R.string.transpose)));
        powButton.setText(Html.fromHtml(getString(R.string.matrix_pow)));
        inverseButton.setText(Html.fromHtml(getString(R.string.inverse_a)));
        newButton.setText(Html.fromHtml(getString(R.string.newe)));
    }
}
