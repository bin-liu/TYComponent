/**
 * The MIT License (MIT)
 * Copyright (c) 2012-2014 唐虞科技 Corporation
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
package com.tangyu.component;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.tangyu.component.demo.HorizontalListViewDemo;
import com.tangyu.component.view.HorizontalListView;

import java.lang.reflect.Field;

/**
 * @author bin
 */
public class TestHorizontalListView extends ActivityInstrumentationTestCase2<HorizontalListViewDemo> {

    private HorizontalListView mVList;
    protected int mMinX;

    public TestHorizontalListView() {
        super("com.tangyu.component.test", HorizontalListViewDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mVList = getActivity().testGetListView();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private final BaseAdapter genAdapter(String[] data) {
        return new ArrayAdapter<String>(getActivity(),
                R.layout.simple_list_item_3, data);
    }

    public void testForSelection() throws Throwable {
        TSelectionDataLess();
        TSelectionDataBig();
    }

    public void testForRequestFreeze() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVList.setAdapter(genAdapter(getActivity().mStringShorts));
            }

        });
        //TODO to be completed
    }

    public void testForScrollListener() {
        //TODO to be completed
    }

    private void TSelectionDataLess() throws InterruptedException {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVList.setAdapter(genAdapter(getActivity().mStringShorts));
            }

        });
        Thread.sleep(1000);
        // 1. delta out of range
        TSelectionLeft(10000);
        TSelectionRight(10000);

        // 2. delta is zero
        TSelectionLeft(0);
        TSelectionRight(0);

        // 2. delta is positive
        TSelectionLeft(100);
        TSelectionRight(100);

        // 3. delta is negative
        TSelectionLeft(-100);
        TSelectionRight(-100);

    }

    private void TSelectionDataBig() throws InterruptedException {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVList.setAdapter(genAdapter(getActivity().mStringMores));
            }

        });
        Thread.sleep(1000);
        // 1. delta out of range
        TSelectionLeft(10000);
        TSelectionRight(10000);

        // 2. delta is zero
        TSelectionLeft(0);
        TSelectionRight(0);

        // 2. delta is positive
        TSelectionLeft(100);
        TSelectionRight(100);

        // 3. delta is negative
        TSelectionLeft(-100);
        TSelectionRight(-100);
    }


    private void TSelectionLeft(final int i) throws InterruptedException {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVList.setSelectionFromLeft(0, i);
            }

        });

        Thread.sleep(1000);

        final int left = mVList.getChildAt(0).getLeft();

        assertCorrectChildCount();
        assertEquals(mVList.getFirstVisiblePosition(), 0);
        switch (i) {
            case 0:
            case 10000:
                assertEquals(left, 0);
                assertEquals(getMinX(), 0);
                assertEquals(getMaxX(), isLessData() ? 0 : Integer.MAX_VALUE);
                break;
            case 100:
                assertEquals(left, 0);
                assertEquals(getMinX(), 0);
                assertEquals(getMaxX(), isLessData() ? 0 : Integer.MAX_VALUE);
                break;
            case -100:
                assertEquals(left, isLessData() ? 0 : -100);
                assertEquals(getMinX(), isLessData() ? 0 : -100);
                assertEquals(getMaxX(), isLessData() ? 0 : Integer.MAX_VALUE);
                break;
        }
    }

    private void TSelectionRight(final int i) throws InterruptedException {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVList.setSelectionFromLeft(getApr().getCount() - 1, i);
            }

        });

        Thread.sleep(1000);

        final int right = last().getRight();
        assertCorrectChildCount();
        assertEquals(mVList.getLastVisiblePosition(), getApr().getCount() - 1);
        switch (i) {
            case 0:
            case 10000:
                if (isLessData()) {
                    assertTrue(first().getLeft() == 0);
                    assertTrue(right < mVList.getWidth());
                } else {
                    assertEquals(right, mVList.getWidth());
                }
                assertEquals(getMinX(), isLessData() ? 0 : Integer.MIN_VALUE);
                assertEquals(getMaxX(), 0);
                break;
            case 100:
                if (isLessData()) {
                    assertTrue(first().getLeft() == 0);
                    assertTrue(right < mVList.getWidth());
                } else {
                    assertEquals(right, mVList.getWidth());
                }
                assertEquals(getMinX(), isLessData() ? 0 : Integer.MIN_VALUE);
                assertEquals(getMaxX(), 0);
                break;
            case -100:
                if (isLessData()) {
                    assertTrue(first().getLeft() == 0);
                    assertTrue(right < mVList.getWidth());
                } else {
                    assertEquals(right, mVList.getWidth());
                }
                assertEquals(getMinX(), isLessData() ? 0 : Integer.MIN_VALUE);
                assertEquals(getMaxX(), 0);
                break;
        }
    }

    private void assertCorrectChildCount() {
        View firstChild = mVList.getChildAt(0);
        View lastChild = mVList.getChildAt(mVList.getChildCount() - 1);
        if (isLessData()) {
            assertTrue(mVList.getChildCount() == getApr().getCount());
        } else {
            assertTrue(mVList.getChildCount() < getApr().getCount());
        }
//		if (firstChild.getLeft() + firstChild.getMeasuredWidth() < 0) {
//			return false;
//		}
//		if (lastChild.getRight() - lastChild.getMeasuredWidth() < mVList.getMeasuredWidth()) {
//			return false;
//		}
    }

    private BaseAdapter getApr() {
        return (BaseAdapter) mVList.getAdapter();
    }

    private View first() {
        return mVList.getChildAt(0);
    }

    private View last() {
        return mVList.getChildAt(mVList.getChildCount() - 1);
    }

    private boolean isLessData() {
        return getApr().getCount() == mVList.getChildCount();
    }

    private boolean isBigData() {
        return getApr().getCount() > mVList.getChildCount();
    }

    private int getMaxX() {
        try {
            Field field = mVList.getClass().getDeclaredField("mMaxX");
            field.setAccessible(true);
            return field.getInt(mVList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("max not found. please check class name and field name");
    }

    private int getMinX() {
        try {
            Field field = mVList.getClass().getDeclaredField("mMinX");
            field.setAccessible(true);
            return mMinX = field.getInt(mVList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("min not found. please check class name and field name");
    }


}
