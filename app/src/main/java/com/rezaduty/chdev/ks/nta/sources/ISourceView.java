package com.rezaduty.chdev.ks.nta.sources;

import com.rezaduty.chdev.ks.nta.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/8/2015.
 */
public interface ISourceView {
    void dataSourceSaved(String message);

    void dataSourceSaveFailed(String message);

    void dataSourceLoaded(List<String> sourceNames);

    void dataSourceItemsLoaded(List<SourceItem> sourceItems);

    void dataSourceLoadingFailed(String message);

    void sourceItemModified(SourceItem sourceItem, String oldName);

    void sourceItemModificationFailed(String message);

    void sourceItemDeleted(SourceItem sourceItem);

    void sourceItemDeletionFailed(String message);
}
