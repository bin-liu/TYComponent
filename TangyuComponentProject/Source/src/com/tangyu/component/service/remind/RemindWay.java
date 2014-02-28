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
package com.tangyu.component.service.remind;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;

import java.util.Calendar;

/**
 * @author binliu
 */
public class RemindWay {

    public static final String APP_NAME = "com.tangyu.app";
    public final static String ACT_REMIND_NEWDAY = ".NEWDAY_REMIND";
    public final static String ACT_REMIND_WAKEUP = ".WAKEUP";

    public final static String ACTION_NEWDAY_REMIND = APP_NAME + ACT_REMIND_NEWDAY;

    public final static String ACTION_WAKEUP = APP_NAME + ACT_REMIND_WAKEUP;

    private Context mCtx;
    private NotificationManager mNotify;
    private AlarmManager mAlarm;
    private Vibrator mVibrator;
    private PowerManager mPower;
    private WakeLock mNewWakeLock;

    public RemindWay(Context context) {
        mCtx = context;
        mNotify = (NotificationManager) mCtx
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarm = (AlarmManager) mCtx.getSystemService(Context.ALARM_SERVICE);
        mVibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        mPower = (PowerManager) mCtx.getSystemService(Context.POWER_SERVICE);
        mNewWakeLock = mPower.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tangyu");
    }

    public void notify(int id, Notification notification) {
        if (notification == null) throw new NullPointerException("param notification should not be null");
        mNotify.notify(id, notification);
    }

    public void notify(Intent rIntent, Class destinyCls, NotifyMsg msg, int id) {
        if (rIntent == null) throw new NullPointerException("intent is null");
        if (destinyCls == null) throw new NullPointerException("destiny class is null");
        if (msg == null) throw new NullPointerException("msg is null");

        Intent intent = rIntent.cloneFilter();
        intent.putExtras(rIntent);
        intent.setClass(mCtx, destinyCls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(mCtx, 0, intent, 0);

		/* *
         * modify by: bin time: 2012-9-12 reason: Builder is need more than API
		 * 11
		 * 
		 * Builder builder = new Notification.Builder(mContext);
		 * builder.setTicker(tickerText); builder.setContentTitle(contentTitle);
		 * builder.setContentText(contentText); builder.setSmallIcon(icon);
		 * builder.setWhen(when); builder.setDefaults(Notification.DEFAULT_ALL);
		 * builder.setContentIntent(pi); Notification notification =
		 * builder.getNotification();
		 */

        // ----modify start
        Notification notification = new Notification(msg.icon, msg.tickerText, msg.when);
        notification.defaults = Notification.DEFAULT_ALL;
        notification.setLatestEventInfo(mCtx, msg.contentTitle, msg.contentText, pi);
        // ----modify end

        mNotify.notify(id, notification);
    }

    public void notify(Intent rIntent, Class destinyCls, NotifyMsg msg, int id, Notification notification) {
        if (rIntent == null) throw new NullPointerException("intent is null");
        if (destinyCls == null) throw new NullPointerException("destiny class is null");
        if (msg == null) throw new NullPointerException("msg is null");

        Intent intent = rIntent.cloneFilter();
        intent.putExtras(rIntent);
        intent.setClass(mCtx, destinyCls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(mCtx, 0, intent, 0);
		
		/* *
		 * modify by: bin time: 2012-9-12 reason: Builder is need more than API
		 * 11
		 * 
		 * Builder builder = new Notification.Builder(mContext);
		 * builder.setTicker(tickerText); builder.setContentTitle(contentTitle);
		 * builder.setContentText(contentText); builder.setSmallIcon(icon);
		 * builder.setWhen(when); builder.setDefaults(Notification.DEFAULT_ALL);
		 * builder.setContentIntent(pi); Notification notification =
		 * builder.getNotification();
		 */

        // ----modify start
        if (notification == null) {
            notification = new Notification(msg.icon, msg.tickerText, msg.when);
            notification.defaults = Notification.DEFAULT_ALL;
        }
        notification.setLatestEventInfo(mCtx, msg.contentTitle, msg.contentText, pi);
        // ----modify end

        mNotify.notify(id, notification);
    }

    public void alarm(Intent intent, Calendar c) {
        PendingIntent pi = PendingIntent.getBroadcast(mCtx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);// .FLAG_ONE_SHOT);
        mAlarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }

    public void alarm(Intent intent, long timeMills) {
        PendingIntent pi = PendingIntent.getBroadcast(mCtx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);// .FLAG_ONE_SHOT);
        mAlarm.set(AlarmManager.RTC_WAKEUP, timeMills, pi);
    }

    public void wakeup() {
        if (mNewWakeLock != null) {
            mNewWakeLock.acquire();
        }
    }

    public void cancelNofity(int id) {
        if (mNotify != null) {
            mNotify.cancel(id);
        }
    }

    public void cancelAll(Intent[] intents) {
        // if (mNotify != null) {
        // mNotify.cancelAll();
        // }
        // action, data, type, class, and categories
        PendingIntent operation = null;
        for (Intent intent : intents) {
            operation = PendingIntent.getBroadcast(mCtx, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);// .FLAG_ONE_SHOT);
            mAlarm.cancel(operation);
        }
    }

    public static class NotifyMsg {
        public int icon = 0;
        public String tickerText = "", contentTitle = "", contentText = "";
        public long when = 0;

    }
}
