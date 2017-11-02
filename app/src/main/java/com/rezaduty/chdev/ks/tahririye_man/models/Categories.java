package com.rezaduty.chdev.ks.tahririye_man.models;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;

import com.rezaduty.chdev.ks.tahririye_man.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 12/13/2015.
 */
public class Categories {
    private Context mContext;

    public Categories(Context context) {
        this.mContext = context;
    }

    public List<CategoryItem> getCategoryItems() {
        List<CategoryItem> categoryItems = new ArrayList<>();

        String[] categoryNames = mContext.getResources().getStringArray(R.array.category_names);
        TypedArray categoryImgs = mContext.getResources().obtainTypedArray(R.array.category_drawables);

        for (int i = 0; i < categoryNames.length; i++) {
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setCategoryName(categoryNames[i]);
            categoryItem.setCategoryImg(ContextCompat.getDrawable(mContext, categoryImgs.getResourceId(i, -1)));
            categoryItems.add(categoryItem);
        }

        return categoryItems;
    }

    public int getDrawableId(String category) {
        switch (category) {
            case "خبری":
                return R.drawable.icons8_news;
            case "ورزشی":
                return R.drawable.icons8_gymnastics;
            case "بازی":
                return R.drawable.icons8_steam;
            case "تکنولوژی":
                return R.drawable.fiber_new_black_24x24;
            case "آموزشی":
                return R.drawable.icons8_e_learning;
            case "نرم افزار":
                return R.drawable.code_black_24x24;
            case "سخت افزار":
                return R.drawable.icons8_processor;
            case "هک و امنیت":
                return R.drawable.icons8_forgot_password;
            case "رویداد ها":
                return R.drawable.icons8_event;
            case "شخصیت ها":
                return R.drawable.icons8_person;
            case "متفرقه":
                return R.drawable.icons8_questionmark_24;
            default:
                return 0;
        }
    }
}
