package com.mopub.common.event;

/**
 * This interface represents a backend to which MoPub client events are logged.
 */
public interface EventRecorder {
    public void record(BaseEvent baseEvent);
}
