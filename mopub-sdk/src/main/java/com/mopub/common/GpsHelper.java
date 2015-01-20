package com.mopub.common;

import android.content.Context;
import android.os.AsyncTask;

import com.mopub.common.factories.MethodBuilderFactory;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.AsyncTasks;

import java.lang.ref.WeakReference;

import static com.mopub.common.util.Reflection.MethodBuilder;
import static com.mopub.common.util.Reflection.classFound;

public class GpsHelper {
    static public final int GOOGLE_PLAY_SUCCESS_CODE = 0;
    static public final String ADVERTISING_ID_KEY = "advertisingId";
    static public final String IS_LIMIT_AD_TRACKING_ENABLED_KEY = "isLimitAdTrackingEnabled";
    private static String sPlayServicesUtilClassName = "com.google.android.gms.common.GooglePlayServicesUtil";
    private static String sAdvertisingIdClientClassName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";

    static boolean isPlayServicesAvailable(final Context context) {
        try {
            MethodBuilder methodBuilder = MethodBuilderFactory.create(null, "isGooglePlayServicesAvailable")
                    .setStatic(Class.forName(sPlayServicesUtilClassName))
                    .addParam(Context.class, context);

            Object result = methodBuilder.execute();

            return (result != null && (Integer) result == GOOGLE_PLAY_SUCCESS_CODE);
        } catch (Exception exception) {
            return false;
        }
    }

    static public boolean isLimitAdTrackingEnabled(Context context) {
        final boolean defaultValue = false;
        if (isPlayServicesAvailable(context)) {
            return SharedPreferencesHelper.getSharedPreferences(context)
                    .getBoolean(IS_LIMIT_AD_TRACKING_ENABLED_KEY, defaultValue);
        } else {
            return defaultValue;
        }
    }

    static boolean isClientMetadataPopulated(final Context context) {
        return ClientMetadata.getInstance(context).isAdvertisingInfoSet();
    }

    static public void fetchAdvertisingInfoAsync(final Context context, final GpsHelperListener gpsHelperListener) {
        // This method guarantees that the Google Play Services (GPS) advertising info will
        // be populated if GPS is available and the ad info is not already cached
        // The above will happen before the callback is run
        boolean playServicesIsAvailable = isPlayServicesAvailable(context);
        if (playServicesIsAvailable && !isClientMetadataPopulated(context)) {
            internalFetchAdvertisingInfoAsync(context, gpsHelperListener);
        } else {
            if (gpsHelperListener != null) {
                gpsHelperListener.onFetchAdInfoCompleted();
            }
            if (playServicesIsAvailable) {
                // Kick off a request to update the ad information in the background.
                internalFetchAdvertisingInfoAsync(context, null);
            }
        }
    }

    static private void internalFetchAdvertisingInfoAsync(final Context context, final GpsHelperListener gpsHelperListener) {
        if (!classFound(sAdvertisingIdClientClassName)) {
            if (gpsHelperListener != null) {
                gpsHelperListener.onFetchAdInfoCompleted();
            }
            return;
        }

        try {
            AsyncTasks.safeExecuteOnExecutor(new FetchAdvertisingInfoTask(context, gpsHelperListener));
        } catch (Exception exception) {
            MoPubLog.d("Error executing FetchAdvertisingInfoTask", exception);

            if (gpsHelperListener != null) {
                gpsHelperListener.onFetchAdInfoCompleted();
            }
        }
    }

    static void updateClientMetadata(final Context context, final Object adInfo) {
        String advertisingId = reflectedGetAdvertisingId(adInfo, null);
        boolean isLimitAdTrackingEnabled = reflectedIsLimitAdTrackingEnabled(adInfo, false);

        /*
         * Committing using the editor is atomic; a single editor must always commit
         * to ensure that the state of the GPS variables are in sync.
         */

        ClientMetadata clientMetadata = ClientMetadata.getInstance(context);
        clientMetadata.setAdvertisingInfo(advertisingId, isLimitAdTrackingEnabled);
    }

    static String reflectedGetAdvertisingId(final Object adInfo, final String defaultValue) {
        try {
            return (String) MethodBuilderFactory.create(adInfo, "getId").execute();
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    static boolean reflectedIsLimitAdTrackingEnabled(final Object adInfo, final boolean defaultValue) {
        try {
            Boolean result = (Boolean) MethodBuilderFactory.create(adInfo, "isLimitAdTrackingEnabled").execute();
            return (result != null) ? result : defaultValue;
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    @Deprecated
    static public void setClassNamesForTesting() {
        // This method is used for testing only to help alleviate pain with testing
        // unlinked libraries via reflection
        // Set class names to something that is linked so Class.forName method doesn't throw
        String className = "java.lang.Class";
        sPlayServicesUtilClassName = className;
        sAdvertisingIdClientClassName = className;
    }

    public interface GpsHelperListener {
        public void onFetchAdInfoCompleted();
    }

    static private class FetchAdvertisingInfoTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContextWeakReference;
        private WeakReference<GpsHelperListener> mGpsHelperListenerWeakReference;

        public FetchAdvertisingInfoTask(Context context, GpsHelperListener gpsHelperListener) {
            mContextWeakReference = new WeakReference<Context>(context);
            mGpsHelperListenerWeakReference = new WeakReference<GpsHelperListener>(gpsHelperListener);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Context context = mContextWeakReference.get();
                if (context == null) {
                    return null;
                }

                MethodBuilder methodBuilder = MethodBuilderFactory.create(null, "getAdvertisingIdInfo")
                        .setStatic(Class.forName(sAdvertisingIdClientClassName))
                        .addParam(Context.class, context);

                Object adInfo = methodBuilder.execute();

                if (adInfo != null) {
                    updateClientMetadata(context, adInfo);
                }
            } catch (Exception exception) {
                MoPubLog.d("Unable to obtain Google AdvertisingIdClient.Info via reflection.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GpsHelperListener gpsHelperListener = mGpsHelperListenerWeakReference.get();
            if (gpsHelperListener != null) {
                gpsHelperListener.onFetchAdInfoCompleted();
            }
        }
    }
}

