package com.rezaduty.chdev.ks.nta.article;

import android.content.Context;

import com.rezaduty.chdev.ks.nta.models.FeedItem;

/**
 * Created by Kartik_ch on 12/2/2015.
 */
public interface IArticleInteractor {
    void loadArticleAsync(OnArticleLoadedListener onArticleLoadedListener, Context context, String url);

    void articleLoaded(String articleBody);

    void articleLoadingFailed(String message);

    void archiveArticleInDb(OnArticleArchivedListener onArticleArchivedListener, Context context, FeedItem feedItem, String article);

    void deleteArticleInDb(OnArticleRemoveListener onArticleRemoveListener, Context context, FeedItem feedItem);
}
