package com.rezaduty.chdev.ks.tahririye_man.article;

import com.rezaduty.chdev.ks.tahririye_man.models.FeedItem;

/**
 * Created by Kartik_ch on 12/2/2015.
 */
public interface IArticlePresenter {
    void attemptArticleLoading(String url);

    void archiveArticle(FeedItem feedItem, String article);

    void removeArticle(FeedItem feedItem);
}
