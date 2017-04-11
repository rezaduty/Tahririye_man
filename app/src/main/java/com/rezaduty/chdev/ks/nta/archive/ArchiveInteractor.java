package com.rezaduty.chdev.ks.nta.archive;

import android.content.Context;

import com.rezaduty.chdev.ks.nta.models.FeedItem;
import com.rezaduty.chdev.ks.nta.utils.DatabaseUtil;

import java.util.List;

/**
 * Created by Kartik_ch on 12/9/2015.
 */
public class ArchiveInteractor implements IArchiveInteractor {
    public void retrieveArchiveFromDb(OnArticleRetrievedListener onArticleRetrievedListener, Context context) {
        DatabaseUtil databaseUtil = new DatabaseUtil(context);
        try {
            List<FeedItem> feedItems = databaseUtil.getSavedArticles();
            onArticleRetrievedListener.onSuccess(feedItems);
        } catch (Exception e) {
            e.printStackTrace();
            onArticleRetrievedListener.onFailure(e.getMessage());
        }
    }
}
