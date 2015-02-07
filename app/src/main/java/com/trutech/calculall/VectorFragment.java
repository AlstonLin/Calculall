package com.trutech.calculall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Defines the UI ofr VectorMode.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class VectorFragment extends BasicFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VectorMode.getInstance().setFragment(this);
        return inflater.inflate(R.layout.vector, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
