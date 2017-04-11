package com.rezaduty.chdev.ks.nta.feeds;

import com.rezaduty.chdev.ks.nta.models.FeedItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/5/2015.
 */
public interface OnFeedsLoadedListener {
    void onSuccess(List<FeedItem> feedItems, boolean loadedNewFeeds);

    void onFailure(String message);
}
