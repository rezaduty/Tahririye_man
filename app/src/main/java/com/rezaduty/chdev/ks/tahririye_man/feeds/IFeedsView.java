package com.rezaduty.chdev.ks.tahririye_man.feeds;

import com.rezaduty.chdev.ks.tahririye_man.models.FeedItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/4/2015.
 */
public interface IFeedsView {
    void feedsLoaded(List<FeedItem> feedItems);

    void loadingFailed(String message);
}
