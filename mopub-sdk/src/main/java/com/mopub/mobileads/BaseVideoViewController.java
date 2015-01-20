package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.mopub.common.logging.MoPubLog;

import static com.mopub.mobileads.EventForwardingBroadcastReceiver.ACTION_INTERSTITIAL_CLICK;
import static com.mopub.mobileads.EventForwardingBroadcastReceiver.ACTION_INTERSTITIAL_FAIL;

public abstract class BaseVideoViewController {
    private final Context mContext;
    private final RelativeLayout mLayout;
    private final BaseVideoViewControllerListener mBaseVideoViewControllerListener;
    private long mBroadcastIdentifier;

    protected BaseVideoViewController(final Context context, final long broadcastIdentifier, final BaseVideoViewControllerListener baseVideoViewControllerListener) {
        mContext = context.getApplicationContext();
        mBroadcastIdentifier = broadcastIdentifier;
        mBaseVideoViewControllerListener = baseVideoViewControllerListener;
        mLayout = new RelativeLayout(mContext);
    }

    protected void onCreate() {
        final RelativeLayout.LayoutParams adViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLayout.addView(getVideoView(), 0, adViewLayout);
        mBaseVideoViewControllerListener.onSetContentView(mLayout);
    }

    protected abstract VideoView getVideoView();

    protected abstract void onPause();

    protected abstract void onResume();

    protected abstract void onDestroy();

    public boolean backButtonEnabled() {
        return true;
    }

    void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // By default, the activity result is ignored
    }

    protected BaseVideoViewControllerListener getBaseVideoViewControllerListener() {
        return mBaseVideoViewControllerListener;
    }

    protected Context getContext() {
        return mContext;
    }

    public ViewGroup getLayout() {
        return mLayout;
    }

    protected void videoError(boolean shouldFinish) {
        MoPubLog.e("Video cannot be played.");
        broadcastAction(ACTION_INTERSTITIAL_FAIL);
        if (shouldFinish) {
            mBaseVideoViewControllerListener.onFinish();
        }
    }

    protected void videoCompleted(boolean shouldFinish) {
        if (shouldFinish) {
            mBaseVideoViewControllerListener.onFinish();
        }
    }

    void videoClicked() {
        broadcastAction(ACTION_INTERSTITIAL_CLICK);
    }

    void broadcastAction(final String action) {
        EventForwardingBroadcastReceiver.broadcastAction(mContext, mBroadcastIdentifier, action);
    }

    public interface BaseVideoViewControllerListener {
        void onSetContentView(final View view);

        void onSetRequestedOrientation(final int requestedOrientation);

        void onFinish();

        void onStartActivityForResult(final Class<? extends Activity> clazz,
                                      final int requestCode,
                                      final Bundle extras);
    }
}
