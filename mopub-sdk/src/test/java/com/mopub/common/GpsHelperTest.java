package com.mopub.common;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;

import com.mopub.common.factories.MethodBuilderFactory;
import com.mopub.common.test.support.SdkTestRunner;
import com.mopub.common.util.test.support.TestMethodBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.concurrent.Semaphore;

import static com.mopub.common.util.Reflection.MethodBuilder;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SdkTestRunner.class)
public class GpsHelperTest {
    private MethodBuilder methodBuilder;
    private Activity context;
    private TestAdInfo adInfo;
    private Semaphore semaphore;
    private GpsHelper.GpsHelperListener semaphoreGpsHelperListener;
    private Looper gpsHelperListenerCallbackLooper;

    // This class emulates the AdInfo class returned from the Google Play Services
    // AdvertisingIdClient.getAdvertisingIdInfo method; need to implement getters for reflection calls
    public static final class TestAdInfo {
        public static final String ADVERTISING_ID = "38400000-8cf0-11bd-b23e-10b96e40000d";
        public static final boolean LIMIT_AD_TRACKING_ENABLED = true;

        public String mAdId = ADVERTISING_ID;
        public boolean mLimitAdTrackingEnabled = LIMIT_AD_TRACKING_ENABLED;

        public String getId() {
            return mAdId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return mLimitAdTrackingEnabled;
        }
    }

    @Before
    public void setup() {
    	context = new Activity();
        adInfo = new TestAdInfo();

        methodBuilder = TestMethodBuilderFactory.getSingletonMock();
        when(methodBuilder.setStatic(any(Class.class))).thenReturn(methodBuilder);
        when(methodBuilder.addParam(any(Class.class), any())).thenReturn(methodBuilder);

        semaphore = new Semaphore(0);
        semaphoreGpsHelperListener = new GpsHelper.GpsHelperListener() {
            @Override
            public void onFetchAdInfoCompleted() {
                gpsHelperListenerCallbackLooper = Looper.myLooper();
                semaphore.release();
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        reset(methodBuilder);
    }

    @Test
    public void isPlayServicesAvailable_whenGooglePlayServicesIsLinked_shouldReturnTrue() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(GpsHelper.GOOGLE_PLAY_SUCCESS_CODE);
        assertThat(GpsHelper.isPlayServicesAvailable(context)).isTrue();
    }

    @Test
    public void isPlayServicesAvailable_whenGooglePlayServicesReturnsNonSuccessCode_shouldReturnFalse() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(GpsHelper.GOOGLE_PLAY_SUCCESS_CODE + 1);
        assertThat(GpsHelper.isPlayServicesAvailable(context)).isFalse();
    }

