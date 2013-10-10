package com.tangyu.component.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;

/**
 * image utils
 *
 * @author bin
 */
public class ImageUtils {

    /**
     * scale the Bitmap width and height to parameter.</br>
     * if covers non-null, draw cover on bitmap one by one.
     *
     * @param src
     * @param destWidth
     * @param destHeight
     * @param padding
     * @param covers
     * @return
     */
    public static synchronized Bitmap imgScaleCenter(Bitmap src, int destWidth, int destHeight, int padding, Bitmap... covers) {
        final int sWidth = src.getWidth();
        final int sHeight = src.getHeight();
        final int half = padding >> 1;

        Bitmap out = Bitmap.createBitmap(destWidth, destHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        Rect dst = new Rect(half, half, destWidth - half, destHeight - half);

        canvas.drawBitmap(src, new Rect(0, 0, sWidth, sHeight), dst, null);

        if (covers != null && covers.length > 0) {
            for (int i = 0; i < covers.length; i++) {
                Bitmap bitmap = covers[i];
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, destWidth, destHeight), null);
            }
        }

        return out;
    }

    /**
     * As same as {@link ImageUtils#imgScaleCenter(android.graphics.Bitmap, int, int, android.graphics.Bitmap...)}
     *
     * @param src
     * @param destWidth
     * @param destHeight
     * @param padding
     * @param cb
     * @param covers
     * @note all param will be used in other thread. so keep it don't change on exec time.
     * @see {@link ImageUtils#imgScaleCenter(android.graphics.Bitmap, int, int, android.graphics.Bitmap...)}
     */
    public static void asyncImgScaleCenter(Bitmap src, final int destWidth,
                                           final int destHeight, final int padding, final Callback cb,
                                           final Bitmap... covers) {
        new AsyncTask<Bitmap, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Bitmap... params) {
                Bitmap src = params[0];
                return imgScaleCenter(src, destWidth, destHeight, padding, covers);
            }

            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (cb != null) {
                    cb.onFinish(result);
                }
            }

        }.execute(src);
    }

    public static interface Callback {
        void onFinish(Bitmap bmp);
    }
}
