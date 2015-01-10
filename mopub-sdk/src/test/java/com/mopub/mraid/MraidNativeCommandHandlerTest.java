package com.mopub.mraid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mopub.common.test.support.SdkTestRunner;
import com.mopub.mobileads.test.support.FileUtils;
import com.mopub.mobileads.test.support.TestHttpResponseWithHeaders;
import com.mopub.mobileads.test.support.ThreadUtils;
import com.mopub.mraid.MraidNativeCommandHandler.MraidCommandFailureListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.mopub.mraid.MraidNativeCommandHandler.ANDROID_CALENDAR_CONTENT_TYPE;
import static java.io.File.separator;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(SdkTestRunner.class)
public class MraidNativeCommandHandlerTest {
    private static final String IMAGE_URI_VALUE = "file://tmp/expectedFile.jpg";
    private static final int TIME_TO_PAUSE_FOR_NETWORK = 300;
    private static final String FAKE_IMAGE_DATA = "imageFileData";
    //XXX: Robolectric or JUNIT doesn't support the correct suffix ZZZZZ in the parse pattern, so replacing xx:xx with xxxx for time.
    private static final String CALENDAR_START_TIME = "2013-08-14T20:00:00-0000";


    @Mock MraidCommandFailureListener mraidCommandFailureListener;
    private MraidNativeCommandHandler subject;
    private Context context;
    private Map<String, String> params;

    private File expectedFile;
    private File pictureDirectory;
    private File fileWithoutExtension;
    private TestHttpResponseWithHeaders response;


    @Before
    public void setUp() throws Exception {
        subject = new MraidNativeCommandHandler();
        context = Robolectric.buildActivity(Activity.class).create().get();

        FileUtils.copyFile("etc/expectedFile.jpg", "/tmp/expectedFile.jpg");
        expectedFile = new File(Environment.getExternalStorageDirectory(), "Pictures" + separator + "expectedFile.jpg");
        pictureDirectory = new File(Environment.getExternalStorageDirectory(), "Pictures");
        fileWithoutExtension = new File(pictureDirectory, "file");
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_withActivityContext_shouldDisplayAlertDialog() throws Exception {
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);

        subject.storePicture(context, IMAGE_URI_VALUE, mraidCommandFailureListener);

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(alertDialog);

        assertThat(alertDialog.isShowing());

        assertThat(shadowAlertDialog.getTitle()).isEqualTo("Save Image");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("Download image to Picture gallery?");
        assertThat(shadowAlertDialog.isCancelable()).isTrue();

        assertThat(alertDialog.getButton(BUTTON_POSITIVE).hasOnClickListeners());
        assertThat(alertDialog.getButton(BUTTON_NEGATIVE)).isNotNull();
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_whenOkayClicked_shouldDownloadImage() throws Exception {
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);
        Robolectric.addPendingHttpResponse(response);

        subject.storePicture(context, IMAGE_URI_VALUE, mraidCommandFailureListener);

        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        ThreadUtils.pause(TIME_TO_PAUSE_FOR_NETWORK);

        assertThat(expectedFile.exists()).isTrue();
        assertThat(expectedFile.length()).isEqualTo(FAKE_IMAGE_DATA.length());
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_whenCancelClicked_shouldDismissDialog() throws Exception {
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);

        subject.storePicture(context, IMAGE_URI_VALUE, mraidCommandFailureListener);

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(alertDialog);

        alertDialog.getButton(BUTTON_NEGATIVE).performClick();
        assertThat(shadowAlertDialog.hasBeenDismissed()).isTrue();

        assertThat(expectedFile.exists()).isFalse();
        assertThat(expectedFile.length()).isEqualTo(0);
    }

    @Ignore("MRAID 2.0")
    @Test
    public void showUserDownloadImageAlert_withAppContext_shouldToastAndDownloadImage() throws Exception {
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);
        Robolectric.addPendingHttpResponse(response);

        assertThat(ShadowToast.shownToastCount()).isEqualTo(0);

        subject.storePicture(context.getApplicationContext(), IMAGE_URI_VALUE, mraidCommandFailureListener);
        ThreadUtils.pause(TIME_TO_PAUSE_FOR_NETWORK);

        assertThat(ShadowToast.shownToastCount()).isEqualTo(1);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Downloading image to Picture gallery...");

        Robolectric.runUiThreadTasks();

