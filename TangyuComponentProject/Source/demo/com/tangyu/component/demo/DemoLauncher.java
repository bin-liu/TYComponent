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
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tangyu.component.R;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author binliu on 1/13/14.
 */
public class DemoLauncher extends Activity implements ListView.OnItemClickListener {

    public static final String INTENT_FILTER = "com.tangyu.component.demo.action";

    private ListView mVList;
    private List<ResolveInfo> resolveInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_launcher);

        mVList = (ListView) findViewById(R.id.demo_launcher_list);

        resolveInfoList = getPackageManager().queryIntentActivities(new Intent(INTENT_FILTER, null), 0);

        LinkedList<String> activities = new LinkedList<String>();
        for (int i = 0; i < resolveInfoList.size(); ++i) {
            String fullname = resolveInfoList.get(i).activityInfo.name;
            Pattern pattern = Pattern.compile("\\.");
            String[] split = pattern.split(fullname);
            String activityName = split[split.length - 1];
            activities.add(activityName);
        }
        mVList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activities));

        mVList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ResolveInfo info = resolveInfoList.get(position);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, info.activityInfo.name));
        startActivity(intent);
    }
}
