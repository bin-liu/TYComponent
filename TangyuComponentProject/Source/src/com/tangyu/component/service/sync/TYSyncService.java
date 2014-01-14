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
package com.tangyu.component.service.sync;

import android.app.IntentService;
import android.content.Intent;

import com.tangyu.component.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author binliu on 1/11/14.
 */
public class TYSyncService extends IntentService {

    public static final String INTENT_DATA = "DATA";

    public static final String INTENT_CONFIG = "CONFIG";

    public static final String ACTION_TYSYNC_RESPONSE = TYSyncService.class.getName() + ".RESPONSE";

    public static final String INTENT_RESPONSE_SUCCESS = "SUCCESS";

    public static final String INTENT_RESPONSE_CONTENT = "CONTENT";

    public TYSyncService() {
        this(TYSyncService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TYSyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        if (!intent.hasExtra(INTENT_CONFIG)) {
            throw new NullPointerException("Not found the key of INTENT_DATA in intent extra, It's couldn't be null");
        }
        if (!intent.hasExtra(INTENT_DATA)) {
            throw new NullPointerException("Not found the key of INTENT_DATA in intent extra, It's couldn't be null");
        }

        List<? extends TYNameValuePair> data = intent.getParcelableArrayListExtra(INTENT_DATA);
        TYSyncNetConfigure config = intent.getParcelableExtra(INTENT_CONFIG);

        onPreExecute(config, data);
        TYResponseResult responseResult = executeByModel(config, data);
        onPostExecute(responseResult);

        Intent responseIntent = new Intent(ACTION_TYSYNC_RESPONSE);
        responseIntent.putExtra(INTENT_RESPONSE_SUCCESS, responseResult.isSuccess);
        responseIntent.putExtra(INTENT_RESPONSE_CONTENT, responseResult.content);
        sendBroadcast(responseIntent);
        Util.v("Action = " + ACTION_TYSYNC_RESPONSE);

    }

    private TYResponseResult executeByModel(TYSyncNetConfigure config, List<? extends TYNameValuePair> data) {
        if (TYSyncNetConfigure.MODEL_POST.equals(config.model())) {
            return executePost(config, data);
        } else {
            return executeGet(config, data);
        }
    }


    private TYResponseResult executePost(TYSyncNetConfigure config, List<? extends TYNameValuePair> data) {
        Util.v("execute post");
        HttpPost post = new HttpPost(config.remoteURL());
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, config.connectionTimeoutInMills());
        HttpConnectionParams.setSoTimeout(params, config.soTimeoutInMills());

        try {
            post.setEntity(new UrlEncodedFormEntity(data, config.encodeFormat()));
            HttpResponse httpResponse = new DefaultHttpClient(params).execute(post);
            TYResponseResult result = new TYResponseResult(httpResponse);
            result.isSuccess = httpResponse.getStatusLine().getStatusCode() == 200;
            result.content = EntityUtils.toString(httpResponse.getEntity());
            return result;
        } catch (UnsupportedEncodingException e) {
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private TYResponseResult executeGet(TYSyncNetConfigure config, List<? extends TYNameValuePair> data) {
        // TODO: will be done in future.
        return null;
    }

    protected void onPreExecute(TYSyncNetConfigure config, List<? extends TYNameValuePair> data) {
        Util.v("pre execute");
    }

    protected void onPostExecute(TYResponseResult responseResult) {
        Util.v("post execute");
    }

    protected class TYResponseResult {
        private HttpResponse httpResponse;
        private boolean isSuccess;
        private String content;

        public TYResponseResult(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

    }
}
