package com.trutechinnovations.calculall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Represents the front-end UI (Fragment) component of the overall Basic Mode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class BasicFragment extends Fragment {

    protected MainActivity activity;

    public BasicFragment() {
        activity = (MainActivity) getActivity();
    }

    /**
     * When the fragment needs to be created.
     *
     * @param inflater           Inflater for the fragment
     * @param container          What the fragment is contained within
     * @param savedInstanceState Not Used
     * @return The View of this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        Basic.getInstance().setFragment(this);
        return inflater.inflate(R.layout.basic, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = (MainActivity) getActivity();
    }

    /**
     * Stops the ad if not already shown.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

}
