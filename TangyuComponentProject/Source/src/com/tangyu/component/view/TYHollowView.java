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
import android.widget.ImageView;
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
 * Often use in guide</br>
 * <p/>
 * Cover with a black layer and multiple hollow. </br>
 * <p/>
 * if touch event position in handle area. The HollowListener will be handle it.</br>
 * <p/>
 * u can also set a message view to hollow to display the detail of hollow.</br>
 *
 * STEPS:<br>
 * <pre>
 *     1. create a hollow object.
 *     2. invoke {@link com.tangyu.component.view.TYHollowView.Hollow#calculateDelta(android.view.View, int)} to calculate the offset of hollow.
 *     3. create a message view, TextView ImageView or other, and set to hollow if need.
 *     4. if you set a message view. u can setting the position and gap params.
 *     5. add to hollow view.
 * </pre>
 *
 * SIMPLE CODE: <br>
 * <pre>
 *  TYHollowView.Hollow hollow = new TYHollowView.Hollow(item);
 *  TextView msgView = TYHollowView.Hollow.createSimpleTextView(this);
 *  msgView.setText(sb.toString());
 *  Point delta = TYHollowView.Hollow.calculateDelta(item, mRootView.getId());
 *  hollow.setDelta(delta);
 *  hollow.setPosition(position[i % position.length]);
 *  hollow.setMsgView(msgView);
 *  hollow.setGapBetweenMsgAndHollow((i + 1) * 20);
 *  mRootView.addHollow(hollow);
 * </pre>
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

    protected Paint mPaint = getDefPaint();

    protected List<Hollow> mHollows = new LinkedList<Hollow>();

    protected TextView mVBlackLayout;

    protected HollowListener mListener;

    protected Hollow mFocusHollow;

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
            requestLayout();
        }
    }

    public void removeHollow(Hollow hollow) {
        if (!Util.isNull(mHollows)) {
            for (Hollow item : mHollows) {
                if (item.equals(hollow)) {
                    mHollows.remove(item);
                    // be careful if you wanna remove 'break'
                    break;
                }
            }
        }
    }


    public final List<Hollow> getHollows() {
        return mHollows;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFocusHollow = null;
                if (mHollows.size() > 0) {
                    int x = (int) event.getX(), y = (int) event.getY();
                    Rect rect = new Rect();
                    for (Hollow hollow : mHollows) {
                        hollow.mHandler.getHitRect(rect);
                        if (rect.contains(x, y)) {
                            mFocusHollow = hollow;
                            onFocusHollowWhenTouchDown(hollow);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (null != mListener) {
                    boolean hasFocusHollow = null != mFocusHollow;
                    mListener.onTappedListener(hasFocusHollow, hasFocusHollow ? mFocusHollow.mHandler : null);
                }
                break;
        }
        return true;
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
                Rect rect = calculateLayoutOfHollowMsgView(hollow);
                onLayoutOfHollowMsgView(hollow, rect);
                hollow.mVMsg.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    private Rect calculateLayoutOfHollowMsgView(Hollow hollow) {
        Rect rect = new Rect();
        Rect handleRect = new Rect();
        hollow.mHandler.getHitRect(handleRect);
        handleRect.offset(hollow.mDelta.x, hollow.mDelta.y);
        int widthMeasureSpec, heightMeasureSpec;
        switch (hollow.mPos) {
            case Hollow.POS_LEFT_HOLLOW:
                rect.left = 0;
                rect.right = handleRect.left - hollow.mGapMsgAndHollow;
                rect.top = handleRect.top;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.AT_MOST);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.left = rect.width() - hollow.mVMsg.getMeasuredWidth();
                rect.bottom = rect.top + hollow.mVMsg.getMeasuredHeight();
                break;
            case Hollow.POS_TOP_HOLLOW:
                rect.top = 0;
                rect.bottom = handleRect.top - hollow.mGapMsgAndHollow;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.AT_MOST);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.top = rect.bottom - hollow.mVMsg.getMeasuredHeight();
                rect.left = handleRect.centerX() - (hollow.mVMsg.getMeasuredWidth() >> 1);
                rect.right = rect.left + hollow.mVMsg.getMeasuredWidth();
                break;
            case Hollow.POS_RIGHT_HOLLOW:
                rect.left = handleRect.right + hollow.mGapMsgAndHollow;
                rect.right = getMeasuredWidth() - rect.left;
                rect.top = handleRect.top;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.AT_MOST);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.right = rect.left + hollow.mVMsg.getMeasuredWidth();
                rect.bottom = rect.top + hollow.mVMsg.getMeasuredHeight();
                break;
            case Hollow.POS_BOTTOM_HOLLOW:
                rect.top = handleRect.bottom + hollow.mGapMsgAndHollow;
                rect.bottom = getMeasuredHeight();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.AT_MOST);
                hollow.mVMsg.measure(widthMeasureSpec, heightMeasureSpec);
                rect.bottom = rect.top + hollow.mVMsg.getMeasuredHeight();
                rect.left = handleRect.centerX() - (hollow.mVMsg.getMeasuredWidth() >> 1);
                rect.right = rect.left + hollow.mVMsg.getMeasuredWidth();
                break;
        }
        Util.v("w = " + hollow.mVMsg.getMeasuredWidth() + "|h = " + hollow.mVMsg.getMeasuredHeight());
        Util.v(rect.toString());
        return rect;
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

    /**
     * Layout the msg view of hollow.<br>
     *
     * If you wants to change the position of msg view. Just modify the parameter ‘rect’.<br>
     *
     * The rect is absolute coordinate in this view.
     *
     * @param hollow
     * @param rect
     */
    protected void onLayoutOfHollowMsgView(Hollow hollow, Rect rect) {

    }

    /**
     * find out the hollow when user touch.
     * @param hollow
     */
    protected void onFocusHollowWhenTouchDown(Hollow hollow) {

    }

    public void setOnHollowListener(HollowListener listener) {
        mListener = listener;
    }

    public interface HollowListener {
        /**
         * User was tapped view.
         * @param hasTappedHollow whether tapped in hollow or not.
         * @param view
         */
        public void onTappedListener(boolean hasTappedHollow, View view);
    }

    public static class Hollow {

        public static final int POS_LEFT_HOLLOW = 0;
        public static final int POS_TOP_HOLLOW = 1;
        public static final int POS_RIGHT_HOLLOW = 2;
        public static final int POS_BOTTOM_HOLLOW = 3;

        protected View mHandler;
        protected View mVMsg;
        protected Point mDelta;
        protected int mPos;
        protected int mGapMsgAndHollow;

        public Hollow(View handler) {
            mHandler = handler;
        }

        public static TextView createSimpleTextView(Context ctx) {
            if (ctx == null) {
                throw new NullPointerException("ctx is null");
            }
            TextView textView = new TextView(ctx);
            textView.setTextColor(Color.WHITE);
            textView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL));
            return textView;
        }

        public static ImageView createSimpleImageView(Context ctx) {
            if (ctx == null) {
                throw new NullPointerException("ctx is null");
            }
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            return imageView;
        }

        /**
         * To calculate the absolute coordinate between view with its super view.
         * @param handleView
         * @param dependViewId the super ID of handleView.
         * @return
         */
        public static Point calculateDelta(View handleView, int dependViewId) {
            if (handleView == null) {
                throw new NullPointerException("handleView is null");
            }
            ViewGroup parent;
            Point delta = new Point();
//			Util.v("DDDD " + delta.x + " | " + delta.y);
            parent = (ViewGroup) handleView.getParent();
            delta.offset(parent.getLeft(), parent.getTop());
//			Util.v("DDDD " + delta.x + " | " + delta.y);
            while (parent != null && parent.getId() != dependViewId) {
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

        public void setMsgView(View msgView) {


            mVMsg = msgView;
        }

        public void setGapBetweenMsgAndHollow(int gap) {
            mGapMsgAndHollow = gap;
        }

    }
}