package com.rezaduty.chdev.ks.tahririye_man.sources;

import android.content.Context;

import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;

/**
 * Created by Kartik_ch on 11/7/2015.
 */
public interface ISourcePresenter {
    void addSource(SourceItem sourceItem);

    void getSources();

    void getSourceItems();

    void modifySources(Context context, SourceItem sourceItem);

    void deleteSource(SourceItem sourceItem);
}
