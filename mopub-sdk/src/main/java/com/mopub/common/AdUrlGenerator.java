package com.mopub.common;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.mopub.common.util.IntentUtils;

import static com.mopub.common.ClientMetadata.MoPubNetworkType;

public abstract class AdUrlGenerator extends BaseUrlGenerator {
    private static TwitterAppInstalledStatus sTwitterAppInstalledStatus = TwitterAppInstalledStatus.UNKNOWN;

    protected Context mContext;
    protected String mAdUnitId;
    protected String mKeywords;
    protected Location mLocation;

    public AdUrlGenerator(Context context) {
        mContext = context;
    }

    @Deprecated // for testing
    public static void setTwitterAppInstalledStatus(TwitterAppInstalledStatus status) {
        sTwitterAppInstalledStatus = status;
    }

    public AdUrlGenerator withAdUnitId(String adUnitId) {
        mAdUnitId = adUnitId;
        return this;
    }

    public AdUrlGenerator withKeywords(String keywords) {
        mKeywords = keywords;
        return this;
    }

    public AdUrlGenerator withLocation(Location location) {
        mLocation = location;
        return this;
    }

    protected void setAdUnitId(String adUnitId) {
        addParam("id", adUnitId);
    }

    protected void setSdkVersion(String sdkVersion) {
        addParam("nv", sdkVersion);
    }

    protected void setKeywords(String keywords) {
        addParam("q", keywords);
    }

    protected void setLocation(@Nullable Location location) {
        Location bestLocation = location;
        Location locationFromLocationService = LocationService.getLastKnownLocation(mContext,
                MoPub.getLocationPrecision(),
                MoPub.getLocationAwareness());

        if (locationFromLocationService != null &&
                (location == null || locationFromLocationService.getTime() >= location.getTime())) {
            bestLocation = locationFromLocationService;
        }

        if (bestLocation != null) {
            addParam("ll", bestLocation.getLatitude() + "," + bestLocation.getLongitude());
            addParam("lla", "" + (int) bestLocation.getAccuracy());

            if (bestLocation == locationFromLocationService) {
                addParam("llsdk", "1");
            }
        }
    }

    protected void setTimezone(String timeZoneOffsetString) {
        addParam("z", timeZoneOffsetString);
    }

    protected void setOrientation(String orientation) {
        addParam("o", orientation);
    }

    protected void setDensity(float density) {
        addParam("sc_a", "" + density);
    }

    protected void setMraidFlag(boolean mraid) {
        if (mraid) addParam("mr", "1");
    }

    protected void setMccCode(String networkOperator) {
        String mcc = networkOperator == null ? "" : networkOperator.substring(0, mncPortionLength(networkOperator));
        addParam("mcc", mcc);
    }

    protected void setMncCode(String networkOperator) {
        String mnc = networkOperator == null ? "" : networkOperator.substring(mncPortionLength(networkOperator));
        addParam("mnc", mnc);
    }

    protected void setIsoCountryCode(String networkCountryIso) {
        addParam("iso", networkCountryIso);
    }

    protected void setCarrierName(String networkOperatorName) {
        addParam("cn", networkOperatorName);
    }

    protected void setNetworkType(MoPubNetworkType networkType) {
        addParam("ct", networkType);
    }

    private void addParam(String key, MoPubNetworkType value) {
        addParam(key, value.toString());
    }

    private int mncPortionLength(String networkOperator) {
        return Math.min(3, networkOperator.length());
    }

    protected void setTwitterAppInstalledFlag() {
        if (sTwitterAppInstalledStatus == TwitterAppInstalledStatus.UNKNOWN) {
            sTwitterAppInstalledStatus = getTwitterAppInstallStatus();
        }

        if (sTwitterAppInstalledStatus == TwitterAppInstalledStatus.INSTALLED) {
            addParam("ts", "1");
        }
    }

    public TwitterAppInstalledStatus getTwitterAppInstallStatus() {
        return IntentUtils.canHandleTwitterUrl(mContext) ? TwitterAppInstalledStatus.INSTALLED : TwitterAppInstalledStatus.NOT_INSTALLED;
    }

    /**
     * @deprecated As of release 2.4
     */
    @Deprecated
    public AdUrlGenerator withFacebookSupported(boolean enabled) {
        return this;
    }

    public static enum TwitterAppInstalledStatus {
        UNKNOWN,
        NOT_INSTALLED,
        INSTALLED,
    }
}
