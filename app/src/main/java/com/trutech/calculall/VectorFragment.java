package com.trutech.calculall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Defines the UI ofr VectorMode.
 */
public class VectorFragment extends BasicFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_vector, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
