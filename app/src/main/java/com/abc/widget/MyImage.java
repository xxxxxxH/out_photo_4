package com.abc.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

public class MyImage {
    public static Bitmap NewImage(int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bm.eraseColor(0);
        return bm;
    }

    public static Bitmap NewImage(Bitmap srcImg, int color, boolean transparent) {
        return NewImage(srcImg.getWidth(), srcImg.getHeight(), color, transparent);
    }

    public static Bitmap NewImage(int width, int height, int color, boolean transparent) {
        if (transparent) {
            return NewImage(width, height);
        }
        Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bm.eraseColor(color);
        return bm;
    }


    public static Bitmap Clone(Bitmap srcImg, int x, int y, int width, int height) {
        return Clone(srcImg, new Rect (x, y, x + width, y + height));
    }

    public static Bitmap Clone(Bitmap srcImg, Rect rect) {
        if (srcImg == null) {
            return null;
        }
        try {
            Rect intersection = Intersection(srcImg, rect);
            if (intersection != null) {
                Bitmap bm = NewImage(intersection.width(), intersection.height());
                new Canvas (bm).drawBitmap(srcImg, (float) (-intersection.left), (float) (-intersection.top), null);
                return bm;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Rect Intersection(Bitmap srcImg, Rect rect) {
        if (srcImg == null) {
            return null;
        }
        Rect intersection = new Rect ();
        if (intersection.setIntersect(rect, new Rect (0, 0, srcImg.getWidth(), srcImg.getHeight()))) {
            return intersection;
        }
        return null;
    }

}
