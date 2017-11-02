package com.rezaduty.chdev.ks.rssmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 11/15/2015.
 * Developed by rezaduty on 2017-09-12
 */
public class RssReader implements OnFeedLoadListener {
    private String[] mUrlList, mSourceList, mCategories;
    private int[] mCategoryImgIds;
    private Context mContext;
    private List<RssItem> mRssItems = new ArrayList<>();
    private RssParser mRssParser;
    private int mPosition = 0;
    private MaterialDialog mMaterialDialog;
    private OnRssLoadListener mOnRssLoadListener;
    private int material = Color.parseColor("#616161");
    public RssReader(Context context, String[] urlList, String[] sourceList, String[] categories, int[] categoryImgIds, OnRssLoadListener onRssLoadListener) {
        this.mContext = context;
        this.mUrlList = urlList;
        this.mSourceList = sourceList;
        this.mCategories = categories;
        this.mCategoryImgIds = categoryImgIds;
        this.mOnRssLoadListener = onRssLoadListener;
    }

    public void readRssFeeds() {

        mMaterialDialog = new MaterialDialog.Builder(mContext)
                .backgroundColor(material)
                    .titleGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .title(R.string.loading_feeds)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (mRssParser != null) {
                            mRssParser.cancel(true);
                        }
                        mOnRssLoadListener.onFailure("User performed dismiss action");
                    }
                })
                .build();
        mMaterialDialog.show();

        mMaterialDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mRssParser != null) {
                    mRssParser.cancel(true);
                }
                mOnRssLoadListener.onFailure("User performed dismiss action");
            }
        });

        if (mRssItems != null) {
            mRssItems.clear();
        }
        parseRss(0);
    }

    private void parseRss(int position) {
        if (position != mUrlList.length) {
            mRssParser = new RssParser(mUrlList[position], this);
            mRssParser.execute();
            String source = getWebsiteName(mUrlList[position]);
            mMaterialDialog.setContent(source);
        } else {
            mMaterialDialog.dismiss();
            mOnRssLoadListener.onSuccess(mRssItems);
        }
    }

    private String getWebsiteName(String url) {
        URI uri;
        try {
            uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onSuccess(Elements items) {
        for (Element item : items) {
            mRssItems.add(getRssItem(item));
        }
        mPosition++;
        parseRss(mPosition);
    }

    @Override
    public void onFailure(String message) {
        mOnRssLoadListener.onFailure(message);
    }

    private RssItem getRssItem(Element element) {
        String title,pubDate,description,link,sourceName,sourceUrl;
        title="";
        description = "";
        link="";
        sourceName="";
        sourceUrl="";
        pubDate = "";
        RssItem rssItem = new RssItem();
        try {
             title = element.select("title").first().text();
            if(!element.select("link").first().text().isEmpty()){
                link = element.select("link").first().text();

            }else{
                link = "http://www.tabnak.ir/fa/news/731286/پیوند-عجیب-استخوان-با-پرینتر-سه-بعدی";
            }

            if(!element.select("description").first().text().isEmpty() || element.select("description").first().text().length() <=6){
                description = element.select("description").first().text();
                Log.d("axc",description);

            }else{
                Log.d("axxc",description);
                description = "برای اطلاعات بیشتر روی علامت کرده ضربه بزنید";
            }





            if(!mSourceList[mPosition].isEmpty()){
                sourceName = mSourceList[mPosition];

            }else{
                sourceName = "google";
            }
            if(!getWebsiteName(mUrlList[mPosition]).isEmpty()){
                sourceUrl = getWebsiteName(mUrlList[mPosition]);
            }else{
                sourceUrl = "http://www.tabnak.ir/fa/news/731286/پیوند-عجیب-استخوان-با-پرینتر-سه-بعدی";

            }



        }catch (Exception e){

            Log.e("Exception", "Try Again: " + title.toString());
            Log.e("Exception", "Try Again: " + description.toString());
            Log.e("Exception", "Try Again: " + link.toString());
            Log.e("Exception", "Try Again: " + sourceName.toString());

            Log.e("Exception", "Try Again: " + sourceUrl.toString());

            Log.e("Exception", "Try Again: " + e.toString());
        }
        String imageUrl;
        if (!element.select("media|thumbnail").isEmpty()) {
            imageUrl = element.select("media|thumbnail").attr("url");
        } else if (!element.select("media|content").isEmpty()) {
            imageUrl = element.select("media|content").attr("url");
        } else if (!element.select("image").isEmpty()) {
            imageUrl = element.select("image").attr("url");
        } else {
            imageUrl="";
            Document document =Jsoup.parse(element.select("description").text());
            Elements imgs = document.select("img");
            for (Element img : imgs) {
                if (img.hasAttr("src")) {
                    imageUrl = img.attr("src");
                }
                else {
                    imageUrl = null;
                }
            }

        }

        String category = null;
        if (mCategories == null) {
            if (!element.select("category").isEmpty()) {
                category = element.select("category").first().text();
            }
        } else {
            category = mCategories[mPosition];
        }


        if (element.select("pubDate").toString() !="") {
            pubDate = element.select("pubDate").first().text();
            //Log.d("pubDate",pubDate);
        }


        int categoryImgId = mCategoryImgIds[mPosition];



        rssItem.setTitle(title);

        //Log.d("description",description);
        rssItem.setLink(link);
        rssItem.setSourceName(sourceName);
        rssItem.setDescription(description);
        rssItem.setSourceUrl(sourceUrl);
        rssItem.setImageUrl(imageUrl);
        rssItem.setCategory(category);
        rssItem.setPubDate(pubDate);
        rssItem.setCategoryImgId(categoryImgId);

        return rssItem;
    }
}
