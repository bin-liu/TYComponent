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
package com.tangyu.component.demo.service.sync;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.tangyu.component.service.sync.TYNameValuePair;
import com.tangyu.component.service.sync.TYSyncNetConfigure;
import com.tangyu.component.service.sync.TYSyncService;
import com.tangyu.component.service.sync.TYSyncTrigger;

import java.util.ArrayList;

/**
 * @author binliu on 1/11/14.
 */
public class SyncTrigger extends TYSyncTrigger {

    public static final int POUR_OVER_FLOW = 100;

    public static final int POUR_STEP = 1;

    private static SyncTrigger mInstance;

    public static synchronized SyncTrigger getIntance(Context context) {
        return null == mInstance ? mInstance = new SyncTrigger(context) : mInstance;
    }

    private SyncNetConfigure config = new SyncNetConfigure();

    public SyncTrigger(Context context) {
        super(context);
    }

    @Override
    public void onSync() {
        new AsyncTask<String, String, ArrayList<? extends TYNameValuePair>>() {

            @Override
            protected ArrayList<? extends TYNameValuePair> doInBackground(String... params) {
                //TODO: to prepare sync data.
                return new ArrayList<TYNameValuePair>();
            }

            @Override
            protected void onPostExecute(ArrayList<? extends TYNameValuePair> tyNameValuePairs) {
                Intent intent = new Intent(context, SyncService.class);
                intent.putExtra(TYSyncService.INTENT_CONFIG, createConfigIfNeed());
                intent.putParcelableArrayListExtra(TYSyncService.INTENT_DATA, tyNameValuePairs);
                context.startService(intent);
            }
        }.execute();

    }

    @Override
    public int overFlow() {
        return POUR_OVER_FLOW;
    }

    private TYSyncNetConfigure createConfigIfNeed() {
        // Sometimes, you may need different config when syncing..
        // such as different remote address.
        return null == config ? config = new SyncNetConfigure() : config;
    }
}
