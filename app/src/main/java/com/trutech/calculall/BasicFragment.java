package com.trutech.calculall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.Random;

/**
 * Represents the front-end UI (Fragment) component of the overall Basic Mode.
 */
public class BasicFragment extends Fragment implements MoPubInterstitial.InterstitialAdListener{
    //CONSTANTS
    public static final String CLASS_NAME = Basic.class.getName();
    public static final int AD_RATE = 2; //Ads will show 1 in 2 activity opens
    private static final String AD_ID = "3ae32e9f72e2402cb01bbbaf1d6ba1f4";
    //PRIVATE VARIABLES
    private MoPubInterstitial interstitial;
    private boolean adShown = false;
    protected MainActivity activity;

    public BasicFragment(){
        activity = (MainActivity) getActivity();
    }

    /**
     * When the fragment needs to be created.
     *
     * @param inflater Inflater for the fragment
     * @param container What the fragment is contained within
     * @param savedInstanceState Not Used
     * @return The View of this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.activity_basic, container, false);
    }

    /**
     * Possibly shows an ad when a fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!(interstitial != null && interstitial.isReady()) && !adShown && !((Object) this).getClass().getName().equals(CLASS_NAME)) {
            Random random = new Random();
            if (random.nextInt(AD_RATE) == 0) {
                // Create the interstitial.
                interstitial = new MoPubInterstitial(getActivity(), AD_ID);
                interstitial.setInterstitialAdListener(this);
                interstitial.load();
                interstitial.load();
            }
        }
        activity = (MainActivity)getActivity();
        activity.getDisplay().clear();
    }

    /**
     * Stops the ad if not already shown.
     */
    @Override
    public void onPause() {
        if (interstitial != null) {
            interstitial.destroy(); //Prevents Ads from other activities appearing if it is not loaded before switching between them
        }
        activity.getDisplay().clear();
        super.onPause();
    }
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        adShown = true;
        if (interstitial.isReady()) {
            interstitial.show();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }

}
