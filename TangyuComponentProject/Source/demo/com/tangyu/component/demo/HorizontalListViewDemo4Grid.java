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
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangyu.component.R;
import com.tangyu.component.view.HorizontalListView;

/**
 * lionlions say some bug about HorizontalListView. so i write this to reproduce question.
 *
 * @author binliu on 13-10-10.
 */
public class HorizontalListViewDemo4Grid extends Activity {

    private static final boolean isTestGridView = true;

    private HorizontalListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // only gridView.
//        final GridView gridView = createGridViewWithAdapter();
//        gridView.setBackgroundColor(0xFFAA66CC);
//        setContentView(gridView);

        // horizontalListView + gridView.
        mListView = createHListView();
        setContentView(mListView);
        mListView.setAdapter(new HListViewAdapter4GridView());

    }

    HorizontalListView createHListView() {
        HorizontalListView listView = new HorizontalListView(this, null);
        listView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(0x33FF0000);
        return listView;
    }

    GridView createGridViewWithAdapter() {
        GridView gridView = createGridView();
        gridView.setAdapter(createGridViewAdapter());
        return gridView;
    }

    TextView createTextView() {
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.simple_list_item_2, null);
        if (tv.getLayoutParams() == null) {
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return tv;
    }

    GridView createGridView() {
        GridView gridView = new GridView(this, null);
        gridView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT));
        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HorizontalListViewDemo4Grid.this, "click position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        return gridView;
    }

    BaseAdapter createGridViewAdapter() {
        final String[] mStrings = {"1 : Beyaz Peynir",
                "2 : Harbourne Blue",
                "3 : Doolin",
                "4 : Cougar Gold",
                "5 : Blue Castello",
                "6 : Appenzell",
                "7 : Lancer Gold",
                "8 : Debbie",
                "9 : Bin",
        };
        return new ArrayAdapter<String>(this, R.layout.simple_list_item_2, mStrings);
    }

    public class HListViewAdapter4GridView extends BaseAdapter {
        final int[] colors = new int[] {
                0xFF33B5E5,
                0xFFAA66CC,
                0xFF99CC00,
                0xFFFFBB33,
                0xFFFF4444,
                0xFF0099CC,
                0xFF9933CC,
                0xFF669900,
                0xFFFF8800,
                0xFFCC0000,
        };

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (isTestGridView) {
                    // test for GridView.
                    convertView = createGridViewWithAdapter();
                } else {
                    // test for TextView.
                    convertView = createTextView();
                }
            }
            convertView.setBackgroundColor(colors[position % colors.length]);
            return convertView;
        }
    }
}
