package com.mopub.common.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.mopub.common.logging.MoPubLog;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static com.mopub.common.util.Reflection.MethodBuilder;
import static com.mopub.common.util.VersionCode.HONEYCOMB;
import static com.mopub.common.util.VersionCode.currentApiLevel;
import static java.util.Collections.list;

public class DeviceUtils {
    private static final int MAX_MEMORY_CACHE_SIZE = 30 * 1024 * 1024; // 30 MB
    private static final int MIN_DISK_CACHE_SIZE = 30 * 1024 * 1024; // 30 MB
    private static final int MAX_DISK_CACHE_SIZE = 100 * 1024 * 1024; // 100 MB

    private DeviceUtils() {
    }

    public static String getIpAddress(IP ip) throws SocketException {
        for (final NetworkInterface networkInterface : list(NetworkInterface.getNetworkInterfaces())) {
            for (final InetAddress address : list(networkInterface.getInetAddresses())) {
                if (!address.isLoopbackAddress()) {
                    String hostAddress = address.getHostAddress().toUpperCase(Locale.US);
                    if (ip.matches(hostAddress)) {
                        return ip.toString(hostAddress);
                    }
                }
            }
        }

        return null;
    }

    public static String getHashedUdid(final Context context) {
        if (context == null) {
            return null;
        }

        String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return Utils.sha1(udid);
    }

    public static String getUserAgent() {
        return System.getProperty("http.agent");
    }

    public static boolean isNetworkAvailable(final Context context) {
        if (context == null) {
            return false;
        }

        final int internetPermission = context.checkCallingOrSelfPermission(INTERNET);
        if (internetPermission == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        /**
         * This is only checking if we have permission to access the network state
         * It's possible to not have permission to check network state but still be able
         * to access the network itself.
         */
        final int networkAccessPermission = context.checkCallingOrSelfPermission(ACCESS_NETWORK_STATE);
        if (networkAccessPermission == PackageManager.PERMISSION_DENIED) {
            return true;
        }

        // Otherwise, perform the connectivity check.
        try {
            final ConnectivityManager connnectionManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connnectionManager.getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static int memoryCacheSizeBytes(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        long memoryClass = activityManager.getMemoryClass();

        if (currentApiLevel().isAtLeast(HONEYCOMB)) {
            try {
                final int flagLargeHeap = ApplicationInfo.class.getDeclaredField("FLAG_LARGE_HEAP").getInt(null);
                if (Utils.bitMaskContainsFlag(context.getApplicationInfo().flags, flagLargeHeap)) {
                    memoryClass = (Integer) new MethodBuilder(activityManager, "getLargeMemoryClass").execute();
                }
            } catch (Exception e) {
                MoPubLog.d("Unable to reflectively determine large heap size on Honeycomb and above.");
            }
        }

        long result = Math.min(MAX_MEMORY_CACHE_SIZE, memoryClass / 8 * 1024 * 1024);
        return (int) result;
    }

    public static long diskCacheSizeBytes(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long availableBytes = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            size = availableBytes / 50;
        } catch (IllegalArgumentException e) {
            MoPubLog.d("Unable to calculate 2% of available disk space, defaulting to minimum");
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    public static int getScreenOrientation(@NonNull final Activity activity) {
        final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        final boolean isPortrait =
                (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)) &&
                        height > width) ||
                        (((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) &&
                                width > height);

        if (isPortrait) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                default:
                    MoPubLog.d("Unknown screen orientation. Defaulting to portrait.");
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                default:
                    MoPubLog.d("Unknown screen orientation. Defaulting to landscape.");
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
    }

    public static enum IP {
        IPv4,
        IPv6;

        private boolean matches(final String address) {
            switch (this) {
                case IPv4:
                    return InetAddressUtils.isIPv4Address(address);
                case IPv6:
                    return InetAddressUtils.isIPv6Address(address);
                default:
                    return false;
            }
        }

        private String toString(final String address) {
            switch (this) {
                case IPv4:
                    return address;
                case IPv6:
                    return address.split("%")[0];
                default:
                    return null;
            }
        }
    }
}
