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
package com.tangyu.component.demo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangyu.component.R;
import com.tangyu.component.Util;
import com.tangyu.component.view.TYHollowView;

/**
 * @author binliu on 1/15/14.
 */
public class HollowViewDemo extends Activity {

    private View mView1;
    private View mView2;
    private View mView3;
    private View mView4;
    private View[] mViews;
    private TYHollowView mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_hollow_view);

        mView1 = findViewById(R.id.demo_hollow_view_1);
        mView2 = findViewById(R.id.demo_hollow_view_2);
        mView3 = findViewById(R.id.demo_hollow_view_3);
        mView4 = findViewById(R.id.demo_hollow_view_4);
        mViews = new View[] {mView1, mView2, mView3, mView4};

        mRootView = (TYHollowView) findViewById(R.id.demo_hollow_view_hollow);

        mRootView.setOnHollowListener(new TYHollowView.HollowListener() {

            @Override
            public void onTappedListener(boolean hasTappedHollow, View view) {
                if (hasTappedHollow) {
                    Util.toast(HollowViewDemo.this, "hollow Tapped!!", true);
                } else {
                    Util.toast(HollowViewDemo.this, "hollow not Tapped!!", true);
                }
                mRootView.setVisibility(View.GONE);
            }
        });

        final int[] position = new int[] {
                TYHollowView.Hollow.POS_BOTTOM_HOLLOW,
                TYHollowView.Hollow.POS_RIGHT_HOLLOW,
                TYHollowView.Hollow.POS_LEFT_HOLLOW,
                TYHollowView.Hollow.POS_TOP_HOLLOW,
        };

        for (int i = 0; i < mViews.length; ++i) {
            View item = mViews[i];

            TYHollowView.Hollow hollow = new TYHollowView.Hollow(item);
            View msgView = null;
            if (i % 2 == 1) {
                msgView = TYHollowView.Hollow.createSimpleTextView(this);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < i * 2; j++) {
                    sb.append("balabalabalabalabalabalabalabala");
                }
                ((TextView) msgView).setText(sb.toString());
            } else {
                msgView = TYHollowView.Hollow.createSimpleImageView(this);
                ((ImageView) msgView).setImageResource(R.drawable.ic_launcher);
            }

            Point delta = TYHollowView.Hollow.calculateDelta(item, mRootView.getId());
            hollow.setDelta(delta);
            hollow.setPosition(position[i % position.length]);
            hollow.setMsgView(msgView);
            hollow.setGapBetweenMsgAndHollow((i + 1) * 20);
            mRootView.addHollow(hollow);
        }

    }
}
