package com.abc.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import java.lang.reflect.Array;

public class Shatter extends Filter {
    private Cube cube;
    private Correction Correction1;
    private Correction Correction2;
    private static int _maxSize = 0;

    public Shatter() {
        this.Correction1 = new Correction(120);
        this.Correction2 = new Correction(60);
        this.cube = new Cube();
        this.effectType = EffectType.Shatter;
        this.intPar[0] = new IntParameter("Count", 20, 2, 100);
        this.intPar[1] = new IntParameter("X", "%", 40, 0, 100);
        this.boolPar[0] = new BoolParameter("Rotate Blocks", Boolean.TRUE);
        this.boolPar[1] = new BoolParameter("Shattered Blocks", Boolean.TRUE);
        this.colorPar[0] = new BackColorParameter(ViewCompat.MEASURED_STATE_MASK);
        this.cube.boolPar[0].value = true;
    }

    public void RandomValues(boolean main) {
        super.RandomValues(main);
        setRandomInt(0, 10, 26);
        setRandomInt(1, 40, 60);
        this.boolPar[0].value = random(75);
        this.boolPar[1].value = random(66);
        ColorParameter colorParameter = this.colorPar[0];
        int GetDarkColor = random(66) ? ViewCompat.MEASURED_STATE_MASK : this.rand.nextBoolean() ? -1 : PMS.GetDarkColor(this.rand);
        colorParameter.setValue(GetDarkColor);
    }

    private Paint getTexturePaint(Bitmap img) {
        Paint paint = new Paint ();
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        setShader(paint, img);
        return paint;
    }

