package com.rezaduty.chdev.ks.tahririye_man.sources;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

/**
 * Created by Kartik_ch on 12/12/2015.
 */
public interface OnSourcesModifyListener {
    void onSourceModified(SourceItem sourceItem, String oldName);

    void onSourceModifiedFailed(String message);

    void onSourceDeleted(SourceItem sourceItem);

    void onSourceDeletionFailed(String message);
}
