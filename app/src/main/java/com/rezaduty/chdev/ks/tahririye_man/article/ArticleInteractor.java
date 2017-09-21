package com.rezaduty.chdev.ks.tahririye_man.article;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rezaduty.chdev.ks.tahririye_man.R;
import com.rezaduty.chdev.ks.tahririye_man.models.FeedItem;
import com.rezaduty.chdev.ks.tahririye_man.utils.DatabaseUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kartik_ch on 12/2/2015.
 * Developed by rezaduty on
 */
public class ArticleInteractor implements IArticleInteractor {
    ArticleAsyncLoader mArticleAsyncLoader;
    private OnArticleLoadedListener mOnArticleLoadedListener;
    private MaterialDialog mMaterialDialog;
    public boolean img_status;
    public String desc;
    public void loadArticleAsync(OnArticleLoadedListener onArticleLoadedListener, Context context, String url) {
        this.mOnArticleLoadedListener = onArticleLoadedListener;
        mArticleAsyncLoader = new ArticleAsyncLoader(url);
        mArticleAsyncLoader.execute();
        showLoadingDialog(context);
    }

    private void showLoadingDialog(Context context) {
        mMaterialDialog = new MaterialDialog.Builder(context)
                .titleGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .title(R.string.loading)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (mArticleAsyncLoader != null) {
                            mArticleAsyncLoader.cancel(true);
                        }
                        mOnArticleLoadedListener.onFailure("User performed dismiss action");
                    }
                })
                .build();
        mMaterialDialog.show();

        mMaterialDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mOnArticleLoadedListener.onFailure("User performed dismiss action");
            }
        });
    }

    public void articleLoaded(String articleBody) {
        mMaterialDialog.dismiss();
        mOnArticleLoadedListener.onSuccess("success", articleBody);
    }

    public void articleLoadingFailed(String message) {
        mMaterialDialog.dismiss();
        mOnArticleLoadedListener.onFailure(message);
    }

    //save article in db
    public void archiveArticleInDb(OnArticleArchivedListener onArticleArchivedListener, Context context, FeedItem feedItem, String article) {
        DatabaseUtil databaseUtil = new DatabaseUtil(context);
        try {
            databaseUtil.saveArticle(feedItem, article);
            onArticleArchivedListener.onArticleSaved("success");
        } catch (Exception e) {
            e.printStackTrace();
            onArticleArchivedListener.onArticleSavingFailed(e.getMessage());
        }
    }

    public void deleteArticleInDb(OnArticleRemoveListener onArticleRemoveListener, Context context, FeedItem feedItem) {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil(context);
            databaseUtil.deleteArticle(feedItem);
            databaseUtil.removeDescFromFeed(feedItem);
            onArticleRemoveListener.onArticleDeleted("deleted");
        } catch (Exception e) {
            e.printStackTrace();
            onArticleRemoveListener.onArticleDeletionFailed(e.getMessage());
        }
    }
    public static final Pattern RTL_CHARACTERS =
            Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]");
    protected class ArticleAsyncLoader extends AsyncTask<String, Integer, String> {

        String mUrl, mErrorMsg;
        Elements mParagraphs;

        public ArticleAsyncLoader(String mUrl) {
            try{
                mUrl.replace(" ", "%20");
                int pos = mUrl.lastIndexOf('/') + 1;

                Matcher matcher = RTL_CHARACTERS.matcher(mUrl);
                if(matcher.find()){
                    this.mUrl = mUrl.substring(0, pos) + URLEncoder.encode(mUrl.substring(pos), "UTF-8");
                    Log.d("URL",this.mUrl.toString());
                }else{
                    this.mUrl = mUrl;
                    Log.d("URL",this.mUrl.toString());
                }


            }catch (Exception e){
                Log.d("Error","error");
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                Document htmlDocument = Jsoup.connect(mUrl).get();
                Log.d("URLL",mUrl);
                mParagraphs = htmlDocument.select("p");
                //Log.d("aaaa",mParagraphs.toString());

                if(mUrl.startsWith("http://farsi.khamenei.ir")){
                    Log.d("salam",htmlDocument.body().toString());
                    desc = htmlDocument.getAllElements().select("meta[property=og:description]").attr("content").toString();
                    if(desc ==""){
                        desc = "برای اطلاعات بیشتر بر روی نشان کره ضربه بزنید";
                    }
                }
                if (htmlDocument.select("div[class=body]")!=null){
                    Elements metalinks = htmlDocument.select("div[class=body]");

                    try {
                        Log.d("desccccc",metalinks.first().text().toString());
                        desc = metalinks.first().text().toString();
                        //mParagraphs.append(metalinks.first().attr("content"));
                    }catch (Exception e){

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
                return "failure";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                String articleBody = getArticleBody(mParagraphs);
                articleLoaded(articleBody);
            } else if (s.equals("failure")) {
                articleLoadingFailed(mErrorMsg);
            }
            super.onPostExecute(s);
        }

        private String getArticleBody(Elements paragraphs) {
            img_status = false;
            String body = "";
            for (Element paragraph : paragraphs) {
                String para = paragraph.toString().trim();
                Log.d("before",para);
                if (!para.isEmpty()) {

                    // check img tag in string for margin of text
                    // replace for bayanbox format
                    if(!para.contains("<img src=\"//analytics") || !para.contains("<p><img src=\"//analytics")){
                        if(para.contains("<img")) {


                                img_status = true;
                                Log.d("IMG",para);

                                if(para.contains("//b")){
                                    para = para.replaceAll("//","http://");

                                    Log.d("IMG_AFTER_REPLACE",para);
                                }

                                if(para.startsWith("//")){
                                    para = para.replaceAll("//","http://");

                                    Log.d("IMG_AFTER_REPLACE",para);
                                }else if(para.contains("ndata/news")){
                                    para = para.replaceAll("ndata/news","http://farsi.khamenei.ir/ndata/news");

                                    Log.d("IMG_AFTER_REPLACE",para);
                                }else if(para.contains("/files/fa")){
                                    Log.d("okok","ok");
                                    para = para.replaceAll("/files/fa","http://www.mizanonline.ir/files/fa");
                                }
                                body += "<br>" + para + "<br><br><br><br><br><br><br><br>";
                            }

                        else if (img_status) {

                            body += "<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>" + para + "\n\n";
                            img_status = false;
                        }else {
                            body += para + "\n\n";
                        }
                    }else{
                        body += "<br>" + para + "\n\n";
                    }


                }

            }

            if (body.length() != 0) {
                //
                Log.d("aaaa", String.valueOf(body.length()));
                if(body.length()<=93){
                    body = " لطفا برای اطلاعات بیشتر کره را لمس کنید";
                }
                return body.substring(0, body.length() - 1);
            } else {
                //body = desc;
                return desc;

            }
        }
    }
}
