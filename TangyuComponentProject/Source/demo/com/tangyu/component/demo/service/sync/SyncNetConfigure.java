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
package com.tangyu.component.demo.service.sync;

import android.os.Parcel;

import com.tangyu.component.service.sync.TYSyncNetConfigure;

import org.apache.http.protocol.HTTP;

/**
 * @author binliu on 1/11/14.
 */
public class SyncNetConfigure implements TYSyncNetConfigure {

    public SyncNetConfigure() {

    }

    public SyncNetConfigure(Parcel parcel) {
        //current do nothing.
    }

    @Override
    public String remoteURL() {
        return Config.host;
    }

    @Override
    public int connectionTimeoutInMills() {
        return Config.Default_Connection_Timeout;
    }

    @Override
    public int soTimeoutInMills() {
        return Config.Default_So_Timeout;
    }

    @Override
    public String encodeFormat() {
        return Config.encodeFormat;
    }

    @Override
    public String model() {
        return Config.model;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // current do nothing.
    }

    public static final Creator<SyncNetConfigure> CREATOR = new Creator<SyncNetConfigure>() {

        @Override
        public SyncNetConfigure createFromParcel(Parcel source) {
            return new SyncNetConfigure(source);
        }

        @Override
        public SyncNetConfigure[] newArray(int size) {
            return new SyncNetConfigure[size];
        }

    };


    public static class Config {

        public static final String host = "http://data.tangcloud.com/login/android/new";//"http://data.tangcloud.com";

        public static final int Default_Connection_Timeout = 10 * 1000;

        public static final int Default_So_Timeout = 10 * 1000;

        public static final String encodeFormat = HTTP.UTF_8;

        public static final String model = TYSyncNetConfigure.MODEL_POST;
    }

}
