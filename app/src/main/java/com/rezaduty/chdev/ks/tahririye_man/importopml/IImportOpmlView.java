package com.rezaduty.chdev.ks.tahririye_man.importopml;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 1/9/2016.
 */
public interface IImportOpmlView {
    void opmlFeedsRetrieved(List<SourceItem> sourceItems);

    void opmlFeedsRetrievalFailed(String message);
}
