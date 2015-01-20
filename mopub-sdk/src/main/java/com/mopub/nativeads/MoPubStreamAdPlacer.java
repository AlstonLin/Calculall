package com.mopub.nativeads;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.common.Preconditions;
import com.mopub.common.Preconditions.NoThrow;
import com.mopub.common.VisibleForTesting;
import com.mopub.common.logging.MoPubLog;
import com.mopub.nativeads.MoPubNativeAdPositioning.MoPubClientPositioning;
import com.mopub.nativeads.MoPubNativeAdPositioning.MoPubServerPositioning;
import com.mopub.nativeads.PositioningSource.PositioningListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * @code MoPubStreamAdPlacer facilitates loading ads and placing them into a content stream.
 * <p/>
 * If you are inserting ads into a ListView, we recommend that you use a {@link MoPubAdAdapter}
 * instead of this class.
 * <p/>
 * To start loading ads, call {@link #loadAds}. We recommend passing targeting information to
 * increase the chance that you show ads that are relevant to your users.
 * <p/>
 * This class is not intended to be used by multiple threads. All calls should be made from the main
 * UI thread.
 */
public class MoPubStreamAdPlacer {
    /**
     * Constant representing that the view type for a given position is a regular content item
     * instead of an ad.
     */
    public static final int CONTENT_VIEW_TYPE = 0;
    private final static MoPubNativeAdLoadedListener EMPTY_NATIVE_AD_LOADED_LISTENER =
            new MoPubNativeAdLoadedListener() {
                @Override
                public void onAdLoaded(final int position) {
                }

                @Override
                public void onAdRemoved(final int position) {
                }
            };
    @NonNull
    private MoPubNativeAdLoadedListener mAdLoadedListener =
            EMPTY_NATIVE_AD_LOADED_LISTENER;
    // The visible range is the range of items which we believe are visible, inclusive.
    // Placing ads near this range makes for a smoother user experience when scrolling up
    // or down.
    private static final int MAX_VISIBLE_RANGE = 100;
    // A buffer around the visible range where we'll place ads if possible.
    private static final int RANGE_BUFFER = 10;
    @NonNull
    private final Context mContext;
    @NonNull
    private final Handler mPlacementHandler;
    @NonNull
    private final Runnable mPlacementRunnable;
    @NonNull
    private final PositioningSource mPositioningSource;
    @NonNull
    private final NativeAdSource mAdSource;
    @NonNull
    private final ImpressionTracker mImpressionTracker;
    @NonNull
    private final HashMap<NativeResponse, WeakReference<View>> mViewMap;
    @NonNull
    private final WeakHashMap<View, NativeResponse> mNativeResponseMap;
    private boolean mHasReceivedPositions;
    @NonNull
    private PlacementData mPendingPlacementData;
    private boolean mHasReceivedAds;
    private boolean mHasPlacedAds;
    @NonNull
    private PlacementData mPlacementData;
    @Nullable
    private MoPubAdRenderer mAdRenderer;
    @Nullable
    private String mAdUnitId;
    private int mVisibleRangeStart;
    private int mVisibleRangeEnd;
    private int mItemCount;
    private boolean mNeedsPlacement;

    /**
     * Creates a new MoPubStreamAdPlacer object.
     * <p/>
     * By default, the StreamAdPlacer will contact the server to determine ad positions. If you
     * wish to hard-code positions in your app, see {@link MoPubStreamAdPlacer(Context,
     * MoPubClientPositioning)}.
     *
     * @param context The activity context.
     */
    public MoPubStreamAdPlacer(@NonNull final Context context) {
        // MoPubClientPositioning is mutable, so we must take care not to hold a
        // reference to it that might be subsequently modified by the caller.
        this(context, MoPubNativeAdPositioning.serverPositioning());
    }

    /**
     * Creates a new MoPubStreamAdPlacer object, using server positioning.
     *
     * @param context       The activity context.
     * @param adPositioning A positioning object for specifying where ads will be placed in your
     *                      stream. See {@link MoPubNativeAdPositioning#serverPositioning()}.
     */
    public MoPubStreamAdPlacer(@NonNull final Context context,
                               @NonNull final MoPubServerPositioning adPositioning) {
        this(context,
                new NativeAdSource(),
                new ImpressionTracker(context),
                new ServerPositioningSource(context));
    }

    /**
     * Creates a new MoPubStreamAdPlacer object, using client positioning.
     *
     * @param context       The activity context.
     * @param adPositioning A positioning object for specifying where ads will be placed in your
     *                      stream. See {@link MoPubNativeAdPositioning#clientPositioning()}.
     */
    public MoPubStreamAdPlacer(@NonNull final Context context,
                               @NonNull final MoPubClientPositioning adPositioning) {
        // MoPubClientPositioning is mutable, so we must take care not to hold a
        // reference to it that might be subsequently modified by the caller.
        this(context,
                new NativeAdSource(),
                new ImpressionTracker(context),
                new ClientPositioningSource(adPositioning));
    }

    @VisibleForTesting
    MoPubStreamAdPlacer(@NonNull final Context context,
                        @NonNull final NativeAdSource adSource,
                        @NonNull final ImpressionTracker impressionTracker,
                        @NonNull final PositioningSource positioningSource) {
        Preconditions.checkNotNull(context, "context is not allowed to be null");
        Preconditions.checkNotNull(adSource, "adSource is not allowed to be null");
        Preconditions.checkNotNull(impressionTracker, "impressionTracker is not allowed to be " +
                "null");
        Preconditions.checkNotNull(positioningSource, "positioningSource is not allowed to be " +
                "null");

        mContext = context;
        mImpressionTracker = impressionTracker;
        mPositioningSource = positioningSource;
        mAdSource = adSource;
        mPlacementData = PlacementData.empty();

        mNativeResponseMap = new WeakHashMap<View, NativeResponse>();
        mViewMap = new HashMap<NativeResponse, WeakReference<View>>();

        mPlacementHandler = new Handler();
        mPlacementRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mNeedsPlacement) {
                    return;
                }
                placeAds();
                mNeedsPlacement = false;
            }
        };

        mVisibleRangeStart = 0;
        mVisibleRangeEnd = 0;
    }

    /**
     * Registers an ad renderer to use when displaying ads in your stream.
     * <p/>
     * This renderer will automatically create and render your view when you call {@link
     * #getAdView}. If you register a second renderer, it will replace the first, although this
     * behavior is subject to change in a future SDK version.
     *
     * @param adRenderer The ad renderer.
     */
    public void registerAdRenderer(@NonNull final MoPubAdRenderer adRenderer) {
        if (!NoThrow.checkNotNull(adRenderer, "Cannot register a null adRenderer")) {
            return;
        }
        mAdRenderer = adRenderer;
    }

    /**
     * Sets a listener that will be called after the SDK loads new ads from the server and places
     * them into your stream.
     * <p/>
     * The listener will be active between when you call {@link #loadAds} and when you call {@link
     * #destroy()}. You can also set the listener to {@code null} to remove the listener.
     * <p/>
     * Note that there is not a one to one correspondence between calls to {@link #loadAds} and this
     * listener. The SDK will call the listener every time an ad loads.
     *
     * @param listener The listener.
     */
    public void setAdLoadedListener(@Nullable final MoPubNativeAdLoadedListener listener) {
        mAdLoadedListener = (listener == null) ? EMPTY_NATIVE_AD_LOADED_LISTENER : listener;
    }

    /**
     * Start loading ads from the MoPub server.
     * <p/>
     * We recommend using {@link #loadAds(String, RequestParameters)} instead of this method, in
     * order to pass targeting information to the server.
     *
     * @param adUnitId The ad unit ID to use when loading ads.
     */
    public void loadAds(@NonNull final String adUnitId) {
        loadAds(adUnitId, /* requestParameters */ null);
    }

    /**
     * Start loading ads from the MoPub server, using the given request targeting information.
     * <p/>
     * When loading ads, use {@link MoPubNativeAdLoadedListener#onAdLoaded(int)} will be called for
     * each ad that is added to the stream.
     * <p/>
     * To refresh ads in your stream, call {@code loadAds} again. When new ads load, they will
     * replace the current ads in your stream. If you are using {@code MoPubNativeAdLoadedListener}
     * you will see a call to {@code onAdRemoved} for each of the old ads, followed by a calls to
     * {@code onAdLoaded}.
     *
     * @param adUnitId          The ad unit ID to use when loading ads.
     * @param requestParameters Targeting information to pass to the ad server.
     */
    public void loadAds(@NonNull final String adUnitId,
                        @Nullable final RequestParameters requestParameters) {
        if (!NoThrow.checkNotNull(adUnitId, "Cannot load ads with a null ad unit ID")) {
            return;
        }

        if (mAdRenderer == null) {
            MoPubLog.w("You must call registerAdRenderer before loading ads");
            return;
        }

        mAdUnitId = adUnitId;

        mHasPlacedAds = false;
        mHasReceivedPositions = false;
        mHasReceivedAds = false;

        mPositioningSource.loadPositions(adUnitId, new PositioningListener() {
            @Override
            public void onLoad(@NonNull final MoPubClientPositioning positioning) {
                handlePositioningLoad(positioning);
            }

            @Override
            public void onFailed() {
                // This will happen only if positions couldn't be loaded after several tries
                MoPubLog.d("Unable to show ads because ad positions could not be loaded from " +
                        "the MoPub ad server.");
            }
        });

        mAdSource.setAdSourceListener(new NativeAdSource.AdSourceListener() {
            @Override
            public void onAdsAvailable() {
                handleAdsAvailable();
            }
        });

        mAdSource.loadAds(mContext, adUnitId, requestParameters);
    }

    @VisibleForTesting
    void handlePositioningLoad(@NonNull final MoPubClientPositioning positioning) {
        PlacementData placementData = PlacementData.fromAdPositioning(positioning);
        if (mHasReceivedAds) {
            placeInitialAds(placementData);
        } else {
            mPendingPlacementData = placementData;
        }
        mHasReceivedPositions = true;
    }

    @VisibleForTesting
    void handleAdsAvailable() {
        // If we've already placed ads, just notify that we need placement.
        if (mHasPlacedAds) {
            notifyNeedsPlacement();
            return;
        }

        // Otherwise, we may need to place initial ads.
        if (mHasReceivedPositions) {
            placeInitialAds(mPendingPlacementData);
        }
        mHasReceivedAds = true;
    }

    private void placeInitialAds(PlacementData placementData) {
        // Remove ads that may be present and immediately place ads again. This prevents the UI
        // from flashing grossly.
        removeAdsInRange(0, mItemCount);

        mPlacementData = placementData;
        placeAds();
        mHasPlacedAds = true;
    }

    /**
     * Inserts ads that should appear in the given range.
     * <p/>
     * By default, the ad placer will place ads withing the first 10 positions in your stream,
     * according the positions you've specified. You can should use this method as your user scrolls
     * through your stream to place ads into the currently visible range.
     * <p/>
     * This method takes advantage of a short-lived in memory ad cache, and will immediately place
     * any ads from the cache. If there are no ads in the cache, this method will load additional
     * ads from the server and place them once they are loaded. If you call {@code placeAdsInRange}
     * again before ads are retrieved from the server, the new ads will show in the new positions
     * rather than the old positions.
     * <p/>
     * You can pass any integer as a startPosition and endPosition for the range, including negative
     * numbers or numbers greater than the current stream item count. The ad placer will only place
     * ads between 0 and item count.
     *
     * @param startPosition The start of the range in which to place ads, inclusive.
     * @param endPosition   The end of the range in which to place ads, exclusive.
     */
    public void placeAdsInRange(final int startPosition, final int endPosition) {
        mVisibleRangeStart = startPosition;
        mVisibleRangeEnd = Math.min(endPosition, startPosition + MAX_VISIBLE_RANGE);
        notifyNeedsPlacement();
    }

    /**
     * Whether the given position is an ad.
     * <p/>
     * This will return {@code true} only if there is an ad loaded for this position. You can listen
     * for ads to load using {@link MoPubNativeAdLoadedListener#onAdLoaded(int)}.
     *
     * @param position The position to check for an ad, expressed in terms of the position in the
     *                 stream including ads.
     * @return Whether there is an ad at the given position.
     */
    public boolean isAd(final int position) {
        return mPlacementData.isPlacedAd(position);
    }

    /**
     * Stops loading ads, immediately clearing any ads currently in the stream.
     * <p/>
     * This method also stops ads from loading as the user moves through the stream. If you want to
     * just remove ads but want to continue loading them, call {@link #removeAdsInRange(int, int)}.
     * <p/>
     * When ads are cleared, {@link MoPubNativeAdLoadedListener#onAdRemoved} will be called for each
     * ad that is removed from the stream.
     */
    public void clearAds() {
        removeAdsInRange(0, mItemCount);
        mAdSource.clear();
    }

    /**
     * Destroys the ad placer, preventing it from future use.
     * <p/>
     * You must call this method before the hosting activity for this class is destroyed in order to
     * avoid a memory leak. Typically you should destroy the adapter in the life-cycle method that
     * is counterpoint to the method you used to create the adapter. For example, if you created the
     * adapter in {@code Fragment#onCreateView} you should destroy it in {code
     * Fragment#onDestroyView}.
     */
    public void destroy() {
        mPlacementHandler.removeMessages(0);
        mAdSource.clear();
        mImpressionTracker.destroy();
        mPlacementData.clearAds();
    }

    /**
     * Returns an ad data object, or {@code null} if there is no ad at this position.
     * <p/>
     * This method is useful when implementing your own Adapter using {@code MoPubStreamAdPlacer}.
     * To avoid worrying about view type, consider using {@link MoPubAdAdapter} instead of this
     * class.
     *
     * @param position The position where to place an ad.
     * @return An object representing ad data.
     */
    @Nullable
    public Object getAdData(final int position) {
        return mPlacementData.getPlacedAd(position);
    }

    /**
     * Gets the ad at the given position, or {@code null} if there is no ad at the given position.
     * <p/>
     * This method will attempt to reuse the convertView if it is not {@code null}, and will
     * otherwise create it. See {@link MoPubAdRenderer#createAdView(Context, ViewGroup)}.
     *
     * @param position    The position where to place an ad.
     * @param convertView A recycled view into which to render data, or {@code null}.
     * @param parent      The parent that the view will eventually be attached to.
     * @return The newly placed ad view.
     */
    @Nullable
    public View getAdView(final int position, @Nullable final View convertView,
                          @Nullable final ViewGroup parent) {
        final NativeAdData adData = mPlacementData.getPlacedAd(position);
        if (adData == null) {
            return null;
        }

        final MoPubAdRenderer adRenderer = adData.getAdRenderer();
        final View view = (convertView != null) ?
                convertView : adRenderer.createAdView(mContext, parent);

        NativeResponse nativeResponse = adData.getAd();
        WeakReference<View> mappedViewRef = mViewMap.get(nativeResponse);
        View mappedView = null;
        if (mappedViewRef != null) {
            mappedView = mappedViewRef.get();
        }
        if (!view.equals(mappedView)) {
            clearNativeResponse(mappedView);
            clearNativeResponse(view);
            prepareNativeResponse(nativeResponse, view);
            //noinspection unchecked
            adRenderer.renderAdView(view, nativeResponse);
        }

        return view;
    }

    /**
     * Removes ads in the given range from [startRange, endRange).
     *
     * @param originalStartPosition The start position to clear, expressed as the original content
     *                              position before ads were inserted.
     * @param originalEndPosition   The position after end position to clear, expressed as the
     *                              original content position before ads were inserted.
     * @return The number of ads removed.
     */
    public int removeAdsInRange(int originalStartPosition, int originalEndPosition) {
        int[] positions = mPlacementData.getPlacedAdPositions();

        int adjustedStartRange = mPlacementData.getAdjustedPosition(originalStartPosition);
        int adjustedEndRange = mPlacementData.getAdjustedPosition(originalEndPosition);

        ArrayList<Integer> removedPositions = new ArrayList<Integer>();
        // Traverse in reverse order to make this less error-prone for developers who are removing
        // views directly from their UI.
        for (int i = positions.length - 1; i >= 0; --i) {
            int position = positions[i];
            if (position < adjustedStartRange || position >= adjustedEndRange) {
                continue;
            }

            removedPositions.add(position);

            // Decrement the start range for any removed ads. We don't bother to decrement the end
            // range, as it is OK if it isn't 100% accurate.
            if (position < mVisibleRangeStart) {
                mVisibleRangeStart--;
            }
            mItemCount--;
        }

        int clearedAdsCount = mPlacementData.clearAdsInRange(adjustedStartRange, adjustedEndRange);
        for (int position : removedPositions) {
            mAdLoadedListener.onAdRemoved(position);
        }
        return clearedAdsCount;
    }

    /**
     * Returns the number of ad view types that can be placed by this ad placer. The number of
     * possible ad view types is currently 1, but this is subject to change in future SDK versions.
     *
     * @return The number of ad view types.
     * @see #getAdViewType
     */
    public int getAdViewTypeCount() {
        return 1;
    }

    /**
     * The ad view type for this position.
     * <p/>
     * Returns 0 if this is a regular content item. Otherwise, returns a number between 1 and {@link
     * #getAdViewTypeCount}.
     * <p/>
     * This method is useful when implementing your own Adapter using {@code MoPubStreamAdPlacer}.
     * To avoid worrying about view type, consider using {@link MoPubAdAdapter} instead of this
     * class.
     *
     * @param position The stream position.
     * @return The ad view type.
     */
    public int getAdViewType(final int position) {
        return isAd(position) ? 1 : CONTENT_VIEW_TYPE;
    }

    /**
     * Returns the original position of an item considering ads in the stream.
     * <p/>
     * For example if your stream looks like:
     * <p/>
     * {@code Item0 Ad Item1 Item2 Ad Item3 </code>
     * <p/>
     * {@code getOriginalPosition(5)} will return {@code 3}.
     *
     * @param position The adjusted position.
     * @return The original position before placing ads.
     */
    public int getOriginalPosition(final int position) {
        return mPlacementData.getOriginalPosition(position);
    }

    /**
     * Returns the position of an item considering ads in the stream.
     *
     * @param originalPosition The original position.
     * @return The position adjusted by placing ads.
     */
    public int getAdjustedPosition(final int originalPosition) {
        return mPlacementData.getAdjustedPosition(originalPosition);
    }

    /**
     * Returns the original number of items considering ads in the stream.
     *
     * @param count The number of items in the stream.
     * @return The original number of items before placing ads.
     */
    public int getOriginalCount(final int count) {
        return mPlacementData.getOriginalCount(count);
    }

    /**
     * Returns the number of items considering ads in the stream.
     *
     * @param originalCount The original number of items.
     * @return The number of items adjusted by placing ads.
     */
    public int getAdjustedCount(final int originalCount) {
        return mPlacementData.getAdjustedCount(originalCount);
    }

    /**
     * Sets the original number of items in your stream.
     * <p/>
     * You must call this method so that the placer knows where valid positions are to place ads.
     * After calling this method, the ad placer will call {@link
     * MoPubNativeAdLoadedListener#onAdLoaded
     * (int)} each time an ad is loaded in the stream.
     *
     * @param originalCount The original number of items.
     */
    public void setItemCount(final int originalCount) {
        mItemCount = mPlacementData.getAdjustedCount(originalCount);

        // If we haven't already placed ads, we'll let ads get placed by the normal loadAds call
        if (mHasPlacedAds) {
            notifyNeedsPlacement();
        }
    }

    /**
     * Inserts a content row at the given position, adjusting ad positions accordingly.
     * <p/>
     * Use this method if you are inserting an item into your stream and want to increment ad
     * positions based on that new item.
     * <p/>
     * For example if your stream looks like:
     * <p/>
     * {@code Item0 Ad Item1 Item2 Ad Item3}
     * <p/>
     * and you insert an item at position 2, your new stream will look like:
     * <p/>
     * {@code Item0 Ad Item1 Item2 New Item Ad Item3}
     *
     * @param originalPosition The position at which to add an item. If you have an adjusted
     *                         position, you will need to call {@link #getOriginalPosition} to get this value.
     */
    public void insertItem(final int originalPosition) {
        mPlacementData.insertItem(originalPosition);
    }

    /**
     * Removes the content row at the given position, adjusting ad positions accordingly.
     * <p/>
     * Use this method if you are removing an item from your stream and want to decrement ad
     * positions based on that removed item.
     * <p/>
     * For example if your stream looks like:
     * <p/>
     * {@code Item0 Ad Item1 Item2 Ad Item3}
     * <p/>
     * and you remove an item at position 2, your new stream will look like:
     * <p/>
     * {@code Item0 Ad Item1 Ad Item3}
     *
     * @param originalPosition The position at which to add an item. If you have an adjusted
     *                         position, you will need to call {@link #getOriginalPosition} to get this value.
     */
    public void removeItem(final int originalPosition) {
        mPlacementData.removeItem(originalPosition);
    }

    /**
     * Moves the content row at the given position adjusting ad positions accordingly.
     * <p/>
     * Use this method if you are moving an item in your stream and want to have ad positions move
     * as well.
     *
     * @param originalPosition The position from which to move an item. If you have an adjusted
     *                         position, you will need to call {@link #getOriginalPosition} to get this value.
     * @param newPosition      The new position, also expressed in terms of the original position.
     */
    public void moveItem(final int originalPosition, final int newPosition) {
        mPlacementData.moveItem(originalPosition, newPosition);
    }

    private void notifyNeedsPlacement() {
        // Avoid posting if this method has already been called.
        if (mNeedsPlacement) {
            return;
        }
        mNeedsPlacement = true;

        // Post the placement to happen on the next UI render loop.
        mPlacementHandler.post(mPlacementRunnable);
    }

    /**
     * Places ads using the current visible range.
     */
    private void placeAds() {
        // Place ads within the visible range
        if (!tryPlaceAdsInRange(mVisibleRangeStart, mVisibleRangeEnd)) {
            return;
        }

        // Place ads after the visible range so that user will see an ad if they scroll down. We
        // don't place an ad before the visible range, because we are trying to be mindful of
        // changes that will affect scrolling.
        tryPlaceAdsInRange(mVisibleRangeEnd, mVisibleRangeEnd + RANGE_BUFFER);
    }

    /**
     * Attempts to place ads in the range (start, end], returning false if there is no ad available
     * to be placed.
     */
    private boolean tryPlaceAdsInRange(final int start, final int end) {
        int position = start;
        int lastPosition = end - 1;
        while (position <= lastPosition && position != PlacementData.NOT_FOUND) {
            if (position >= mItemCount) {
                break;
            }
            if (mPlacementData.shouldPlaceAd(position)) {
                if (!tryPlaceAd(position)) {
                    return false;
                }
                lastPosition++;
            }
            position = mPlacementData.nextInsertionPosition(position);
        }
        return true;
    }

    /**
     * Attempts to place an ad at the given position, returning false if there is no ad available to
     * be placed.
     */
    private boolean tryPlaceAd(final int position) {
        final NativeResponse adResponse = mAdSource.dequeueAd();
        if (adResponse == null) {
            return false;
        }

        final NativeAdData adData = createAdData(position, adResponse);
        mPlacementData.placeAd(position, adData);
        mItemCount++;

        mAdLoadedListener.onAdLoaded(position);
        return true;
    }

    @NonNull
    private NativeAdData createAdData(final int position, @NonNull final NativeResponse adResponse) {
        Preconditions.checkNotNull(mAdUnitId);
        Preconditions.checkNotNull(mAdRenderer);

        //noinspection ConstantConditions
        return new NativeAdData(mAdUnitId, mAdRenderer, adResponse);
    }

    private void clearNativeResponse(@Nullable final View view) {
        if (view == null) {
            return;
        }
        mImpressionTracker.removeView(view);
        final NativeResponse lastNativeResponse = mNativeResponseMap.get(view);
        if (lastNativeResponse != null) {
            lastNativeResponse.clear(view);
            mNativeResponseMap.remove(view);
            mViewMap.remove(lastNativeResponse);
        }
    }

    private void prepareNativeResponse(@NonNull final NativeResponse nativeResponse, @NonNull final View view) {
        mViewMap.put(nativeResponse, new WeakReference<View>(view));
        mNativeResponseMap.put(view, nativeResponse);
        if (!nativeResponse.isOverridingImpressionTracker()) {
            mImpressionTracker.addView(view, nativeResponse);
        }
        nativeResponse.prepare(view);
    }
}
