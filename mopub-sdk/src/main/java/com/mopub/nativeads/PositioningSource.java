package com.mopub.nativeads;

import android.support.annotation.NonNull;

import com.mopub.nativeads.MoPubNativeAdPositioning.MoPubClientPositioning;

/**
 * Allows asynchronously requesting positioning information.
 */
interface PositioningSource {

    void loadPositions(@NonNull String adUnitId, @NonNull PositioningListener listener);

    interface PositioningListener {
        void onLoad(@NonNull MoPubClientPositioning positioning);

        void onFailed();
    }

}
