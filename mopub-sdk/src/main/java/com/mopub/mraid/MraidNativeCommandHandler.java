package com.mopub.mraid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mopub.common.Preconditions;
import com.mopub.common.VisibleForTesting;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.AsyncTasks;
import com.mopub.common.util.IntentUtils;
import com.mopub.common.util.Streams;
import com.mopub.common.util.Utils;
import com.mopub.common.util.VersionCode;
import com.mopub.mobileads.factories.HttpClientFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.MEDIA_MOUNTED;
import static com.mopub.common.network.HeaderUtils.extractHeader;
import static com.mopub.common.util.ResponseHeader.LOCATION;

public class MraidNativeCommandHandler {
    public static final String ANDROID_CALENDAR_CONTENT_TYPE = "vnd.android.cursor.item/event";
    @VisibleForTesting
    static final String MIME_TYPE_HEADER = "Content-Type";

    private static final int MAX_NUMBER_DAYS_IN_MONTH = 31;
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ssZZZZZ",
            "yyyy-MM-dd'T'HH:mmZZZZZ"
    };

    void createCalendarEvent(final Context context, final Map<String, String> params)
            throws MraidCommandException {
        if (isCalendarAvailable(context)) {
            try {
                Map<String, Object> calendarParams = translateJSParamsToAndroidCalendarEventMapping(params);
                Intent intent = new Intent(Intent.ACTION_INSERT).setType(ANDROID_CALENDAR_CONTENT_TYPE);
                for (String key : calendarParams.keySet()) {
                    Object value = calendarParams.get(key);
                    if (value instanceof Long) {
                        intent.putExtra(key, ((Long) value).longValue());
                    } else if (value instanceof Integer) {
                        intent.putExtra(key, ((Integer) value).intValue());
                    } else {
                        intent.putExtra(key, (String) value);
                    }
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                MoPubLog.d("no calendar app installed");
                throw new MraidCommandException(
                        "Action is unsupported on this device - no calendar app installed");
            } catch (IllegalArgumentException e) {
                MoPubLog.d("create calendar: invalid parameters " + e.getMessage());
                throw new MraidCommandException(e);
            } catch (Exception e) {
                MoPubLog.d("could not create calendar event");
                throw new MraidCommandException(e);
            }
        } else {
            MoPubLog.d("unsupported action createCalendarEvent for devices pre-ICS");
            throw new MraidCommandException("Action is " +
                    "unsupported on this device (need Android version Ice Cream Sandwich or " +
                    "above)");
        }
    }

    void storePicture(@NonNull final Context context,
                      @NonNull final String imageUrl,
                      @NonNull MraidCommandFailureListener failureListener) throws MraidCommandException {
        if (!isStorePictureSupported(context)) {
            MoPubLog.d("Error downloading file - the device does not have an SD card mounted, or " +
                    "the Android permission is not granted.");
            throw new MraidCommandException("Error downloading file " +
                    " - the device does not have an SD card mounted, " +
                    "or the Android permission is not granted.");
        }

        if (context instanceof Activity) {
            showUserDialog(context, imageUrl, failureListener);
        } else {
            Toast.makeText(context, "Downloading image to Picture gallery...", Toast.LENGTH_SHORT).show();
            downloadImage(context, imageUrl, failureListener);
        }
    }

    boolean isTelAvailable(Context context) {
        Intent telIntent = new Intent(Intent.ACTION_DIAL);
        telIntent.setData(Uri.parse("tel:"));

        return IntentUtils.deviceCanHandleIntent(context, telIntent);
    }

    boolean isSmsAvailable(Context context) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:"));

        return IntentUtils.deviceCanHandleIntent(context, smsIntent);
    }

    public boolean isStorePictureSupported(Context context) {
        return MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.checkCallingOrSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    boolean isCalendarAvailable(Context context) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT).setType(ANDROID_CALENDAR_CONTENT_TYPE);

        return VersionCode.currentApiLevel().isAtLeast(VersionCode.ICE_CREAM_SANDWICH)
                && IntentUtils.deviceCanHandleIntent(context, calendarIntent);
    }

    /**
     * Inline video support was added in 3.1. Returns true if the activity has hardware acceleration
     * enabled in its foreground window and only if the View or any ParentView in the view tree
     * has not had hardware acceleration explicitly turned off.
     */
    // TargetApi is needed to access hardware accelerated flags
    @TargetApi(11)
    boolean isInlineVideoAvailable(@NonNull Activity activity, @NonNull View view) {
        // In addition to potential hardware acceleration problems, there is a problem in the WebKit
        // HTML5VideoView implementation pre-Gingerbread that would result in HTML5VideoViewProxy
        // holding on to an instance of the WebView even after the WebView is destroyed. For
        // this reason, we never allow inline video on Gingerbread devices.
        if (VersionCode.currentApiLevel().isBelow(VersionCode.HONEYCOMB_MR1)) {
            return false;
        }

        // Hardware Acceleration
        // Hardware acceleration for the application and activity is enabled by default
        // in API >= 14 (Ice Cream Sandwich)
        // http://developer.android.com/reference/android/R.attr.html#hardwareAccelerated
        // http://developer.android.com/guide/topics/graphics/hardware-accel.html

        // HTML5 Inline Video
        // http://developer.android.com/about/versions/android-3.1.html

        // Traverse up the View tree to determine if any views are being software rendered
        // You can only disable hardware acceleration at the view level by setting the layer type
        View tempView = view;
        while (true) {
            // View#isHardwareAccelerated does not reflect the layer type used to render the view
            // therefore we have to check for both
            if (!tempView.isHardwareAccelerated()
                    || Utils.bitMaskContainsFlag(tempView.getLayerType(), View.LAYER_TYPE_SOFTWARE)) {
                return false;
            }

            // If parent is not a view or parent is null then break
            if (!(tempView.getParent() instanceof View)) {
                break;
            }

            tempView = (View) tempView.getParent();
        }

        // Has hardware acceleration been enabled in the current window?
        // Hardware acceleration can only be enabled for a window, not disabled
        // This flag is automatically set by the system if the android:hardwareAccelerated
        // XML attribute is set to true on an activity or on the application.
        // http://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_HARDWARE_ACCELERATED
        Window window = activity.getWindow();
        if (window != null) {
            if (Utils.bitMaskContainsFlag(window.getAttributes().flags,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)) {
                return true;
            }
        }

        return false;
    }

    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
    private Map<String, Object> translateJSParamsToAndroidCalendarEventMapping(Map<String, String> params) {
        Map<String, Object> validatedParamsMapping = new HashMap<String, Object>();
        if (!params.containsKey("description") || !params.containsKey("start")) {
            throw new IllegalArgumentException("Missing start and description fields");
        }

        validatedParamsMapping.put(CalendarContract.Events.TITLE, params.get("description"));

        if (params.containsKey("start") && params.get("start") != null) {
            Date startDateTime = parseDate(params.get("start"));
            if (startDateTime != null) {
                validatedParamsMapping.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime.getTime());
            } else {
                throw new IllegalArgumentException("Invalid calendar event: start time is malformed. Date format expecting (yyyy-MM-DDTHH:MM:SS-xx:xx) or (yyyy-MM-DDTHH:MM-xx:xx) i.e. 2013-08-14T09:00:01-08:00");
            }
        } else {
            throw new IllegalArgumentException("Invalid calendar event: start is null.");
        }

        if (params.containsKey("end") && params.get("end") != null) {
            Date endDateTime = parseDate(params.get("end"));
            if (endDateTime != null) {
                validatedParamsMapping.put(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime.getTime());
            } else {
                throw new IllegalArgumentException("Invalid calendar event: end time is malformed. Date format expecting (yyyy-MM-DDTHH:MM:SS-xx:xx) or (yyyy-MM-DDTHH:MM-xx:xx) i.e. 2013-08-14T09:00:01-08:00");
            }
        }

        if (params.containsKey("location")) {
            validatedParamsMapping.put(CalendarContract.Events.EVENT_LOCATION, params.get("location"));
        }

        if (params.containsKey("summary")) {
            validatedParamsMapping.put(CalendarContract.Events.DESCRIPTION, params.get("summary"));
        }

        if (params.containsKey("transparency")) {
            validatedParamsMapping.put(
                    CalendarContract.Events.AVAILABILITY,
                    params.get("transparency").equals("transparent") ?
                            CalendarContract.Events.AVAILABILITY_FREE :
                            CalendarContract.Events.AVAILABILITY_BUSY
            );
        }

        validatedParamsMapping.put(CalendarContract.Events.RRULE, parseRecurrenceRule(params));

        return validatedParamsMapping;
    }

    private Date parseDate(String dateTime) {
        Date result = null;
        for (final String DATE_FORMAT : DATE_FORMATS) {
            try {
                result = new SimpleDateFormat(DATE_FORMAT, Locale.US).parse(dateTime);
                if (result != null) {
                    break;
                }
            } catch (ParseException e) {
                // an exception is okay, just try the next format and find the first one that works
            }
        }
        return result;
    }

    private String parseRecurrenceRule(Map<String, String> params) throws IllegalArgumentException {
        StringBuilder rule = new StringBuilder();
        if (params.containsKey("frequency")) {
            String frequency = params.get("frequency");
            int interval = -1;
            if (params.containsKey("interval")) {
                interval = Integer.parseInt(params.get("interval"));
            }
            if ("daily".equals(frequency)) {
                rule.append("FREQ=DAILY;");
                if (interval != -1) {
                    rule.append("INTERVAL=" + interval + ";");
                }
            } else if ("weekly".equals(frequency)) {
                rule.append("FREQ=WEEKLY;");
                if (interval != -1) {
                    rule.append("INTERVAL=" + interval + ";");
                }
                if (params.containsKey("daysInWeek")) {
                    String weekdays = translateWeekIntegersToDays(params.get("daysInWeek"));
                    if (weekdays == null) {
                        throw new IllegalArgumentException("invalid ");
                    }
                    rule.append("BYDAY=" + weekdays + ";");
                }
            } else if ("monthly".equals(frequency)) {
                rule.append("FREQ=MONTHLY;");
                if (interval != -1) {
                    rule.append("INTERVAL=" + interval + ";");
                }
                if (params.containsKey("daysInMonth")) {
                    String monthDays = translateMonthIntegersToDays(params.get("daysInMonth"));
                    if (monthDays == null) {
                        throw new IllegalArgumentException();
                    }
                    rule.append("BYMONTHDAY=" + monthDays + ";");
                }
            } else {
                throw new IllegalArgumentException("frequency is only supported for daily, weekly, and monthly.");
            }
        }
        return rule.toString();
    }

    private String translateWeekIntegersToDays(String expression) throws IllegalArgumentException {
        StringBuilder daysResult = new StringBuilder();
        boolean[] daysAlreadyCounted = new boolean[7];
        String[] days = expression.split(",");
        int dayNumber;
        for (final String day : days) {
            dayNumber = Integer.parseInt(day);
            dayNumber = dayNumber == 7 ? 0 : dayNumber;
            if (!daysAlreadyCounted[dayNumber]) {
                daysResult.append(dayNumberToDayOfWeekString(dayNumber) + ",");
                daysAlreadyCounted[dayNumber] = true;
            }
        }
        if (days.length == 0) {
            throw new IllegalArgumentException("must have at least 1 day of the week if specifying repeating weekly");
        }
        daysResult.deleteCharAt(daysResult.length() - 1);
        return daysResult.toString();
    }

    private String translateMonthIntegersToDays(String expression) throws IllegalArgumentException {
        StringBuilder daysResult = new StringBuilder();
        boolean[] daysAlreadyCounted = new boolean[2 * MAX_NUMBER_DAYS_IN_MONTH + 1]; //for -31 to 31
        String[] days = expression.split(",");
        int dayNumber;
        for (final String day : days) {
            dayNumber = Integer.parseInt(day);
            if (!daysAlreadyCounted[dayNumber + MAX_NUMBER_DAYS_IN_MONTH]) {
                daysResult.append(dayNumberToDayOfMonthString(dayNumber) + ",");
                daysAlreadyCounted[dayNumber + MAX_NUMBER_DAYS_IN_MONTH] = true;
            }
        }
        if (days.length == 0) {
            throw new IllegalArgumentException("must have at least 1 day of the month if specifying repeating weekly");
        }
        daysResult.deleteCharAt(daysResult.length() - 1);
        return daysResult.toString();
    }

    private String dayNumberToDayOfWeekString(int number) throws IllegalArgumentException {
        String dayOfWeek;
        switch (number) {
            case 0:
                dayOfWeek = "SU";
                break;
            case 1:
                dayOfWeek = "MO";
                break;
            case 2:
                dayOfWeek = "TU";
                break;
            case 3:
                dayOfWeek = "WE";
                break;
            case 4:
                dayOfWeek = "TH";
                break;
            case 5:
                dayOfWeek = "FR";
                break;
            case 6:
                dayOfWeek = "SA";
                break;
            default:
                throw new IllegalArgumentException("invalid day of week " + number);
        }
        return dayOfWeek;
    }

    private String dayNumberToDayOfMonthString(int number) throws IllegalArgumentException {
        String dayOfMonth;
        // https://android.googlesource.com/platform/frameworks/opt/calendar/+/504844526f1b7afec048c6d2976ffb332670d5ba/src/com/android/calendarcommon2/EventRecurrence.java
        if (number != 0 && number >= -MAX_NUMBER_DAYS_IN_MONTH && number <= MAX_NUMBER_DAYS_IN_MONTH) {
            dayOfMonth = "" + number;
        } else {
            throw new IllegalArgumentException("invalid day of month " + number);
        }
        return dayOfMonth;
    }

    void downloadImage(final Context context, final String uriString,
                       final MraidCommandFailureListener failureListener) {
        final DownloadImageAsyncTask downloadImageAsyncTask = new DownloadImageAsyncTask(context,
                new DownloadImageAsyncTask.DownloadImageAsyncTaskListener() {
                    @Override
                    public void onSuccess() {
                        MoPubLog.d("Image successfully saved.");
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(context, "Image failed to download.", Toast.LENGTH_SHORT).show();
                        MoPubLog.d("Error downloading and saving image file.");
                        failureListener.onFailure(new MraidCommandException("Error " +
                                "downloading and saving image file."));
                    }
                });
        AsyncTasks.safeExecuteOnExecutor(downloadImageAsyncTask, uriString);
    }

    private void showUserDialog(final Context context, final String imageUrl,
                                final MraidCommandFailureListener failureListener) {
        AlertDialog.Builder alertDialogDownloadImage = new AlertDialog.Builder(context);
        alertDialogDownloadImage
                .setTitle("Save Image")
                .setMessage("Download image to Picture gallery?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadImage(context, imageUrl, failureListener);
                    }
                })
                .setCancelable(true)
                .show();
    }

    interface MraidCommandFailureListener {
        void onFailure(MraidCommandException exception);
    }

    private static class DownloadImageAsyncTask extends AsyncTask<String, Void, Boolean> {
        private final Context mContext;
        private final DownloadImageAsyncTaskListener mListener;

        public DownloadImageAsyncTask(@NonNull final Context context,
                                      @NonNull final DownloadImageAsyncTaskListener listener) {
            super();
            mContext = context.getApplicationContext();
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(@NonNull String[] params) {
            Preconditions.checkState(params.length > 0);
            Preconditions.checkNotNull(params[0]);

            final File pictureStoragePath = getPictureStoragePath();

            //noinspection ResultOfMethodCallIgnored
            pictureStoragePath.mkdirs();

            final String uriString = params[0];
            URI uri = URI.create(uriString);

            final HttpClient httpClient = HttpClientFactory.create();
            final HttpGet httpGet = new HttpGet(uri);

            InputStream pictureInputStream = null;
            OutputStream pictureOutputStream = null;
            try {
                final HttpResponse httpResponse = httpClient.execute(httpGet);
                pictureInputStream = httpResponse.getEntity().getContent();

                final String redirectLocation = extractHeader(httpResponse, LOCATION);
                if (redirectLocation != null) {
                    uri = URI.create(redirectLocation);
                }

                final String pictureFileName = getFileNameForUriAndHttpResponse(uri, httpResponse);
                final File pictureFile = new File(pictureStoragePath, pictureFileName);
                pictureOutputStream = new FileOutputStream(pictureFile);
                Streams.copyContent(pictureInputStream, pictureOutputStream);

                final String pictureFileFullPath = pictureFile.toString();
                loadPictureIntoGalleryApp(pictureFileFullPath);

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                Streams.closeStream(pictureInputStream);
                Streams.closeStream(pictureOutputStream);
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success != null && success) {
                mListener.onSuccess();
            } else {
                mListener.onFailure();
            }
        }

        private String getFileNameForUriAndHttpResponse(final URI uri, final HttpResponse response) {
            final String path = uri.getPath();

            if (path == null) {
                return null;
            }

            String filename = new File(path).getName();

            Header header = response.getFirstHeader(MIME_TYPE_HEADER);
            if (header != null) {
                String[] fields = header.getValue().split(";");
                for (final String field : fields) {
                    String extension;
                    if (field.contains("image/")) {
                        extension = "." + field.split("/")[1];
                        if (!filename.endsWith(extension)) {
                            filename += extension;
                        }
                        break;
                    }
                }
            }

            return filename;
        }

        private File getPictureStoragePath() {
            return new File(Environment.getExternalStorageDirectory(), "Pictures");
        }

        private void loadPictureIntoGalleryApp(final String filename) {
            MoPubMediaScannerConnectionClient mediaScannerConnectionClient =
                    new MoPubMediaScannerConnectionClient(filename, null);
            final MediaScannerConnection mediaScannerConnection =
                    new MediaScannerConnection(mContext, mediaScannerConnectionClient);
            mediaScannerConnectionClient.setMediaScannerConnection(mediaScannerConnection);
            mediaScannerConnection.connect();
        }

        interface DownloadImageAsyncTaskListener {
            void onSuccess();

            void onFailure();
        }
    }

    private static class MoPubMediaScannerConnectionClient
            implements MediaScannerConnection.MediaScannerConnectionClient {
        private final String mFilename;
        private final String mMimeType;
        private MediaScannerConnection mMediaScannerConnection;

        private MoPubMediaScannerConnectionClient(String filename, String mimeType) {
            mFilename = filename;
            mMimeType = mimeType;
        }

        private void setMediaScannerConnection(MediaScannerConnection connection) {
            mMediaScannerConnection = connection;
        }

        @Override
        public void onMediaScannerConnected() {
            if (mMediaScannerConnection != null) {
                mMediaScannerConnection.scanFile(mFilename, mMimeType);
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (mMediaScannerConnection != null) {
                mMediaScannerConnection.disconnect();
            }
        }
    }
}
