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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tangyu.component.service.remind.RemindWay;
import com.tangyu.component.service.remind.TYRemindData;
import com.tangyu.component.service.remind.TYRemindService;
import com.tangyu.component.service.remind.TYRemindServiceDataSource;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author binliu on 12/31/13.
 */
public class TYRemindServiceImpl extends TYRemindService {

    public final static String ACTION_REMIND = RemindWay.ACTION_WAKEUP + ".Test";

    TYRemindServiceDataSourceAndDelegateImpl dataSourceAndDelegate;

    TYRemindData.RemindDataUtil<RemindData> remindDataUtil;

//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public TYRemindServiceImpl(String name) {
//        super(name);
//    }

    @Override
    public TYRemindServiceDataSourceAble remindServiceDataSource() {
        return createIfNeed();
    }

    @Override
    public TYRemindServiceDelegateAble remindServiceDelegate() {
        return createIfNeed();
    }

    public TYRemindServiceDataSourceAndDelegateImpl createIfNeed() {
        return dataSourceAndDelegate == null ? dataSourceAndDelegate = new TYRemindServiceDataSourceAndDelegateImpl(this) : dataSourceAndDelegate;
    }

    public TYRemindData.RemindDataUtil<RemindData> createUtilIfNeed() {
        return remindDataUtil == null ? remindDataUtil = new TYRemindData.RemindDataUtil<RemindData>() : remindDataUtil;
    }

    public class TYRemindServiceDataSourceAndDelegateImpl
            extends TYRemindServiceDataSource<RemindData>
            implements TYRemindService.TYRemindServiceDelegateAble {

        public final Class DESTINY_CLASS = TYRemindReceiver.class;

        private List<RemindData> reminds;

        private Context context;

        private DataPersistLayer saver;

        public TYRemindServiceDataSourceAndDelegateImpl(Context context) {
            this.context = context;
        }

        @Override
        public List<RemindData> getAllRemind() {
            return reminds;
        }

        @Override
        public RemindData getNextRemind(long timeMills) {
            // please ensure reminds was sorted.

            Calendar baseline = Calendar.getInstance();
            baseline.setTimeInMillis(timeMills);
            RemindData next = createUtilIfNeed().filterNextRemindsPassingTest(reminds,
                    baseline, new TYRemindData.RemindDataUtil.PassingTest<RemindData>() {
                @Override
                public boolean passingTestStep(RemindData obj) {
                    return false;
                }
            });
            if (null != next) {
                output("find out next remind time = " + next.getmRemindTime() + " request base time = " + timeMills);
            } else {
                output("non next remind ");
            }
            return next;
        }

        @Override
        public Intent intentOfNotify(int remindId) {
            Intent intent = new Intent(context, DESTINY_CLASS);
            intent.setAction(ACTION_REMIND);
            intent.setExtrasClassLoader(RemindData.class.getClassLoader());
            return intent;
        }

        @Override
        public Intent[] intentOfCancelAll() {
            String[] actions = new String[]{ACTION_REMIND};

            Intent[] intents = new Intent[actions.length];
            for (int i = 0; i < actions.length; ++i) {
                intents[i] = new Intent(context, TYRemindService.class);
                intents[i].setAction(actions[i]);
            }
            return intents;
        }

        @Override
        public void willResponseCommand(int command) {
            reloadAllReminds(true);
            // Database operations.
            createSaveIfNeed().restoreStatus(reminds);

            output("will response command");
        }

        @Override
        public void didResponseCommand(int command) {
            // Database operations.
            createSaveIfNeed().saveStatus(reminds);

            output("did response command");
        }

        private void reloadAllReminds(boolean isForce) {
            if (isForce || null == reminds) {
                reminds = createSaveIfNeed().generalTestData();// as usually, you will to filter it base on some condition.
                // so you can coding as follow.
                TYRemindData.RemindDataUtil<RemindData> rdUtil = new TYRemindData.RemindDataUtil<RemindData>();
                reminds = rdUtil.filterToCalendarPassingTest(reminds, Calendar.getInstance(), new TYRemindData.RemindDataUtil.PassingTest<RemindData>() {
                    @Override
                    public boolean passingTestStep(RemindData obj) {
                        // Here to add your conditions.
                        // return true means that will not add to result.
                        //
                        return false;
                    }
                });
            }
            Collections.sort(reminds, RemindData.COMPARATOR_FOR_REMIND_TIME);
        }

        private DataPersistLayer createSaveIfNeed() {
            return saver == null ? saver = new DataPersistLayer(context) : saver;
        }
    }
}
