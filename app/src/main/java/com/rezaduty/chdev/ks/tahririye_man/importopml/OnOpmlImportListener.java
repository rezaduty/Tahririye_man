package com.rezaduty.chdev.ks.tahririye_man.importopml;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 1/9/2016.
 */
public interface OnOpmlImportListener {
    void onSuccess(List<SourceItem> sourceItems);

    void onFailure(String message);
}
