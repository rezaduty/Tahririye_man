package com.rezaduty.chdev.ks.tahririye_man.curatedfeeds;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 1/3/2016.
 */
public interface OnCuratedFeedsRetrievedListener {
    void onSuccess(List<SourceItem> sourceItems);

    void onFailure(String message);
}