        assertThat(expectedFile.exists()).isTrue();
        assertThat(expectedFile.length()).isEqualTo(FAKE_IMAGE_DATA.length());
    }

    @Ignore("MRAID 2.0")
    @Test
    public void showUserDownloadImageAlert_withAppContext_whenDownloadImageFails_shouldDisplayFailureToastAndNotDownloadImage() throws Exception {
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);
        Robolectric.addPendingHttpResponse(response);

        assertThat(ShadowToast.shownToastCount()).isEqualTo(0);

        subject.storePicture(context, "this is an invalid image url and cannot be downloaded", mraidCommandFailureListener);
        ThreadUtils.pause(TIME_TO_PAUSE_FOR_NETWORK);

        assertThat(ShadowToast.shownToastCount()).isEqualTo(1);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Downloading image to Picture gallery...");

        Robolectric.runUiThreadTasks();

        assertThat(ShadowToast.shownToastCount()).isEqualTo(2);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Image failed to download.");

        assertThat(expectedFile.exists()).isFalse();
        assertThat(expectedFile.length()).isEqualTo(0);
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_whenStorePictureNotSupported_shouldFireErrorEvent_andNotToastNorAlertDialog() throws Exception {
        Robolectric.getShadowApplication().denyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        subject.storePicture(context, "http://image.jpg", mraidCommandFailureListener);

        assertThat(ShadowToast.shownToastCount()).isEqualTo(0);
        assertThat(ShadowAlertDialog.getLatestAlertDialog()).isNull();
        verify(mraidCommandFailureListener).onFailure(any(MraidCommandException.class));
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_withMimeTypeAndNoFileExtension_shouldSavePictureWithMimeType() throws Exception {
        String fileNameWithNoExtension = "https://www.somewhere.com/images/blah/file";

        assertThatMimeTypeWasAddedCorrectly(
                fileNameWithNoExtension,
                "image/jpg",
                "file.jpg",
                ".jpg");
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_withMultipleContentTypesAndNoFileExtension_shouldSavePictureWithMimeType() throws Exception {
        String fileNameWithNoExtension = "https://www.somewhere.com/images/blah/file";

        assertThatMimeTypeWasAddedCorrectly(
                fileNameWithNoExtension,
                "text/html; image/png",
                "file.png",
                ".png");
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_withMimeTypeAndFileExtension_shouldSavePictureWithFileExtension() throws Exception {
        String fileNameWithExtension = "https://www.somewhere.com/images/blah/file.extension";

        assertThatMimeTypeWasAddedCorrectly(
                fileNameWithExtension,
                "image/extension",
                "file.extension",
                ".extension");

        assertThat((expectedFile.getName()).endsWith(".extension.extension")).isFalse();
    }

    @Ignore("Mraid 2.0")
    @Test
    public void showUserDownloadImageAlert_withHttpUri_shouldRequestPictureFromNetwork() throws Exception {
        response = new TestHttpResponseWithHeaders(200, "OK");
        downloadImageForPendingResponse("https://www.google.com/images/srpr/logo4w.png", response);

        HttpUriRequest latestRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        assertThat(latestRequest.getURI()).isEqualTo(URI.create("https://www.google.com/images/srpr/logo4w.png"));
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withMinimumValidParams_atLeastICS_shouldCreateEventIntent() throws Exception {
        setupCalendarParams();

        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener, never()).onFailure(any(MraidCommandException.class));

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();

        assertThat(intent.getType()).isEqualTo(ANDROID_CALENDAR_CONTENT_TYPE);
        assertThat(intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK).isNotEqualTo(0);
        assertThat(intent.getStringExtra(CalendarContract.Events.TITLE)).isNotNull();
        assertThat(intent.getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, -1)).isNotEqualTo(-1);
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withoutSecondsOnStartDate_atLeastICS_shouldCreateEventIntent() throws Exception {
        setupCalendarParams();
        params.put("start", "2012-12-21T00:00-0500");

        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener, never()).onFailure(any(MraidCommandException.class));

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();

        assertThat(intent.getType()).isEqualTo(ANDROID_CALENDAR_CONTENT_TYPE);
        assertThat(intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK).isNotEqualTo(0);
        assertThat(intent.getStringExtra(CalendarContract.Events.TITLE)).isNotNull();
        assertThat(intent.getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, -1)).isNotEqualTo(-1);
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withDailyRecurrence_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "daily");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getType()).isEqualTo(ANDROID_CALENDAR_CONTENT_TYPE);
        assertThat(intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK).isNotEqualTo(0);
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=DAILY;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withDailyRecurrence_withInterval_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "daily");
        params.put("interval", "2");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=DAILY;INTERVAL=2;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=WEEKLY;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_withInterval_withOutWeekday_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");
        params.put("interval", "7");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=WEEKLY;INTERVAL=7;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_onAllWeekDays_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");
        params.put("daysInWeek", "0,1,2,3,4,5,6");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=WEEKLY;BYDAY=SU,MO,TU,WE,TH,FR,SA;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_onDuplicateWeekDays_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");
        params.put("daysInWeek", "3,2,3,3,7,0");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=WEEKLY;BYDAY=WE,TU,SU;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_withInterval_withWeekDay_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");
        params.put("interval", "1");
        params.put("daysInWeek", "1");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withDailyRecurrence_withWeeklyRecurrence_withMonthlyOccurence_shouldCreateDailyCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "daily");
        params.put("frequency", "daily");
        params.put("frequency", "daily");
        params.put("interval", "2");
        params.put("daysInWeek", "1");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=DAILY;INTERVAL=2;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withMonthlyRecurrence_withOutInterval_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "monthly");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=MONTHLY;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withMonthlyRecurrence_withInterval_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "monthly");
        params.put("interval", "2");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=MONTHLY;INTERVAL=2;");
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void createCalendarEvent_withMonthlyRecurrence_withOutInterval_withDaysOfMonth_shouldCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "monthly");
        params.put("daysInMonth", "2,-15");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();
        assertThat(intent.getStringExtra(CalendarContract.Events.RRULE)).isEqualTo("FREQ=MONTHLY;BYMONTHDAY=2,-15;");
    }

    @Ignore("Mraid 2.0")
    @Test
    public void createCalendarEvent_withMonthlyRecurrence_withInvalidDaysOfMonth_shouldNotCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "monthly");
        params.put("daysInMonth", "55");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();

        assertThat(intent).isNull();
        assertThat(ShadowLog.getLogs().size()).isEqualTo(1);
    }

    @Ignore("Mraid 2.0")
    @Test
    public void createCalendarEvent_withWeeklyRecurrence_withInvalidDaysOfWeek_shouldNotCreateCalendarIntent() throws Exception {
        setupCalendarParams();
        params.put("frequency", "weekly");
        params.put("daysInWeek", "-1,20");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();

        assertThat(intent).isNull();
        assertThat(ShadowLog.getLogs().size()).isEqualTo(1);
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.HONEYCOMB_MR2)
    @Test
    public void createCalendarEvent_beforeIcs_shouldFireErrorEvent() throws Exception {
        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener).onFailure(any(MraidCommandException.class));
    }

    @Ignore("Mraid 2.0")
    @Test
    public void createCalendarEvent_withInvalidDate_shouldFireErrorEvent() throws Exception {
        params.put("start", "2013-08-14T09:00.-08:00");
        params.put("description", "Some Event");

        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener).onFailure(any(MraidCommandException.class));
    }

    @Ignore("Mraid 2.0")
    @Test
    public void createCalendarEvent_withMissingParameters_shouldFireErrorEvent() throws Exception {
        //it needs a start time
        params.put("description", "Some Event");

        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener).onFailure(any(MraidCommandException.class));
    }

    @Ignore("Mraid 2.0")
    @Test
    public void createCalendarEvent_withNullDate_shouldFireErrorEvent() throws Exception {
        params.put("start", null);
        params.put("description", "Some Event");

        subject.createCalendarEvent(context, params);

        verify(mraidCommandFailureListener).onFailure(any(MraidCommandException.class));
    }

    @Ignore("Mraid 2.0")
    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void
    createCalendarEvent_withValidParamsAllExceptRecurrence_atLeastICS_shouldCreateEventIntent() throws Exception {
        setupCalendarParams();
        params.put("location", "my house");
        params.put("end", "2013-08-14T22:01:01-0000");
        params.put("summary", "some description actually");
        params.put("transparency", "transparent");

        subject.createCalendarEvent(context, params);

        Intent intent = Robolectric.getShadowApplication().getNextStartedActivity();

        assertThat(intent.getType()).isEqualTo(ANDROID_CALENDAR_CONTENT_TYPE);
        assertThat(intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK).isNotEqualTo(0);
        assertThat(intent.getStringExtra(CalendarContract.Events.TITLE)).isNotNull();
        assertThat(intent.getStringExtra(CalendarContract.Events.DESCRIPTION)).isNotNull();
        assertThat(intent.getStringExtra(CalendarContract.Events.EVENT_LOCATION)).isNotNull();
        assertThat(intent.getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, -1)).isNotEqualTo(-1);
        assertThat(intent.getLongExtra(CalendarContract.EXTRA_EVENT_END_TIME, -1)).isNotEqualTo(-1);
        assertThat(intent.getIntExtra(CalendarContract.Events.AVAILABILITY, -1)).isEqualTo(CalendarContract.Events.AVAILABILITY_FREE);
    }

    @Test
    public void isTelAvailable_whenCanAcceptIntent_shouldReturnTrue() throws Exception {
        context = createMockContextWithSpecificIntentData("tel", null, null, "android.intent.action.DIAL");

        assertThat(subject.isTelAvailable(context)).isTrue();
    }

    @Test
    public void isTelAvailable_whenCanNotAcceptIntent_shouldReturnFalse() throws Exception {
        context = createMockContextWithSpecificIntentData("", null, null, "android.intent.action.DIAL");

        assertThat(subject.isTelAvailable(context)).isFalse();
    }

    @Test
    public void isSmsAvailable_whenCanAcceptIntent_shouldReturnTrue() throws Exception {
        context = createMockContextWithSpecificIntentData("sms", null, null, "android.intent.action.VIEW");

        assertThat(subject.isSmsAvailable(context)).isTrue();
    }

    @Test
    public void isSmsAvailable_whenCanNotAcceptIntent_shouldReturnFalse() throws Exception {
        context = createMockContextWithSpecificIntentData("", null, null, "android.intent.action.VIEW");

        assertThat(subject.isSmsAvailable(context)).isFalse();
    }

    @Test
    public void isStorePictureAvailable_whenPermissionDeclaredAndMediaMounted_shouldReturnTrue() throws Exception {
        Robolectric.getShadowApplication().grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        assertThat(subject.isStorePictureSupported(context)).isTrue();
    }

    @Test
    public void isStorePictureAvailable_whenPermissionDenied_shouldReturnFalse() throws Exception {
        Robolectric.getShadowApplication().denyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        assertThat(subject.isStorePictureSupported(context)).isFalse();
    }

    @Test
    public void isStorePictureAvailable_whenMediaUnmounted_shouldReturnFalse() throws Exception {
        Robolectric.getShadowApplication().grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);

        assertThat(subject.isStorePictureSupported(context)).isFalse();
    }

    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void isCalendarAvailable_atLeastIcs_shouldReturnTrue() throws Exception {
        context = createMockContextWithSpecificIntentData(null, null, ANDROID_CALENDAR_CONTENT_TYPE, "android.intent.action.INSERT");
        assertThat(subject.isCalendarAvailable(context)).isTrue();
    }

    @Config(reportSdk = Build.VERSION_CODES.HONEYCOMB_MR2)
    @Test
    public void isCalendarAvailable_beforeIcs_shouldReturnFalse() throws Exception {
        context = createMockContextWithSpecificIntentData(null, null, ANDROID_CALENDAR_CONTENT_TYPE, "android.intent.action.INSERT");
        assertThat(subject.isCalendarAvailable(context)).isFalse();
    }

    @Config(reportSdk = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Test
    public void isCalendarAvailable_atLeastIcs_butCanNotAcceptIntent_shouldReturnFalse() throws
            Exception {
        context = createMockContextWithSpecificIntentData(null, null, "vnd.android.cursor.item/NOPE", "android.intent.action.INSERT");
        assertThat(subject.isCalendarAvailable(context)).isFalse();
    }

    @TargetApi(11)
    @Test
    public void isInlineVideoAvailable_whenViewsAreHardwareAccelerated_whenWindowIsHardwareAccelerated_whenApiLevelIsAtLeastHoneycombMR1_shouldReturnTrue() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        View mockView = mock(View.class);
        when(mockView.isHardwareAccelerated()).thenReturn(true);
        when(mockView.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);

        assertThat(subject.isInlineVideoAvailable(activity, mockView)).isTrue();
    }

    @TargetApi(11)
    @Test
    public void isInlineVideoAvailable_whenViewsAreHardwareAccelerated_whenWindowIsNotHardwareAccelerated_whenApiLevelIsAtLeastHoneycombMR1_shouldReturnFalse() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        View mockView = mock(View.class);
        when(mockView.isHardwareAccelerated()).thenReturn(true);
        when(mockView.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);

        assertThat(subject.isInlineVideoAvailable(activity, mockView)).isFalse();
    }

    @Config(reportSdk = Build.VERSION_CODES.HONEYCOMB)
    @TargetApi(11)
    @Test
    public void isInlineVideoAvailable_whenViewsAreHardwareAccelerated_whenWindowIsHardwareAccelerated_whenApiLevelIsLessThanHoneycombMR1_shouldReturnFalse() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        View mockView = mock(View.class);
        when(mockView.isHardwareAccelerated()).thenReturn(true);
        when(mockView.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);

        assertThat(subject.isInlineVideoAvailable(activity, mockView)).isFalse();
    }

    @TargetApi(11)
    @Test
    public void isInlineVideoAvailable_whenViewsAreNotHardwareAccelerated_whenWindowIsHardwareAccelerated_whenApiLevelIsAtLeastHoneycombMR1_shouldReturnFalse() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        View mockView = mock(View.class);
        when(mockView.isHardwareAccelerated()).thenReturn(false);
        when(mockView.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);

        assertThat(subject.isInlineVideoAvailable(activity, mockView)).isFalse();
    }

    @TargetApi(11)
    @Test
    public void isInlineVideoAvailable_whenViewParentIsNotHardwareAccelerated_whenWindowIsHardwareAccelerated_whenApiLevelIsAtLeastHoneycombMR1_shouldReturnFalse() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // ViewParent
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        when(mockLinearLayout.isHardwareAccelerated()).thenReturn(false);
        when(mockLinearLayout.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);

        // View
        View mockView = mock(View.class);
        when(mockView.isHardwareAccelerated()).thenReturn(true);
        when(mockView.getLayerType()).thenReturn(View.LAYER_TYPE_HARDWARE);
        when(mockView.getParent()).thenReturn(mockLinearLayout);

        assertThat(subject.isInlineVideoAvailable(activity, mockView)).isFalse();
    }

    private static Context createMockContextWithSpecificIntentData(final String scheme, final String componentName, final String type, final String action) {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);

        List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
        resolveInfos.add(new ResolveInfo());

        stub(context.getPackageManager()).toReturn(packageManager);

        BaseMatcher intentWithSpecificData = new BaseMatcher() {
            // check that the specific intent has the special data, i.e. "tel:", or a component name, or string type, based on a particular data

            @Override
            public boolean matches(Object item) {
                if (item != null && item instanceof Intent ){
                    boolean result = action != null || type != null || componentName != null || scheme != null;
                    if (action != null) {
                        if (((Intent) item).getAction() != null) {
                            result = result && action.equals(((Intent) item).getAction());
                        }
                    }

                    if (type != null) {
                        if (((Intent) item).getType() != null) {
                            result = result && type.equals(((Intent) item).getType());
                        }
                    }

                    if (componentName != null) {
                        if (((Intent) item).getComponent() != null) {
                            result = result && componentName.equals(((Intent) item).getComponent().getClassName());
                        }
                    }

                    if (scheme != null) {
                        if (((Intent) item).getData() != null) {
                            result = result && scheme.equals(((Intent) item).getData().getScheme());
                        }
                    }
                    return result;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        };

        // It is okay to query with specific intent or nothing, because by default, none of the query would normally any resolveInfo anyways
        stub(packageManager.queryIntentActivities((Intent) argThat(intentWithSpecificData), eq(0))).toReturn(resolveInfos);
        return context;
    }

    private void downloadImageForPendingResponse(String uri, HttpResponse response) throws Exception {
        Robolectric.addPendingHttpResponse(response);

        subject.storePicture(context, uri, mraidCommandFailureListener);

        ThreadUtils.pause(TIME_TO_PAUSE_FOR_NETWORK);
    }

    private void assertThatMimeTypeWasAddedCorrectly(String originalFileName, String contentType,
            String expectedFileName, String expectedExtension) throws Exception {
        expectedFile = new File(pictureDirectory, expectedFileName);
        response = new TestHttpResponseWithHeaders(200, FAKE_IMAGE_DATA);
        response.addHeader(MraidNativeCommandHandler.MIME_TYPE_HEADER, contentType);

        downloadImageForPendingResponse(originalFileName, response);

        assertThat(expectedFile.exists()).isTrue();
        assertThat(expectedFile.getName()).endsWith(expectedExtension);
        assertThat(fileWithoutExtension.exists()).isFalse();
    }

    private void setupCalendarParams() {
        //we need mock Context so that we can validate that isCalendarAvailable() is true
        Context mockContext = createMockContextWithSpecificIntentData(null,
                null, ANDROID_CALENDAR_CONTENT_TYPE, "android.intent.action.INSERT");

        //but a mock context does't know how to startActivity(), so we stub it to use ShadowContext for starting activity
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (!(invocation.getArguments()[0] instanceof Intent)) {
                    throw new ClassCastException("For some reason you are not passing the calendar intent properly");
                }
                Context shadowContext = Robolectric.getShadowApplication().getApplicationContext();
                shadowContext.startActivity((Intent) invocation.getArguments()[0]);
                return null;
            }
        }).when(mockContext).startActivity(any(Intent.class));

        params.put("description", "Some Event");
        params.put("start", CALENDAR_START_TIME);
    }
}