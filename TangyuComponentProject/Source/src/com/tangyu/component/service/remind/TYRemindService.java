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
package com.tangyu.component.service.remind;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.util.Log;

import java.util.List;


/**
 * Demo: {@link com.tangyu.component.demo.service.remind.TYRemindServiceImpl}
 * @author binliu on 12/30/13.
 */
public abstract class TYRemindService extends IntentService {

    /**
     * Cancel current remind. you should be set to intent which is use to start service.
     */
    public static final int CMD_REMIND_CANCEL = 10000000;

    /**
     * find next remind service. you should be set to intent which is use to start service.
     */
    public static final int CMD_REMIND_NEXT = 10000001;

    /**
     * Reschedule remind service. you should be set to intent which is use to start service.
     */
    public static final int CMD_REMIND_RESCHEDULE = 10000002;

    /**
     * The flag of Invalid command.
     */
    public static final int CMD_REMIND_INVALID = -1;

    /**
     * The command of start service.
     */
    public static final String INTENT_REMIND_COMMAND = "INTENT_REMIND_COMMAND";

    /**
     * @deprecated
     * The data of send to BroadCastReceiver.
     */
    public static final String INTENT_REMIND_DATA = "INTENT_REMIND_DATA";

    /**
     * The data of send to BroadCastReceiver. It was marshalled.<br>
     * invoking {@link android.content.Intent#getByteArrayExtra(String)} to get byteArray object when broadcast received.<br>
     *
     * sample code:<br>
     * <pre>
     *     Byte[] byteArrayExtra = intent.getByteArrayExtra(INTENT_REMIND_DATA_MARSHALLED);
     *     Parcel parcel = Parcel.obtain();
     *     parcel.unmarshall(byteArrayExtra, 0, byteArrayExtra.length);
     *     parcel.setDataPosition(0);
     *     TYRemindData remind = TYRemindData.CREATOR.createFromParcel(parcel);
     * </pre>
     *
     * @see android.os.Parcel#marshall()
     */
    public static final String INTENT_REMIND_DATA_MARSHALLED = "INTENT_REMIND_DATA_MARSHALLED";

    /**
     * Whether the properties remind data is missing or not.
     */
    public static final String INTENT_REMIND_DATA_IS_MISSING = "INTENT_REMIND_DATA_IS_MISSING";

    /**
     * whether should stop service in {@link android.app.Service#onStartCommand(android.content.Intent, int, int)} or not.<br>
     * default is stop.
     */
    public static final String INTENT_SERVICE_FOCUSES_STOP = "INTENT_SERVICE_FOCUSES_STOP";

    /**
     * log print controller.
     */
    private static final boolean isOutput = true;

    /**
     * data source
     */
    private TYRemindServiceDataSourceAble mDataSource;

    /**
     * delegate
     */
    private TYRemindServiceDelegateAble mDelegate;

    /**
     * use to communicate with binder activity.
     */
    protected final Messenger mMessenger = new Messenger(new CommandHandler());

    /**
     * The notification encapsulated class.
     */
    protected RemindWay mRemindWay;

    protected AlarmManager mAlarm;

