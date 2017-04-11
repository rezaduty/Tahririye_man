package com.rezaduty.chdev.ks.nta.importopml;

import com.rezaduty.chdev.ks.nta.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 1/9/2016.
 */
public interface OnOpmlImportListener {
    void onSuccess(List<SourceItem> sourceItems);

    void onFailure(String message);
}
