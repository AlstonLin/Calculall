package com.mopub.mobileads.factories;

import android.content.Context;

import com.mopub.mobileads.util.vast.VastManager;

public class VastManagerFactory {
    protected static VastManagerFactory instance = new VastManagerFactory();

    public static VastManager create(final Context context) {
        return instance.internalCreate(context);
    }

    @Deprecated // for testing
    public static void setInstance(VastManagerFactory factory) {
        instance = factory;
    }

    public VastManager internalCreate(final Context context) {
        return new VastManager(context);
    }
}