    public TYRemindService() {
        this(TYRemindService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TYRemindService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDataSource = remindServiceDataSource();
        mDelegate = remindServiceDelegate();
        mRemindWay = new RemindWay(this);
        mAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        output("service start!!!!! PID = " + android.os.Process.myPid() + " | request command = " + intent.getIntExtra(INTENT_REMIND_COMMAND, CMD_REMIND_INVALID));
        if (null != intent && intent.hasExtra(INTENT_REMIND_COMMAND)) {
            int command = intent.getIntExtra(INTENT_REMIND_COMMAND, CMD_REMIND_INVALID);
            responseCommand(command);
        }

    }

    private void responseCommand(int command) {
        if (CMD_REMIND_INVALID != command) {
            mDelegate.willResponseCommand(command);
        }
        switch (command) {
            case CMD_REMIND_CANCEL:
                cancelAllRemind();
                break;
            case CMD_REMIND_RESCHEDULE:
            {
                // 1. cancel current
                cancelAllRemind();
                final long currentTime = System.currentTimeMillis();
                // 2. find and notify next
                TYRemindData nextRemind = mDataSource.next(currentTime);
                if (null != nextRemind) {
                    //3. focus to roll all remind to property
                    mDataSource.focusToRollAllRemindStatusToPropertyBaseOnTime(nextRemind);
                    //4. notify next
                    notifyRemind(nextRemind, nextRemind.getmRemindTime(), false);
                }
            }
                break;

            case CMD_REMIND_NEXT:
            {
                // 1. cancel current
                cancelAllRemind();
                final long currentTime = System.currentTimeMillis();
                // 2. find next
                TYRemindData nextRemind = mDataSource.next(currentTime);
                // 3. focus to roll remind status to property which is Reminding.
                mDataSource.focusToRollRemindingStatusToPropertyIfNeed(nextRemind);

                if (null != nextRemind) {
                    final long notifyTime = nextRemind.getmRemindTime();

                    // 4. find and notify missing if need
                    List<TYRemindData> missingReminds = mDataSource.missing(nextRemind, notifyTime);
                    if (null != missingReminds) {
                        for (TYRemindData missing : missingReminds) {
                            notifyRemind(missing, currentTime, true);
                        }
                    }

                    // 5. notify next.
                    notifyRemind(nextRemind, notifyTime, false);
                }
            }
                break;
        }
        if (CMD_REMIND_INVALID != command) {
            mDelegate.didResponseCommand(command);
        }
    }

    /**
     * cancel all remind
     */
    protected final void cancelAllRemind() {
        mRemindWay.cancelAll(mDataSource.intentOfCancelAll());
        mDataSource.cancelAll();
    }

    /**
     * notify remind
     * @param lastRemind
     */
    private void notifyRemind(TYRemindData lastRemind, long notifyTime, boolean isMissingRemind) {
        if (null == lastRemind || notifyTime <= 0) return;
        Intent it = mDataSource.intentOfNotify(lastRemind.getmRemindId());

        // HERE
        // if you code like this
        // "
        // it.putExtra(INTENT_REMIND_DATA, lastRemind);
        // "
        // that will be get error: "E/Parcel: Class not found when unmarshalling: bala-bala W/Intent: Failure filling in extras bala-bala"
        // see http://nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/

        Parcel parcel = Parcel.obtain();
        lastRemind.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        it.putExtra(INTENT_REMIND_DATA_MARSHALLED, parcel.marshall());
        it.putExtra(INTENT_REMIND_COMMAND, CMD_REMIND_RESCHEDULE);
        it.putExtra(INTENT_REMIND_DATA_IS_MISSING, isMissingRemind);

//        mRemindWay.alarm(it, notifyTime);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, it,
                PendingIntent.FLAG_ONE_SHOT);
        mAlarm.set(AlarmManager.RTC_WAKEUP, notifyTime, pi);
    }

    protected void output(String msg) {
        if (isOutput) Log.v(getPackageName(), msg);
    }

    /**
     * get the data source from subclass
     * @return
     */
    public abstract TYRemindServiceDataSourceAble remindServiceDataSource();

    /**
     * get the delegate from subclass
     * @return
     */
    public abstract TYRemindServiceDelegateAble remindServiceDelegate();

    public static interface TYRemindServiceDataSourceAble<T extends TYRemindData> {

        /**
         * cancel all remind which is running.
         */
        public void cancelAll();

        /**
         * find out next remind base on param of currentTime.
         * subclass will be change {@link com.tangyu.component.service.remind.TYRemindData#mRemindState} from
         * {@link com.tangyu.component.service.remind.TYRemindData#REMIND_STATE_UNREMIND} to
         * {@link com.tangyu.component.service.remind.TYRemindData#REMIND_STATE_REMINDDING}<br>
         * @param currentTime
         * @return
         */
        public T next(long currentTime);

        /**
         * Find out missing remind.<br>
         * subclass will be change {@link com.tangyu.component.service.remind.TYRemindData#mRemindState} from
         * {@link com.tangyu.component.service.remind.TYRemindData#REMIND_STATE_UNREMIND} to
         * {@link com.tangyu.component.service.remind.TYRemindData#REMIND_STATE_REMINDED}<br>
         *
         * @param nextRemind
         * @param remindTime
         * @return
         */
        public List<T> missing(T nextRemind, long remindTime);

        /**
         * change the status from Reminding to UnRemind or Reminded.
         * @param nextRemind
         */
        public void focusToRollRemindingStatusToPropertyIfNeed(T nextRemind);

        /**
         * change all remind status.
         * @param nextRemind
         */
        public void focusToRollAllRemindStatusToPropertyBaseOnTime(T nextRemind);

        /**
         * The intent which will be notify.<br>
         *
         * @param remindId
         * @return
         */
        public Intent intentOfNotify(int remindId);

        /**
         * The intent which will be cancel.<br></>
         * The rules is base on {@link Intent#filterEquals(android.content.Intent)}
         *
         * @return
         */
        public Intent[] intentOfCancelAll();
    }

    public static interface TYRemindServiceDelegateAble {

        /**
         * This will be invoked in {@link android.app.Service#onStartCommand(android.content.Intent, int, int)} on work thread, before response “command”
         * <p>In this stage, you can do something like load and filter remind data. and back up the remind status of them.
         *
         * @param command
         */
        public void willResponseCommand(int command);

        /**
         * This will be invoked in {@link android.app.Service#onStartCommand(android.content.Intent, int, int)} on work thread, after response “command”
         * <p>In this stage, you can save status of remind data to file or database.
         *
         * @param command
         */
        public void didResponseCommand(int command);
    }

    public class CommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            responseCommand(msg.what);
            super.handleMessage(msg);
        }
    }

}