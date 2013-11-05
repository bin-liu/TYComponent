/**
 * The MIT License (MIT)
 * Copyright (c) 2012  Tang Yu Software Corporation
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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

/**
 * @author bin
 */
public class Util {

    public static String Log_Tag = "TangYu";

    private static BroadcastReceiver sdCardReceiver;
    private static boolean isSDCardReceiverRegister = false;
    private static boolean isSDCardAvailable = false;
    private static boolean isSDCardWriteable = false;
    private static Context saveContext;

    public static void v(String msg) {
        Log.v(Log_Tag, msg);
    }

    public static void e(String msg) {
        Log.e(Log_Tag, msg);
    }

    public static void toast(Context ctx, String msg, boolean isShort) {
        Toast.makeText(ctx, msg,
                isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void toast(Context ctx, int msgid, boolean isShort) {
        Toast.makeText(ctx, msgid,
                isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static boolean isNull(Object o) {
        return o == null ? true : false;
    }

    public static boolean isNull(List<?> list) {
        return list == null || list.size() == 0 ? true : false;
    }

    public static boolean isNull(String str) {
        return TextUtils.isEmpty(str) ? true : false;
    }

    /**
     * @param src
     * @param key
     * @return >= 0 is in array. negative value means not.
     */
    public static int isInArray(int[] src, int key) {
        if (isNull(src)) return -1;
        for (int i = 0; i < src.length; i++) {
            if (src[i] == key) {
                return i;
            }
        }
        return -1;
    }

    /**
     * find param2 in param1
     *
     * @param src
     * @param key
     * @return if return positive value. that is index in array. negative value means not.
     */
    public static int isInList(List<? extends Object> src, Object key) {
        if (isNull(src)) return -1;
        if (isNull(key)) return -1;
        for (int i = 0; i < src.size(); i++) {
            if (isNull(src.get(i))) continue;
            if (src.get(i).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * parameter 2 is contain in parameter 1.
     *
     * @param sourceFlag
     * @param compareFlag
     * @return
     */
    public static boolean isFlagContain(int sourceFlag, int compareFlag) {
        return (sourceFlag & compareFlag) == compareFlag;
    }

    /**
     * Whether show StatueBar or not.
     *
     * @param active  in which Activity
     * @param visible View.VISIBLE is show, otherwise is dismiss
     */
    public static void statueBarVisible(Activity active, final int visible) {
        if (visible == View.VISIBLE) {
            active.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            active.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    /**
     * check SD card is available. then u may be call toastShort to communicate
     * with user.
     *
     * @return <b>true</b> the SD card is available. <b>false</b> not.
     */
    public static boolean sdcardIsOnline() {
        final String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ? true : false;
    }

    /**
     * update the isSDCardAvailable and isSDCardWriteable state
     */
    private static void sdCardUpdateState(Context context) {
        final String sdCardState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdCardState)) {
            isSDCardAvailable = isSDCardWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCardState)) {
            isSDCardAvailable = true;
            isSDCardWriteable = false;
        } else {
            isSDCardAvailable = isSDCardWriteable = false;
        }
    }

    /**
     * open the SD card state listener. Generally call it in "onStart" method.
     */
    public static synchronized void sdCardStartListener(Context context,
                                                        sdcardListener lis) {
        if (saveContext != null && saveContext != context) {
            sdCardStopListener(saveContext);
        }
        mSdcardListener = lis;
        saveContext = context;
        sdCardReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sdCardUpdateState(context);
                if (mSdcardListener != null)
                    mSdcardListener.onReceiver(isSDCardAvailable,
                            isSDCardWriteable);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        if (!isSDCardReceiverRegister) {
            context.registerReceiver(sdCardReceiver, filter);
            isSDCardReceiverRegister = true;
        }
        sdCardUpdateState(context);
    }

    /**
     * close the SD card state listener. Generally call it in "onStart" method.
     */
    public static synchronized void sdCardStopListener(Context context) {
        if (isSDCardReceiverRegister && saveContext == context) {
            context.unregisterReceiver(sdCardReceiver);
            isSDCardReceiverRegister = false;
            mSdcardListener = null;
        }
    }

    public static sdcardListener mSdcardListener;

    public interface sdcardListener {
        void onReceiver(boolean isAvailable, boolean isCanWrite);
    }

    public static void sysSetActionBness(Activity action, float bness) {
        WindowManager.LayoutParams lp = action.getWindow().getAttributes();
        lp.screenBrightness = bness;
        action.getWindow().setAttributes(lp);
    }

    public static float sysGetActionBness(Activity action) {
        return action.getWindow().getAttributes().screenBrightness;
    }

    public static void sysIsLockScreen(Activity act, boolean isLock) {
        if (isLock) {
            switch (act.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }
        } else {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo() != null) {
            return cm.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = lm.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static boolean is3rd(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static class DensityUtil {

        /**
         * dp to px
         */
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        /**
         * px to dp
         */
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }

}
