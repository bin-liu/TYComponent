/**
 * The MIT License (MIT)
 * Copyright (c) 2012-2014 唐虞科技(TangyuSoft) Corporation
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
package com.tangyu.component.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tangyu.component.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * ------------------
 * *****************|
 * *****************|
 * ***** MSGAREA ***|
 * *****************|
 * *****************|
 * ****          ***|
 * ****  HANDLE  ***|
 * ****   AREA   ***|
 * ****          ***|
 * ------------------
 * </pre>
 * <p/>
 * Often use with guide</br>
 * <p/>
 * Cover with a black layer. and hollow the handle. </br>
 * <p/>
 * if touch event position in handle area. it will be call handle.click();</br>
 * <p/>
 * u can also set message to show.</br>
 *
 * @author bin
 */
public class TYHollowView extends FrameLayout {

    public static Paint getDefPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(30f);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.WHITE);
        return paint;
    }

    private Paint mPaint = getDefPaint();

    private List<Hollow> mHollows = new LinkedList<Hollow>();

    private TextView mVMsg;

    private TextView mVBlackLayout;

    private HollowListener mListener;

    @SuppressLint("NewApi")
    public TYHollowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int sdkInt = android.os.Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mVBlackLayout = new TextView(context, attrs, defStyle) {

            Rect rect = new Rect();

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                if (mHollows.size() > 0) {
                    canvas.clipRect(0, 0, getWidth(), getHeight());
                    for (Hollow hollow : mHollows) {
                        if (hollow.mHandler.getVisibility() != View.VISIBLE) {
                            continue;
                        }
                        hollow.mHandler.getHitRect(rect);
                        rect.offset(hollow.mDelta.x, hollow.mDelta.y);
                        canvas.clipRect(rect, Region.Op.DIFFERENCE);
                    }
                    canvas.drawColor(0xAA000000);
                }
            }
        };

        mVBlackLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(mVBlackLayout);
    }

    public TYHollowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TYHollowView(Context context) {
        this(context, null, 0);
    }

    public void addHollow(Hollow hollow) {
        if (hollow != null && hollow.mVMsg != null) {
            mHollows.add(hollow);
            addView(hollow.mVMsg);
        }
    }

    public List<Hollow> getHollows() {
        return mHollows;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mVBlackLayout.getVisibility() == View.VISIBLE) {
            mVBlackLayout.layout(left, top, right, bottom);
        }

        for (Hollow hollow : mHollows) {
            if (hollow.mVMsg != null && hollow.mVMsg.getVisibility() != View.GONE) {
                Rect rect = calculateLayout(hollow);
                hollow.mVMsg.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    private Rect calculateLayout(Hollow hollow) {
        Rect rect = new Rect();
        Rect handleRect = new Rect();
        hollow.mHandler.getHitRect(handleRect);
        handleRect.offset(hollow.mDelta.x, hollow.mDelta.y);
        int widthMeasureSpec, heightMeasureSpec;
        switch (hollow.mPos) {
            case Hollow.POS_LEFT_HOLLOW:
                rect.right = handleRect.left;
                rect.top = handleRect.top;
//			if (hollow.mVMsg.getMeasuredWidth() <= handleRect.left) {
//				rect.left = handleRect.left - hollow.mVMsg.getMeasuredWidth();
//			} else {
                rect.left = 0;
//			}
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(handleRect.left, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, MeasureSpec.EXACTLY);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.bottom = rect.top + hollow.mVMsg.getMeasuredHeight();
                break;
            case Hollow.POS_TOP_HOLLOW:
                rect.right = handleRect.right;
                rect.bottom = handleRect.top;
                if (hollow.mVMsg.getMeasuredHeight() <= handleRect.top) {
                    rect.top = handleRect.top - hollow.mVMsg.getMeasuredHeight();
//			} else {
//				rect.top = 0;
                }
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(handleRect.right, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, MeasureSpec.EXACTLY);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.left = rect.right - hollow.mVMsg.getMeasuredWidth();
                break;
        }
        Util.v("w = " + hollow.mVMsg.getMeasuredWidth() + "|h = " + hollow.mVMsg.getMeasuredHeight());
        Util.v(rect.toString());
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHollows.size() > 0) {
            int x = (int) event.getX(), y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect rect = new Rect();
                for (Hollow hollow : mHollows) {
                    hollow.mHandler.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        hollow.mHandler.performClick();
                    }
                }
            }
        }
        if (mListener != null) {
            mListener.onHollowDownListener();
        }
        setVisibility(GONE);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (getVisibility() == View.VISIBLE) {
            if (mVBlackLayout.getVisibility() == View.VISIBLE) {
                mVBlackLayout.draw(canvas);
            }
            // draw msg
            if (mHollows.size() > 0) {
                for (Hollow h : mHollows) {
                    if (h != null && h.mVMsg.getVisibility() == View.VISIBLE) {
                        h.mVMsg.draw(canvas);
                    }
                }
            }
        }
    }

    public void setOnHollowListener(HollowListener listener) {
        mListener = listener;
    }

    public interface HollowListener {
        public void onHollowDownListener();
    }

    public static class Hollow {

        public static final int POS_LEFT_HOLLOW = 0;
        public static final int POS_TOP_HOLLOW = 1;
        public static final int POS_RIGHT_HOLLOW = 2;
        public static final int POS_BOTTOM_HOLLOW = 3;

        View mHandler;
        TextView mVMsg;
        Point mDelta;
        int mPos;

        public Hollow(View handler) {
            mHandler = handler;
        }

        public static TextView generalMsgView(Context ctx) {
            if (ctx == null) {
                throw new NullPointerException("ctx is null");
            }
            TextView mVMsg = new TextView(ctx);
            mVMsg.setTextColor(Color.WHITE);
            mVMsg.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL));
            return mVMsg;
        }

        /**
         * @param handleView
         * @param id         ID of the view which layer is same as hollow view.
         * @return
         */
        public static Point generalDelta(View handleView, int id) {
            if (handleView == null) {
                throw new NullPointerException("handleView is null");
            }
            ViewGroup parent;
            Point delta = new Point();
//			Util.v("DDDD " + delta.x + " | " + delta.y);
            parent = (ViewGroup) handleView.getParent();
            delta.offset(parent.getLeft(), parent.getTop());
//			Util.v("DDDD " + delta.x + " | " + delta.y);
            while (parent != null && parent.getId() != id) {
                parent = (ViewGroup) parent.getParent();
                if (parent != null) {
                    delta.offset(parent.getLeft(), parent.getTop());
//					Util.v("DDDD " + delta.x + " | " + delta.y);
                }
            }
            return delta;
        }

        public void setDelta(Point delta) {
            if (delta == null) {
                throw new NullPointerException();
            }
            mDelta = delta;
        }

        public void setPosition(int position) {
            if (position < 0 || position > 3) {
                throw new IllegalArgumentException();
            }
            mPos = position;
        }

        public void setMsgView(TextView msgView) {
            mVMsg = msgView;
        }

    }
}