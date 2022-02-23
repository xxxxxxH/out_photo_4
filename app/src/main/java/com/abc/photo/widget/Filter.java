package com.abc.photo.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public  class Filter {

    public boolean hidePalette = false;
    protected Random rand = new Random ();
    public EffectType effectType = EffectType.None;
    public BoolParameter[] boolPar = new BoolParameter[20];
    public ColorParameter[] colorPalette = null;
    public ColorParameter[] colorPar = new ColorParameter[10];
    public IntParameter[] intPar = new IntParameter[20];
    public ListParameter[] listPar = new ListParameter[2];

    public enum EffectType {
        Shatter, None, Correction, Cube, MapCube1
    }

    public void RandomValues(boolean mainActivity) {
        int i = 0;
        while (i < this.intPar.length) {
            if (!(this.intPar[i] == null || this.intPar[i].isThreshold())) {
                setRandomInt(i);
            }
            i++;
        }
        for (i = 0; i < this.boolPar.length; i++) {
            if (this.boolPar[i] != null) {
                this.boolPar[i].value = this.rand.nextBoolean();
            }
        }
        for (i = 0; i < this.colorPar.length; i++) {
            if (this.colorPar[i] != null) {
                this.colorPar[i].setValue(PMS.RandomColor(this.rand));
            }
        }
        for (i = 0; i < this.listPar.length; i++) {
            if (this.listPar[i] != null) {
                setRandomList(i);
            }
        }
        if (this.colorPalette != null && !this.hidePalette) {
            for (ColorParameter value : this.colorPalette) {
                value.setValue(PMS.RandomColor(this.rand));
            }
        }
    }

    protected void setRandomInt(int i) {
        if (this.intPar[i] != null) {
            int min = this.intPar[i].min;
            this.intPar[i].setValue(this.rand.nextInt((this.intPar[i].max - min) + 1) + min);
        }
    }

    protected void setRandomInt(int i, int from, int toInclusive) {
        if (toInclusive > from && this.intPar[i] != null) {
            this.intPar[i].setValue(this.rand.nextInt((toInclusive - from) + 1) + from);
        }
    }

    protected void setRandomList(int i) {
        if (this.listPar[i] != null) {
            int min = this.listPar[i].min;
            this.listPar[i].setValue(this.rand.nextInt((this.listPar[i].max - min) + 1) + min);
        }
    }

    protected void copyAlpha(Bitmap srcImg, Bitmap dstImg) {
        try {
            if (srcImg.hasAlpha()) {
                int width = srcImg.getWidth();
                int height = srcImg.getHeight();
                if (width == dstImg.getWidth() && height == dstImg.getHeight()) {
                    int[] dstPix = new int[(width * height)];
                    dstImg.getPixels(dstPix, 0, width, 0, 0, width, height);
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int alpha = ViewCompat.MEASURED_STATE_MASK & srcImg.getPixel(x, y);
                            if (alpha < 255) {
                                dstPix[(y * width) + x] = (ViewCompat.MEASURED_SIZE_MASK & dstPix[(y * width) + x]) | alpha;
                            }
                        }
                    }
                    dstImg.setPixels(dstPix, 0, width, 0, 0, width, height);
                }
            }
        } catch (Exception e) {
        }
    }

    protected RectF getRectF(float x, float y, float w, float h) {
        return new RectF (x, y, x + w, y + h);
    }

    protected boolean random(int percentage) {
        return this.rand.nextInt(100) < percentage;
    }

    public int[] Apply(int[] pix, int width, int height) {
        return null;
    }

    public Bitmap Apply(Bitmap srcImg) {
        if (srcImg == null) {
            return null;
        }
        try {
            int width = srcImg.getWidth();
            int height = srcImg.getHeight();
            int[] pix = new int[(width * height)];
            srcImg.getPixels(pix, 0, width, 0, 0, width, height);
            int[] dstPix = Apply(pix, width, height);
            Bitmap dstImg;
            if (dstPix == null) {
                dstImg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                try {
                    dstImg.setPixels(pix, 0, width, 0, 0, width, height);
                    return dstImg;
                } catch (Exception e) {
                    return dstImg;
                }
            } else if (dstPix.length != 1 || width * height <= 1) {
                dstImg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                dstImg.setPixels(dstPix, 0, width, 0, 0, width, height);
                return dstImg;
            } else {
                dstImg = Clone(srcImg);
                Bitmap bm = Bitmap.createBitmap(pix, width, height, Config.ARGB_8888);
                Paint paint = new Paint ();
                paint.setAlpha((dstPix[0] * 255) / 100);
                new Canvas (dstImg).drawBitmap(bm, 0.0f, 0.0f, paint);
                bm.recycle();
                return dstImg;
            }
        } catch (Exception e2) {
            return null;
        }
    }
    public static Bitmap Clone(Bitmap srcImg) {
        Bitmap bitmap = null;
        if (srcImg != null) {
            try {
                bitmap = srcImg.copy(Config.ARGB_8888, true);
            } catch (Exception e) {
                try {
                    bitmap = NewImage(srcImg);
                } catch (Exception e2) {
                }
            }
        }
        return bitmap;
    }

    public static Bitmap NewImage(Bitmap srcImg) {
        if (srcImg == null) {
            return null;
        }
        return NewImage(srcImg.getWidth(), srcImg.getHeight());
    }

     public static Bitmap NewImage(int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bm.eraseColor(0);
        return bm;
    }

    protected int ClampBilinear(int[] srcPix, int width, int height, float x, float y, int defaultColor) {
        if (x < 0.0f || y < 0.0f || x >= ((float) width) || y >= ((float) height)) {
            return defaultColor;
        }
        int oy2;
        int ox1 = (int) x;
        int oy1 = (int) y;
        int ox2 = ox1 == width + -1 ? ox1 : ox1 + 1;
        if (oy1 == height - 1) {
            oy2 = oy1;
        } else {
            oy2 = oy1 + 1;
        }
        float dx2 = x - ((float) ox1);
        if (dx2 < 0.0f) {
            dx2 = 0.0f;
        }
        float dx1 = 1.0f - dx2;
        float dy2 = y - ((float) oy1);
        if (dy2 < 0.0f) {
            dy2 = 0.0f;
        }
        float dy1 = 1.0f - dy2;
        int p1 = srcPix[(oy1 * width) + ox1];
        int p2 = srcPix[(oy1 * width) + ox2];
        int p3 = srcPix[(oy2 * width) + ox1];
        int p4 = srcPix[(oy2 * width) + ox2];
        return (((ViewCompat.MEASURED_STATE_MASK & (((int) ((((((float) ((p1 >> 24) & 255)) * dx1) + (((float) ((p2 >> 24) & 255)) * dx2)) * dy1) + (((((float) ((p3 >> 24) & 255)) * dx1) + (((float) ((p4 >> 24) & 255)) * dx2)) * dy2))) << 24)) | (16711680 & (((int) ((((((float) ((p1 >> 16) & 255)) * dx1) + (((float) ((p2 >> 16) & 255)) * dx2)) * dy1) + (((((float) ((p3 >> 16) & 255)) * dx1) + (((float) ((p4 >> 16) & 255)) * dx2)) * dy2))) << 16))) | (MotionEventCompat.ACTION_POINTER_INDEX_MASK & (((int) ((((((float) ((p1 >> 8) & 255)) * dx1) + (((float) ((p2 >> 8) & 255)) * dx2)) * dy1) + (((((float) ((p3 >> 8) & 255)) * dx1) + (((float) ((p4 >> 8) & 255)) * dx2)) * dy2))) << 8))) | (((int) ((((((float) (p1 & 255)) * dx1) + (((float) (p2 & 255)) * dx2)) * dy1) + (((((float) (p3 & 255)) * dx1) + (((float) (p4 & 255)) * dx2)) * dy2))) & 255);
    }

    public static class BoolParameter extends FilterParameter{
        public boolean value = false;
        public BoolParameter(String name, Boolean val) {
            super(name);
            this.value = val;
        }
    }

    public class TransparentParameter extends BoolParameter {
        public TransparentParameter(boolean val) {
            super("Transparent", val);
        }
    }

    public static class ColorParameter extends FilterParameter{
        private int value = 0;
        public ColorParameter(String name, int val) {
            super(name);
            this.value = -1;
            this.value = val;
        }
        public int getValue() {
            return this.value;
        }

        public void setValue(int val) {
            this.value = val;
        }

    }

    public static class BackColorParameter extends ColorParameter {
        public BackColorParameter() {
            this(-1);
        }

        public BackColorParameter(int val) {
            super("Background", val);
        }
    }

    public static class IntParameter extends FilterParameter{
        public int max = 100;
        public int min = 0;
        protected boolean threshold = false;
        private int value = 0;

        public IntParameter(String name, String unit, int val, int min, int max) {
            super(name, unit);
            this.min = min;
            this.max = max;
            setValue(val);
        }

        public IntParameter(String name, int val, int min, int max) {
            super(name);
            this.min = min;
            this.max = max;
            setValue(val);
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int val) {
            if (val < this.min) {
                val = this.min;
            } else if (val > this.max) {
                val = this.max;
            }
            this.value = val;
        }

        public boolean isThreshold() {
            return this.threshold;
        }
    }

    private class ListParameter extends FilterParameter{
        public int max = 0;
        public int min = 0;
        private int value = 0;
        public int getValue() {
            return this.value;
        }

        public void setValue(int val) {
            if (val < this.min) {
                val = this.min;
            } else if (val > this.max) {
                val = this.max;
            }
            this.value = val;
        }
    }

    public static class FilterParameter {
        protected String description = "";
        protected String name = "";
        protected String unit = "";

        public FilterParameter(String name) {
            this.name = name;
        }

        public FilterParameter(String name, String unit) {
            this.name = name;
            this.unit = unit;
        }

        public FilterParameter() { }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getUnit() {
            return this.unit;
        }
    }

    public static class PMS {
        private static List<Integer> colors = new ArrayList ();
        private static List<Integer> darkColors = new ArrayList ();

        public static int RandomColor(Random rand) {
            return RandomColor(rand, false);
        }

        public static int RandomColor(Random rand, boolean notBlack) {
            int color = ViewCompat.MEASURED_STATE_MASK;
            try {
                //InitColors();
                int i = 0;
                boolean isBlack = true;
                while (true) {
                    if (i == 0 || (notBlack && isBlack)) {
                        color = (Integer) colors.get(rand.nextInt(colors.size()));
                        isBlack = Color.red(color) == 0 && Color.green(color) == 0 && Color.blue(color) == 0;
                        i++;
                    }
                }
            } catch (Exception e) {
            }
            return color;
        }

        public static int GetDarkColor(Random rand) {
            //InitColors();
            return (Integer) darkColors.get(rand.nextInt(darkColors.size()));
        }
    }
}

