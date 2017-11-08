package com.rezaduty.chdev.ks.tahririye_man.sources;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rezaduty.chdev.ks.tahririye_man.R;
import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;
import com.rezaduty.chdev.ks.tahririye_man.ui.activities.HomeActivity;
import com.rezaduty.chdev.ks.tahririye_man.utils.DatabaseUtil;
import com.rezaduty.chdev.ks.tahririye_man.utils.UrlUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 11/8/2015.
 */
public class SourceInteractor implements ISourceInteractor {
    private Context mContext;
    private OnSourceSavedListener onSourceSavedListener;

    public SourceInteractor(Context mContext) {
        this.mContext = mContext;
    }

    // check url is feed
    public boolean getHtml(String urll, String param) {
        Boolean status;
        status = false;
        try {
            // Build and set timeout values for the request.

            URL url = new URL(urll.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int code = connection.getResponseCode();

            if (code == 200) {
                // Read and store the result line by line then return the entire string.
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                    if (html.toString().contains(param)) {
                        status = true;
                        break;

                    } else {
                        status = false;


                    }
                }
                in.close();
            } else {
                status = false;
            }

        } catch (Exception e) {
            Log.d("not found feed url", e.toString());
        }

        return status;


    }

    public void addSourceToDb(OnSourceSavedListener onSourceSavedListener, SourceItem sourceItem) {
        // main shit happens here
        this.onSourceSavedListener = onSourceSavedListener;

        String regexUrl = UrlUtil.REGEX_URL;

        if (sourceItem.getSourceName().isEmpty() && sourceItem.getSourceUrl().isEmpty() && sourceItem.getSourceCategoryName().isEmpty()) {
            onSourceSavedListener.onFailure("name_url_category_empty");
        } else if (sourceItem.getSourceName().isEmpty()) {
            onSourceSavedListener.onFailure("name_empty");
        } else if (sourceItem.getSourceUrl().isEmpty()) {
            onSourceSavedListener.onFailure("url_empty");
        } else if (sourceItem.getSourceCategoryName().isEmpty()) {
            onSourceSavedListener.onFailure("category_empty");
        } else if (!sourceItem.getSourceUrl().matches(regexUrl)) {
            onSourceSavedListener.onFailure("در حال بررسی");
        } else {

            //Log.e("name", sourceItem.getSourceName());
            //Log.e("url", sourceItem.getSourceUrl());
            //Log.e("category", sourceItem.getSourceCategoryName());
            //Log.e("categoryImgId", String.valueOf(sourceItem.getSourceCategoryImgId()));
            //Log.e("date", sourceItem.getSourceDateAdded());

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            Log.e("RSS", sourceItem.getSourceUrl());

            if (getHtml(sourceItem.getSourceUrl(), "<rss")) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                databaseUtil.saveSourceInDB(sourceItem);

                Toast.makeText(mContext, "با موفقیت ثبت شد" + "\n" + sourceItem.getSourceUrl(),
                        Toast.LENGTH_LONG).show();
                onSourceSavedListener.onSuccess("ذخیره شد");


            } else if (getHtml(sourceItem.getSourceUrl(), "<?xml")) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                databaseUtil.saveSourceInDB(sourceItem);

                Toast.makeText(mContext, "با موفقیت ثبت شد" + "\n" + sourceItem.getSourceUrl(),
                        Toast.LENGTH_LONG).show();
                onSourceSavedListener.onSuccess("ذخیره شد");


            } else {
                Toast.makeText(mContext, "خطایی رخ داده است" + "\n" + sourceItem.getSourceUrl(),
                        Toast.LENGTH_LONG).show();
                onSourceSavedListener.onFailure("در حال بررسی");
            }


        }
    }

    public void getSourcesFromDb(OnSourcesLoadedListener onSourcesLoadedListener) {
        List<String> sourceNames = new ArrayList<>();
        try {
            //default value
            sourceNames.add("تمام منابع");


            List<SourceItem> sourceItems = new DatabaseUtil(mContext).getAllSources();

            for (SourceItem sourceItem : sourceItems) {
                sourceNames.add(sourceItem.getSourceName());

            }

            onSourcesLoadedListener.onSourceLoaded(sourceNames);
        } catch (Exception e) {
            e.printStackTrace();
            onSourcesLoadedListener.onSourceLoadingFailed(e.getMessage());
        }
    }


    public void getSourceItemsFromDb(OnSourcesLoadedListener onSourcesLoadedListener) {
        try {
            List<SourceItem> sourceItems = new DatabaseUtil(mContext).getAllSourceItems();
            onSourcesLoadedListener.onSourceItemsLoaded(sourceItems);
        } catch (Exception e) {
            e.printStackTrace();
            onSourcesLoadedListener.onSourceLoadingFailed(e.getMessage());
        }
    }

    @Override
    public void editSourceItemInDb(OnSourcesModifyListener onSourcesModifyListener, SourceItem sourceItem, String sourceNameOld) {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
            databaseUtil.modifySource(sourceItem, sourceNameOld);
            onSourcesModifyListener.onSourceModified(sourceItem, sourceNameOld);
        } catch (Exception e) {
            e.printStackTrace();
            onSourcesModifyListener.onSourceModifiedFailed(e.getMessage());
        }
    }

    @Override
    public void deleteSourceItemFromDb(OnSourcesModifyListener onSourcesModifyListener, SourceItem sourceItem) {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
            databaseUtil.deleteSourceItem(sourceItem);
            databaseUtil.deleteFeeds(sourceItem);
            onSourcesModifyListener.onSourceDeleted(sourceItem);
        } catch (Exception e) {
            e.printStackTrace();
            onSourcesModifyListener.onSourceDeletionFailed(e.getMessage());
        }
    }
}