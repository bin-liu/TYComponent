/**
 * The MIT License (MIT)
 * Copyright (c) 2012  Tang Yu Software Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tangyu.component.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import java.util.Arrays;

/**
* this code from the internet.
* */
public class StateDrawableTool {

    public static final int TO_LIGHT = 0;
    public static final int TO_DARK = 1;

    public int toWhich = TO_DARK;

    public Paint userPaint;
    private Context mCtx;

    public StateListDrawable convertToStateDrawable(Context ctx, Drawable drawable) {
        mCtx = ctx;
        return createSLD(ctx, drawable,
                userPaint == null ? getPaintByModel() : userPaint);
    }

    public void setStateDrawableModel(int toWhich) {
        this.toWhich = toWhich;
    }

    public void setPaint(Paint p) {
        userPaint = p;
    }

    public Drawable createDrawable(Drawable d, Paint p) {
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap b = bd.getBitmap();
        if (b == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(),
                bd.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b, 0, 0, p);
        return new BitmapDrawable(mCtx.getResources(), bitmap);
    }

    public StateListDrawable createSLD(Context context, Drawable drawable, Paint p) {
        StateListDrawable bg = new StateListDrawable();

        int[] state = drawable.getState();
        Arrays.sort(state);
        int idx = Arrays.binarySearch(state, android.R.attr.state_pressed);
        if (idx < 0) {
            // no pressed state
            Drawable normal = drawable;
            Drawable pressed = createDrawable(drawable, p);
            bg.addState(new int[]{android.R.attr.state_pressed}, pressed);
            bg.addState(new int[]{}, normal);
        }
        return bg;
    }

    public Paint getPaintByModel() {
        Paint paint = new Paint();
        switch (toWhich) {
            case TO_LIGHT:
                paint.setColor(0x40222222);
                break;
            case TO_DARK:
                ColorMatrix cm = new ColorMatrix();
                float contrast = 0.5f;
                cm.set(new float[]{contrast, 0, 0, 0, 0, 0,
                        contrast, 0, 0, 0,// 改变对比度
                        0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                break;
            default:
                break;
        }
        return paint;
    }
}
