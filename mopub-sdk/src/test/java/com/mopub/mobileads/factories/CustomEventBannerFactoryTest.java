package com.mopub.mobileads.factories;

import com.mopub.common.test.support.SdkTestRunner;
import com.mopub.mobileads.AdTypeTranslator;
import com.mopub.mobileads.CustomEventBanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.mopub.mobileads.AdTypeTranslator.CustomEventType.HTML_BANNER;
import static com.mopub.mobileads.AdTypeTranslator.CustomEventType.MRAID_BANNER;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SdkTestRunner.class)
public class CustomEventBannerFactoryTest {

    private CustomEventBannerFactory subject;

    @Before
    public void setup() {
        subject = new CustomEventBannerFactory();
    }

    @Test
    public void create_shouldCreateBanners() throws Exception {
        assertCustomEventClassCreated(MRAID_BANNER);
        assertCustomEventClassCreated(HTML_BANNER);
    }

    private void assertCustomEventClassCreated(AdTypeTranslator.CustomEventType customEventType) throws Exception {
        CustomEventBanner customEventBanner = subject.internalCreate(customEventType.toString());
        assertThat(customEventBanner.getClass().getName()).isEqualTo(customEventType.toString());
    }
}