    @Test
    public void isPlayServicesAvailable_whenGooglePlayServicesReturnsNull_shouldReturnFalse() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(null);
        assertThat(GpsHelper.isPlayServicesAvailable(context)).isFalse();
    }

    @Test
    public void isPlayServicesAvailable_whenGooglePlayServicesIsNotLinked_shouldReturnFalse() throws Exception {
        assertThat(GpsHelper.isPlayServicesAvailable(context)).isFalse();
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenGooglePlayServicesIsLinked_shouldInvokeCallbackOnMainLooper() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(
                adInfo,
                adInfo.mAdId,
                adInfo.mLimitAdTrackingEnabled
        );

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        assertThat(gpsHelperListenerCallbackLooper).isEqualTo(Looper.getMainLooper());
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenGooglePlayServicesIsLinked_shouldPopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(
                GpsHelper.GOOGLE_PLAY_SUCCESS_CODE,
                adInfo,
                adInfo.mAdId,
                adInfo.mLimitAdTrackingEnabled
        );

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyClientMetadata(context, adInfo);
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenReflectedMethodCallThrows_shouldNotPopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenThrow(new Exception());

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyCleanClientMetadata(context);
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenReflectedMethodCallReturnsNull_shouldNotPopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(null);

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyCleanClientMetadata(context);
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenGooglePlayServicesIsLinkedAndClientMetadataIsClean_shouldPopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(
                GpsHelper.GOOGLE_PLAY_SUCCESS_CODE,
                adInfo,
                adInfo.mAdId,
                adInfo.mLimitAdTrackingEnabled
        );

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyClientMetadata(context, adInfo);
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenGooglePlayServicesLinkedAndClientMetadataIsPopulated_shouldRePopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        populateAndVerifyClientMetadata(context, adInfo);
        adInfo.mLimitAdTrackingEnabled = false;
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(
                GpsHelper.GOOGLE_PLAY_SUCCESS_CODE,
                adInfo,
                adInfo.mAdId,
                adInfo.mLimitAdTrackingEnabled
        );

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyClientMetadata(context, adInfo);
    }

    @Test
    public void fetchAdvertisingInfoAsync_whenGooglePlayServicesIsNotLinked_shouldNotPopulateClientMetadata() throws Exception {
        verifyCleanClientMetadata(context);
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(
                GpsHelper.GOOGLE_PLAY_SUCCESS_CODE + 1
        );

        GpsHelper.fetchAdvertisingInfoAsync(context, semaphoreGpsHelperListener);
        safeAcquireSemaphore();
        verifyCleanClientMetadata(context);
    }

    @Test
    public void isClientMetadataPopulated_whenContainsAdvertisingIdKeyAndIsLimitAdTrackingEnabledKey_shouldReturnTrue() throws Exception {
        verifyCleanClientMetadata(context);
        populateAndVerifyClientMetadata(context, adInfo);
        assertThat(GpsHelper.isClientMetadataPopulated(context)).isTrue();
    }

    @Test
    public void isClientMetadataPopulated_whenClean_shouldReturnFalse() throws Exception {
        verifyCleanClientMetadata(context);
        assertThat(GpsHelper.isClientMetadataPopulated(context)).isFalse();
    }

    @Test
    public void updateClientMetadata_whenPassingInValidAdInfoObject_shouldUpdateClientMetadata() throws Exception {
        // Use the real MethodBuilderFactory for this test, not the mock one
        // Most mocks are set by default in SdkTestRunner setup
        MethodBuilderFactory.setInstance(new MethodBuilderFactory());
        verifyCleanClientMetadata(context);
        GpsHelper.updateClientMetadata(context, adInfo);
        verifyClientMetadata(context, adInfo);
    }

    @Test
    public void reflectedGetIsLimitAdTrackingEnabled_whenIsLimitAdTrackingEnabledIsSet_shouldReturnIsLimitAdTrackingEnabled() throws Exception {
        MethodBuilderFactory.setInstance(new MethodBuilderFactory());
        assertThat(GpsHelper.reflectedIsLimitAdTrackingEnabled(adInfo, false)).isEqualTo(adInfo.LIMIT_AD_TRACKING_ENABLED);
    }

    @Test
    public void reflectedGetIsLimitAdTrackingEnabled_whenReflectedMethodCallThrows_shouldReturnDefaultValue() throws Exception {
        when(methodBuilder.execute()).thenThrow(new Exception());
        assertThat(GpsHelper.reflectedIsLimitAdTrackingEnabled(new Object(), false)).isFalse();
        verify(methodBuilder).execute();
        assertThat(GpsHelper.reflectedIsLimitAdTrackingEnabled(new Object(), true)).isTrue();
        verify(methodBuilder, times(2)).execute();
    }

    @Test
    public void reflectedGetIsLimitAdTrackingEnabled_whenReflectedMethodCallReturnsNull_shouldReturnDefaultValue() throws Exception {
        when(methodBuilder.execute()).thenReturn(null);
        assertThat(GpsHelper.reflectedIsLimitAdTrackingEnabled(new Object(), false)).isFalse();
        verify(methodBuilder).execute();
        assertThat(GpsHelper.reflectedIsLimitAdTrackingEnabled(new Object(), true)).isTrue();
        verify(methodBuilder, times(2)).execute();
    }

    @Test
    public void reflectedGetAdvertisingId_whenAdvertisingIdIsSet_shouldReturnAdvertisingId() throws Exception {
        MethodBuilderFactory.setInstance(new MethodBuilderFactory());
        assertThat(GpsHelper.reflectedGetAdvertisingId(adInfo, null)).isEqualTo(adInfo.ADVERTISING_ID);
    }

    @Test
    public void reflectedGetAdvertisingId_whenReflectedMethodCallThrows_shouldReturnDefaultValue() throws Exception {
        when(methodBuilder.execute()).thenThrow(new Exception());
        assertThat(GpsHelper.reflectedGetAdvertisingId(new Object(), null)).isNull();
        verify(methodBuilder).execute();
        String defaultAdId = "TEST_DEFAULT";
        assertThat(GpsHelper.reflectedGetAdvertisingId(new Object(), defaultAdId)).isEqualTo(defaultAdId);
        verify(methodBuilder, times(2)).execute();
    }

    @Test
    public void isLimitAdTrackingEnabled_whenGooglePlayServicesIsLinkedAndLimitAdTrackingIsCached_shouldReturnLimitAdTracking() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(GpsHelper.GOOGLE_PLAY_SUCCESS_CODE);
        SharedPreferencesHelper.getSharedPreferences(context)
                .edit()
                .putBoolean(GpsHelper.IS_LIMIT_AD_TRACKING_ENABLED_KEY, adInfo.LIMIT_AD_TRACKING_ENABLED)
                .commit();
        assertThat(GpsHelper.isLimitAdTrackingEnabled(context)).isEqualTo(adInfo.LIMIT_AD_TRACKING_ENABLED);
    }

    @Test
    public void isLimitAdTrackingEnabled_whenGooglePlayServicesIsLinkedAndAdInfoIsNotCached_shouldReturnFalse() throws Exception {
        GpsHelper.setClassNamesForTesting();
        when(methodBuilder.execute()).thenReturn(GpsHelper.GOOGLE_PLAY_SUCCESS_CODE);
        assertThat(GpsHelper.isLimitAdTrackingEnabled(context)).isFalse();
    }

    @Test
    public void isLimitAdTrackingEnabled_whenGooglePlayServicesIsNotLinked_shouldReturnFalse() throws Exception {
        assertThat(GpsHelper.isLimitAdTrackingEnabled(context)).isFalse();
    }

    static public void populateAndVerifyClientMetadata(Context context, TestAdInfo adInfo) {
        ClientMetadata clientMetadata = ClientMetadata.getInstance(context);
        clientMetadata.setAdvertisingInfo(adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
        verifyClientMetadata(context, adInfo);
    }

    private void safeAcquireSemaphore() throws Exception {
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        semaphore.acquire();
    }

    static public void verifyClientMetadata(Context context, TestAdInfo adInfo) {
        ClientMetadata clientMetadata = ClientMetadata.getInstance(context);
        assertThat(clientMetadata.getAdvertisingId()).isEqualTo("ifa:" + adInfo.getId());
        assertThat(clientMetadata.isDoNotTrackSet()).isEqualTo(adInfo.isLimitAdTrackingEnabled());
    }

    static public void verifyCleanClientMetadata(Context context) {
        ClientMetadata clientMetadata = ClientMetadata.getInstance(context);
        assertThat(clientMetadata.isAdvertisingInfoSet()).isFalse();
    }
}

