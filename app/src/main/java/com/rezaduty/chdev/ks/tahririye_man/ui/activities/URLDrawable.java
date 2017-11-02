package com.rezaduty.chdev.ks.tahririye_man.ui.activities;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class URLDrawable extends BitmapDrawable {
    // the drawable that you need to set, you could set the initial drawing
    // with the loading image if you need to
    protected Drawable drawable;


    public static int getScreenWidth() {
        Log.d("adas",String.valueOf(Resources.getSystem().getDisplayMetrics().widthPixels));
        return Resources.getSystem().getDisplayMetrics().widthPixels;

    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
        if(drawable != null) {
            //Log.d("image found",String.valueOf(canvas.getWidth()));

            drawable.setBounds(0,0,getScreenWidth(),getScreenHeight()/3);
            drawable.draw(canvas);
        }
    }
}