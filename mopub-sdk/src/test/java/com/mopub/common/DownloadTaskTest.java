package com.mopub.common;

import com.mopub.common.test.support.SdkTestRunner;
import com.mopub.common.util.ResponseHeader;
import com.mopub.mobileads.test.support.TestHttpResponseWithHeaders;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;

import static junit.framework.Assert.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SdkTestRunner.class)
public class DownloadTaskTest {

    @Mock private DownloadTask.DownloadTaskListener mockDownloadTaskListener;
    @Captor private ArgumentCaptor<DownloadResponse> responseCaptor;

    private DownloadTask mDownloadTask;
    private HttpGet httpGet;
    private String mTestResponse;
    private FakeHttpLayer mFakeHttpLayer;
    private TestHttpResponseWithHeaders mTestHttpResponseWithHeaders;

    @Before
    public void setUp() {
        mDownloadTask = new DownloadTask(mockDownloadTaskListener);
        try {
            httpGet = new HttpGet("http://www.mopub.com/");
        } catch (IllegalArgumentException e) {
            fail("Could not initialize HttpGet in test");
        }

        mTestResponse = "TEST RESPONSE";
        mTestHttpResponseWithHeaders = new TestHttpResponseWithHeaders(200, mTestResponse);
        mTestHttpResponseWithHeaders.addHeader(ResponseHeader.IMPRESSION_URL.getKey(), "moPubImpressionTrackerUrl");
        mTestHttpResponseWithHeaders.addHeader(ResponseHeader.CLICK_TRACKING_URL.getKey(), "moPubClickTrackerUrl");

        mFakeHttpLayer = Robolectric.getFakeHttpLayer();
    }

    @Test
    public void whenDownloadTaskAndHttpClientCompleteSuccessfully_shouldReturn200HttpResponse() {
        mFakeHttpLayer.addPendingHttpResponse(mTestHttpResponseWithHeaders);
        mDownloadTask.execute(httpGet);

        verify(mockDownloadTaskListener).onComplete(eq(httpGet.getURI().toString()), responseCaptor.capture());
        DownloadResponse response = responseCaptor.getValue();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getFirstHeader(ResponseHeader.IMPRESSION_URL)).isEqualTo("moPubImpressionTrackerUrl");
        assertThat(response.getFirstHeader(ResponseHeader.CLICK_TRACKING_URL)).isEqualTo("moPubClickTrackerUrl");
        assertThat(HttpResponses.asResponseString(response)).isEqualTo(mTestResponse);
    }

    @Test
    public void whenDownloadTaskCompletesSuccessfullyAndHttpClientTimesOut_shouldReturn599HttpResponse() {
        mFakeHttpLayer.addPendingHttpResponse(599, "");
        mDownloadTask.execute(httpGet);

        verify(mockDownloadTaskListener).onComplete(eq(httpGet.getURI().toString()),
                responseCaptor.capture());
        DownloadResponse response = responseCaptor.getValue();
        assertThat(response.getStatusCode()).isEqualTo(599);
        assertThat(HttpResponses.asResponseString(response)).isEqualTo("");
    }

    @Test
    public void whenDownloadTaskIsCancelledBeforeExecute_shouldNotCallOnComplete() {
        mFakeHttpLayer.addPendingHttpResponse(200, mTestResponse);
        mDownloadTask.cancel(true);
        mDownloadTask.execute(httpGet);

        verify(mockDownloadTaskListener, never()).onComplete(
                any(String.class), any(DownloadResponse.class));
    }

    @Ignore("pending")
    @Test
    public void whenHttpUriRequestThrowsIOException_shouldCancelTaskAndReturnNullHttpResponse() {
        // need a way to force HttpUriRequest to throw on execute
    }

    @Test
    public void whenHttpUriRequestIsNull_shouldReturnNullHttpReponseAndNullUrl() {
        mDownloadTask.execute((HttpUriRequest) null);
        verify(mockDownloadTaskListener).onComplete(null, null);
    }

    @Test
    public void whenHttpUriRequestIsNullArray_shouldReturnNullHttpReponseAndNullUrl() {
        mDownloadTask.execute((HttpUriRequest[]) null);
        verify(mockDownloadTaskListener).onComplete(null, null);
    }

    @Test
    public void whenHttpUriRequestIsArray_shouldOnlyReturnFirstResponse() {
        mFakeHttpLayer.addPendingHttpResponse(200, mTestResponse);
        mFakeHttpLayer.addPendingHttpResponse(500, "");
        mDownloadTask.execute(httpGet, new HttpGet("http://www.twitter.com/"));

        verify(mockDownloadTaskListener).onComplete(eq(httpGet.getURI().toString()),
                responseCaptor.capture());
        DownloadResponse response = responseCaptor.getValue();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(HttpResponses.asResponseString(response)).isEqualTo(mTestResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNullListener_shouldThrowIllegalArgumentException() {
        new DownloadTask(null);
    }
}
