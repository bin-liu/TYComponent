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
package com.tangyu.component.demo.service.remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.format.DateFormat;
import android.util.Log;

import com.tangyu.component.service.remind.TYRemindData;
import com.tangyu.component.service.remind.TYRemindService;

/**
 * @author binliu on 12/31/13.
 */
public class TYRemindReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(context.getPackageName(), "broadcast receive !!!");
        if (null != intent && null != intent.getAction()) {
//            Log.v(context.getPackageName(), "action = " + intent.getAction());
            if (intent.getAction().equals(TYRemindServiceImpl.ACTION_REMIND)) {
                int command = intent.getIntExtra(TYRemindServiceImpl.INTENT_REMIND_COMMAND, TYRemindServiceImpl.CMD_REMIND_INVALID);
                boolean isMissing = intent.getBooleanExtra(TYRemindServiceImpl.INTENT_REMIND_DATA_IS_MISSING, false);
                byte[] bytes = intent.getByteArrayExtra(TYRemindServiceImpl.INTENT_REMIND_DATA_MARSHALLED);
                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(bytes, 0, bytes.length);
                parcel.setDataPosition(0);
                TYRemindData remindData = TYRemindData.CREATOR.createFromParcel(parcel);

                Log.v(context.getPackageName(), "i receive remind!!! command = " + command + "is missing[[" + isMissing + "]]");
                String stringRemindTime = DateFormat.format("kk:mm:ss", remindData.getmRemindTime()).toString();
                Log.v(context.getPackageName(), "-- remind time = " + (null == remindData ? " remind data is null" : stringRemindTime));

                if (!isMissing) {
                    Intent newIntent = new Intent(context, TYRemindServiceImpl.class);
                    newIntent.putExtra(TYRemindService.INTENT_REMIND_COMMAND, TYRemindService.CMD_REMIND_NEXT);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(newIntent);
                }
            }
        }
    }
}