    private void setShader(Paint paint, Bitmap img) {
        paint.setShader(new BitmapShader (img, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
    }

    public Bitmap Apply(Bitmap img) {
        if (img == null) {
            return null;
        }
        Bitmap dstImg = null;
        try {
            int i;
            int j;
            int width = img.getWidth();
            int height = img.getHeight();
            dstImg = Filter.Clone(img);
            Canvas canvas = new Canvas (dstImg);
            int countx = this.intPar[0].getValue();
            float w = Math.max(8.0f, ((float) width) / ((float) countx));
            int d = (int) Math.max(2.0f, w / 14.0f);
            //int d2 = d / 2;
            int county = ((int) (((float) height) / w)) + 1;
            countx++;
            Bitmap gammaImg = this.Correction1.Apply(img);
            Paint texturePaint = getTexturePaint(gammaImg);
            Paint blackPaint = new Paint ();
            blackPaint.setStyle(Style.FILL);
            blackPaint.setColor(this.colorPar[0].getValue());
            for (i = 0; i < countx; i++) {
                canvas.drawRect(getRectF(((float) i) * w, 0.0f, (float) d, (float) height), texturePaint);
                canvas.drawRect(getRectF(0.0f, ((float) i) * w, (float) width, (float) d), texturePaint);
            }
            gammaImg.recycle();
            gammaImg = this.Correction2.Apply(img);
            texturePaint = getTexturePaint(gammaImg);
            boolean[][] drawBlock = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{countx, county});
            boolean[][] blackHole = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{countx, county});
            int x1 = (this.intPar[1].getValue() * countx) / 100;
            int x2 = countx - x1;
            boolean rotate = this.boolPar[0].value;
            boolean shatter = this.boolPar[1].value;
            i = 0;
            while (i < countx) {
                j = 0;
                while (j < county) {
                    drawBlock[i][j] = false;
                    blackHole[i][j] = false;
                    if (i > x1) {
                        if ((i >= ((x2 * 3) / 4) + x1 && random(50)) || ((i >= ((x2 * 2) / 4) + x1 && random(40)) || ((i >= ((x2) / 4) + x1 && random(30)) || random(20)))) {
                            canvas.drawRect(getRectF((((float) i) * w) + ((float) d), (((float) j) * w) + ((float) d), w - ((float) d), w - ((float) d)), blackPaint);
                            blackHole[i][j] = true;
                        } else if (i > 0 && !drawBlock[i - 1][j] && j > 0 && !drawBlock[i][j - 1] && j < county - 1 && !drawBlock[i][j + 1] && shatter && (random(80) || i >= ((x2 * 2) / 4) + x1)) {
                            canvas.drawRect(getRectF((((float) i) * w) + ((float) d), (((float) j) * w) + ((float) d), w - ((float) d), w - ((float) d)), blackPaint);
                            blackHole[i][j] = true;
                            drawBlock[i][j] = true;
                        }
                    } else if (i >= (x1 * 3) / 4 && random(25)) {
                        canvas.drawRect(getRectF((((float) i) * w) + ((float) d), (((float) j) * w) + ((float) d), w - ((float) d), w - ((float) d)), blackPaint);
                        blackHole[i][j] = true;
                    } else if ((i >= (x1 * 3) / 4 && random(20)) || ((i >= (x1 * 2) / 4 && random(15)) || ((i >= (x1) / 4 && random(10)) || random(5)))) {
                        canvas.drawRect(getRectF((((float) i) * w) + ((float) d), (((float) j) * w) + ((float) d), w - ((float) d), w - ((float) d)), texturePaint);
                    }
                    j++;
                }
                i++;
            }
            i = 0;
            while (i < countx) {
                j = 0;
                while (j < county) {
                    if (blackHole[i][j]) {
                        if (i == 0 && j == 0) {
                            canvas.drawRect(getRectF(0.0f, 0.0f, (float) d, (float) d), blackPaint);
                        }
                        if (i == 0) {
                            canvas.drawRect(getRectF(0.0f, (((float) j) * w) + ((float) d), (float) d, w - ((float) d)), blackPaint);
                        }
                        if (i == 0 && j < county - 1 && blackHole[i][j + 1]) {
                            canvas.drawRect(getRectF(0.0f, (((float) j) * w) + w, (float) d, (float) d), blackPaint);
                        }
                        if (j == 0) {
                            canvas.drawRect(getRectF((((float) i) * w) + ((float) d), 0.0f, w - ((float) d), (float) d), blackPaint);
                        }
                        if (j == 0 && i < countx - 1 && blackHole[i + 1][j]) {
                            canvas.drawRect(getRectF((((float) i) * w) + w, 0.0f, (float) d, (float) d), blackPaint);
                        }
                        if (i < countx - 1 && blackHole[i + 1][j]) {
                            canvas.drawRect(getRectF((((float) i) * w) + w, (((float) j) * w) + ((float) d), (float) d, w - ((float) d)), blackPaint);
                        }
                        if (j < county - 1 && blackHole[i][j + 1]) {
                            canvas.drawRect(getRectF((((float) i) * w) + ((float) d), (((float) j) * w) + w, w - ((float) d), (float) d), blackPaint);
                        }
                        if (i < countx - 1 && j < county - 1 && blackHole[i + 1][j] && blackHole[i][j + 1] && blackHole[i + 1][j + 1]) {
                            canvas.drawRect(getRectF((((float) i) * w) + w, (((float) j) * w) + w, (float) d, (float) d), blackPaint);
                        }
                    }
                    j++;
                }
                i++;
            }
            Paint paint = new Paint ();
            if (shatter) {
                if (rotate) {
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                }
                for (i = 0; i < countx; i++) {
                    for (j = 0; j < county; j++) {
                        if (drawBlock[i][j]) {
                            int cubesize;
                            try {
                                if (random(50)) {
                                    cubesize = (int) (w / (1.0f + (((float) this.rand.nextInt(34)) / 100.0f)));
                                } else if (random(50)) {
                                    cubesize = (int) ((1.0f + (((float) this.rand.nextInt(20)) / 100.0f)) * w);
                                } else if (random(50)) {
                                    cubesize = (int) ((1.0f + (((float) this.rand.nextInt(40)) / 100.0f)) * w);
                                } else {
                                    cubesize = (int) ((1.0f + (((float) this.rand.nextInt(80)) / 100.0f)) * w);
                                }
                                Bitmap bm = MyImage.Clone(img, (int) (((float) i) * w), (int) (((float) j) * w), cubesize, cubesize);
                                if (bm != null) {
                                    float x = (((float) i) * w) + ((float) this.rand.nextInt((int) w));
                                    float y = (((float) j) * w) + ((float) this.rand.nextInt((int) w));
                                    if (random(5)) {
                                        x = (((float) this.rand.nextInt(i)) * w) + ((float) this.rand.nextInt((int) w));
                                        y = (((float) this.rand.nextInt(j)) * w) + ((float) this.rand.nextInt((int) w));
                                    }
                                    Bitmap bm2 = this.cube.Apply(bm);
                                    bm.recycle();
                                    bm = bm2;
                                    if (rotate) {
                                        float angle = (float) (this.rand.nextInt(41) - 20);
                                        canvas.rotate(angle);
                                        canvas.drawBitmap(bm, x, y, paint);
                                        canvas.rotate(-angle);
                                        bm.recycle();
                                    } else {
                                        canvas.drawBitmap(bm, x, y, paint);
                                        bm.recycle();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            gammaImg.recycle();
            copyAlpha(img, dstImg);
            return dstImg;
        } catch (Exception e2) {
            return dstImg;
        }
    }

    public class Correction extends Filter{
        /*public Correction() {
            Init(100);
        }*/

        public Correction(int gamma) {
            Init(gamma);
        }

        private void Init(int gamma) {
            this.effectType = EffectType.Correction;
            this.intPar[0] = new IntParameter("Gamma", gamma, 10, 999);
        }

        public int[] Apply(int[] pix, int width, int height) {
            try {
                int gamma = this.intPar[0].getValue();
                if (gamma == 100) {
                    return null;
                }
                int i;
                int[] table = new int[256];
                double gam = 1.0d / (((double) gamma) / 100.0d);
                for (i = 0; i < 256; i++) {
                    table[i] = Math.min(255, (int) ((Math.pow(((double) i) / 255.0d, gam) * 255.0d) + 0.5d));
                }
                for (i = 0; i < pix.length; i++) {
                    int p = pix[i];
                    int g = (p >> 8) & 255;
                    int b = p & 255;
                    pix[i] = (((ViewCompat.MEASURED_STATE_MASK & p) | (16711680 & (table[(p >> 16) & 255] << 16))) | (MotionEventCompat.ACTION_POINTER_INDEX_MASK & (table[g] << 8))) | (table[b] & 255);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pix;
        }
    }

    private class Cube extends Filter{
        public Cube() {
            this.effectType = EffectType.Cube;
            this.boolPar[0] = new BoolParameter("Same image on each face", Boolean.FALSE);
            this.boolPar[1] = new TransparentParameter(true);
            this.colorPar[0] = new BackColorParameter();
        }

        public Bitmap Apply(Bitmap img) {
            if (img == null) {
                return null;
            }
            try {
                Bitmap bm;
                Bitmap dstImg;
                int width = img.getWidth();
                int height = img.getHeight();
                MapImage mapImage = new MapImage(EffectType.MapCube1);
                if (this.boolPar[0].value) {
                    int s = Math.min(width, height);
                    if (s * 2 > MaxSize()) {
                        s = MaxSize() / 2;
                    }
                    Bitmap croppedImg = MyImage.Clone(img, (width - s) / 2, (height - s) / 2, s, s);
                    bm = Filter.NewImage(s * 2, s * 2);
                    Canvas canvas = new Canvas (bm);
                    canvas.drawBitmap(croppedImg, 0.0f, 0.0f, null);
                    canvas.drawBitmap(croppedImg, (float) s, 0.0f, null);
                    canvas.drawBitmap(croppedImg, 0.0f, (float) s, null);
                    canvas.drawBitmap(croppedImg, (float) s, (float) s, null);
                    croppedImg.recycle();
                    dstImg = mapImage.Apply(bm);
                    bm.recycle();
                } else {
                    dstImg = mapImage.Apply(img);
                }
                if (this.boolPar[1].value) {
                    return dstImg;
                }
                bm = MyImage.NewImage(dstImg, this.colorPar[0].getValue(), false);
                new Canvas (bm).drawBitmap(dstImg, 0.0f, 0.0f, null);
                dstImg.recycle();
                return bm;
            } catch (Exception e) {
                return null;
            }
        }
    }


    public static int MaxSize() {
        if (_maxSize == 0) {
            _maxSize = 1200;
            try {
                long maxMemory = Runtime.getRuntime().maxMemory() / 1048576;
                if (maxMemory <= 96) {
                    _maxSize = 800;
                } else if (maxMemory <= 192) {
                    _maxSize = 1200;
                } else if (maxMemory <= 256) {
                    _maxSize = 1200;
                } else if (maxMemory <= 512) {
                    _maxSize = 2400;
                } else {
                    _maxSize = 3200;
                }
            } catch (Exception e) {
            }
        }
        return _maxSize;
    }

    public class MapImage extends Filter {
        public MapImage(EffectType effectType) {
            this.effectType = effectType;
        }

        public Bitmap Apply(Bitmap img) {
            return Apply(img, this.effectType);
        }

        public Bitmap Apply(Bitmap img, EffectType type) {
            if (type == EffectType.MapCube1) {
                return ApplyCube(img);
            }
            return null;
        }

        private Bitmap ApplyCube(Bitmap img) {
            try {
                int s = Math.min(img.getWidth(), img.getHeight());
                float s2 = (float) (s / 2);
                int w = (s * 4) / 6;
                float d = (float) (s / 6);
                Bitmap bm = GetImg(img, s, s);
                int[] srcPix = new int[(s * s)];
                bm.getPixels(srcPix, 0, s, 0, 0, s, s);
                Bitmap dstImg = Filter.NewImage(w, w);
                try {
                    int[] dstPix = new int[(w * w)];
                    dstImg.getPixels(dstPix, 0, w, 0, 0, w, w);
                    int y = 0;
                    while (y < w) {
                        int x = 0;
                        while (x < w) {
                            if (((float) x) < s2 && ((float) y) >= d) {
                                dstPix[(y * w) + x] = ClampBilinear(srcPix, s, s, (float) x, s2 + (((float) y) - d), dstPix[(y * w) + x]);
                            } else if (((float) y) >= d - ((float) x) && ((float) y) <= (((float) s) + d) - ((float) x)) {
                                float px = -1.0f;
                                float py = -1.0f;
                                float f = 1.0f;
                                if (((float) x) >= s2) {
                                    px = s2 + (((((float) x) - s2) * s2) / d);
                                    py = (((float) y) - d) + ((float) x);
                                    f = (2.0f + ((((float) x) - s2) / d)) / 3.0f;
                                }
                                if (((float) y) < d && x + y < w) {
                                    px = (((float) x) - d) + ((float) y);
                                    py = (((float) y) * s2) / d;
                                    f = (2.0f + ((d - ((float) y)) / d)) / 3.0f;
                                }
                                if (px >= 0.0f && px < ((float) s) && py >= 0.0f && py < ((float) s)) {
                                    dstPix[(y * w) + x] = ClampBilinear(srcPix, s, s, px, py, dstPix[(y * w) + x]);
                                }
                                if (f >= 0.0f && f < 1.0f) {
                                    int p = dstPix[(y * w) + x];
                                    int r = (int) (((float) ((p >> 16) & 255)) * f);
                                    int g = (int) (((float) ((p >> 8) & 255)) * f);
                                    dstPix[(y * w) + x] = (((ViewCompat.MEASURED_STATE_MASK & (((p >> 24) & 255) << 24)) | (16711680 & (r << 16))) | (MotionEventCompat.ACTION_POINTER_INDEX_MASK & (g << 8))) | (((int) (((float) (p & 255)) * f)) & 255);
                                }
                            }
                            x++;
                        }
                        y++;
                    }
                    bm.recycle();
                    dstImg.setPixels(dstPix, 0, w, 0, 0, w, w);
                    return dstImg;
                } catch (Exception e) {
                    return dstImg;
                }
            } catch (Exception e2) {
                return null;
            }
        }

        private Bitmap GetImg(Bitmap img, int w, int h) {
            Bitmap dstImg = null;
            try {
                int width = img.getWidth();
                int height = img.getHeight();
                int x = 0;
                int y = 0;
                if (width > height) {
                    x = (width - w) / 2;
                } else if (height > width) {
                    y = (height - h) / 2;
                }
                dstImg = MyImage.Clone(img, x, y, w, h);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dstImg == null) {
                return Filter.Clone(img);
            }
            return dstImg;
        }
    }
}
