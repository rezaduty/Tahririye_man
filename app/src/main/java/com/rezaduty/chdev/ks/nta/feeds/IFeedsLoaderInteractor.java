package com.rezaduty.chdev.ks.nta.feeds;

import com.rezaduty.chdev.ks.nta.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/5/2015.
 */
public interface IFeedsLoaderInteractor {
    void loadFeedsFromDb(final OnFeedsLoadedListener onFeedsLoadedListener);

    void loadFeedsFromDbBySource(final OnFeedsLoadedListener onFeedsLoadedListener, String source);

    void loadFeedsAsync(OnFeedsLoadedListener onFeedsLoadedListener, List<SourceItem> sourceItems);
}
