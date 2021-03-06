package com.rezaduty.chdev.ks.tahririye_man.sources;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/18/2015.
 */
public interface OnSourcesLoadedListener {
    void onSourceLoaded(List<String> sourceNames);

    void onSourceItemsLoaded(List<SourceItem> sourceItems);

    void onSourceLoadingFailed(String message);
}
