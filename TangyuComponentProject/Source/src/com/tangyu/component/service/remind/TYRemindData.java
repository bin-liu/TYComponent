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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * The data of remind service.<br>
 * Demo : {@link com.tangyu.component.demo.service.remind.RemindData}
 * @author bin
 */
public class TYRemindData implements Parcelable {

    public static final String TABCOL_ID = "_id";
    public static final String TABCOL_TIME = "time";
    public static final String TABCOL_REMINDSTATE = "REMIND_STATE";
    public static final String TABCOL_ENABLE = "enable";
    public static final String TABCOL_UUID = "uuid";

    public static final int REMIND_STATE_REMINDED = -1;
    public static final int REMIND_STATE_UNREMIND = 0;
    public static final int REMIND_STATE_REMINDDING = 1;
    public static final int REMIND_STATE_INVALID = -2;

    public static boolean isNull(List<?> list) {
        return list == null || list.size() == 0 ? true : false;
    }

    public static Comparator<? super TYRemindData> COMPARATOR_FOR_REMIND_TIME = new Comparator<TYRemindData>() {
        @Override
        public int compare(TYRemindData lhs, TYRemindData rhs) {
            return lhs.getmRemindTime() == rhs.getmRemindTime() ? 0 : lhs.getmRemindTime() < rhs.getmRemindTime() ? -1 : 1;
        }
    };

    public static void sort(List<? extends TYRemindData> list) {
        if (isNull(list)) return;
        Collections.sort(list, COMPARATOR_FOR_REMIND_TIME);
    }

    protected int mRemindId;
    protected long mRemindTime;
    protected int mRemindState;
    protected boolean mEnable = true;
    protected String mUUID = UUID.randomUUID().toString();

    protected TYRemindData() {

    }

    protected TYRemindData(TYRemindData r) {
        mRemindId = r.mRemindId;
        mRemindTime = r.mRemindTime;
        mRemindState = r.mRemindState;
        mEnable = r.mEnable;
        mUUID = r.mUUID;
    }

    protected TYRemindData(Parcel in) {
        mRemindId = in.readInt();
        mRemindTime = in.readLong();
        mRemindState = in.readInt();
        mEnable = in.readInt() == 0 ? false : true;
        mUUID = in.readString();
    }

    public int getmRemindId() {
        return mRemindId;
    }

    public void setmRemindId(int mRemindId) {
        this.mRemindId = mRemindId;
    }

    public long getmRemindTime() {
        return mRemindTime;
    }

    public void setmRemindTime(long mRemindTime) {
        this.mRemindTime = mRemindTime;
    }

    public int getmRemindState() {
        return mRemindState;
    }

    public void setmRemindState(int mRemindState) {
        this.mRemindState = mRemindState;
    }

    public boolean ismEnable() {
        return mEnable;
    }

    public void setmEnable(boolean mEnable) {
        this.mEnable = mEnable;
    }

    public String getmUUID() {
        return mUUID;
    }

    public void setmUUID(String mUUID) {
        this.mUUID = mUUID;
    }

    @Override
    public boolean equals(Object o) {
        TYRemindData data = (TYRemindData) o;
        if (mRemindId == data.mRemindId && mRemindTime == data.mRemindTime &&
                mRemindState == data.mRemindState && mEnable == data.mEnable &&
                mUUID.equals(data.mUUID)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 37 * res + mRemindId;
        res = 37 * res + (int) (mRemindTime ^ mRemindTime >>> 32);
        res = 37 * res + mRemindState;
        res = 37 * res + (mEnable ? 0 : 1);
        res = 37 * res + mUUID.hashCode();
        return res;
    }

    public void clone(TYRemindData r) {
        r.mRemindId = mRemindId;
        r.mRemindState = mRemindState;
        r.mRemindTime = mRemindTime;
        r.mEnable = mEnable;
        r.mUUID = mUUID;
    }

    public void copyFrom(Object obj) {
        if (obj instanceof TYRemindData) {
            TYRemindData source = (TYRemindData) obj;
            mRemindId = source.mRemindId;
            mRemindState = source.mRemindState;
            mRemindTime = source.mRemindTime;
            mEnable = source.mEnable;
            mUUID = source.mUUID;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRemindId);
        dest.writeLong(mRemindTime);
        dest.writeInt(mRemindState);
        dest.writeInt(mEnable ? 1 : 0);
        dest.writeString(mUUID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TYRemindData> CREATOR = new Creator<TYRemindData>() {

        @Override
        public TYRemindData createFromParcel(Parcel source) {
            return new TYRemindData(source);
        }

        @Override
        public TYRemindData[] newArray(int size) {
            return new TYRemindData[size];
        }

    };

    @Override
    public String toString() {
        return "[ID = " + mRemindId + "][Time = " + mRemindTime +
                "][RemindState =" + mRemindState + "][enable = " + mEnable +
                "][UUID = " + mUUID + "]";
    }

    public boolean isSameData(TYRemindData other) {
        if (other == null) return false;
        return RemindDataUtil.isSameDate(mRemindTime, other.mRemindTime);
    }

    public boolean isCompletedState() {
        return mRemindState == REMIND_STATE_REMINDED;
    }

    public static class RemindDataUtil<T extends TYRemindData> {

        /**
         * is same date.
         *
         * @param c1
         * @param c2
         * @return
         */
        public static boolean isSameDate(Calendar c1, Calendar c2) {
            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                    c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                    c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
                return true;
            }
            return false;
        }

        public static boolean isSameDate(long timeMills1, long timeMills2) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(timeMills1);
            c2.setTimeInMillis(timeMills2);
            return isSameDate(c1, c2);
        }

        public static Calendar toDate(Calendar source, Calendar destiny) {
            Calendar res = (Calendar) source.clone();
            res.set(Calendar.YEAR, destiny.get(Calendar.YEAR));
            res.set(Calendar.MONTH, destiny.get(Calendar.MONTH));
            res.set(Calendar.DATE, destiny.get(Calendar.DATE));
            return res;
        }

        public final int indexOf(List<T> source, TYRemindData target) {
            if (!isNull(source) && !TextUtils.isEmpty(target.getmUUID())) {
                for (int i = 0; i < source.size(); ++i) {
                    TYRemindData e = source.get(i);
                    if (TextUtils.isEmpty(e.getmUUID())) continue;
                    if (target.getmUUID().equals(e.getmUUID())) return i;
                }
            }
            return -1;
        }

        public boolean isAllCompleted(List<T> source) {
            if (null != source) {
                for (T remind : source) {
                    if (!remind.isCompletedState()) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * find out next remind data.
         * @param reminds be sorted by remind time.
         * @param c2 base time line.
         * @return the data that later than param c2. if null means not found.
         * @see com.tangyu.component.service.remind.TYRemindData#COMPARATOR_FOR_REMIND_TIME
         */
        public T filterNextRemindsPassingTest(List<T> reminds,
                                              Calendar c2,
                                              PassingTest<T> passing) {
            if (isNull(reminds)) return null;
            final long baseline = c2.getTimeInMillis();
            for (T rd : reminds) {
                if (!rd.ismEnable()) continue;
                boolean isPass = null != passing ? passing.passingTestStep(rd) : true;
                if (!isPass) continue;
                if (rd.getmRemindTime() >= baseline) {
                    return rd;
                }
            }
            return null;
        }

        /**
         * change the remind time of reminds to destiny date.
         * @param reminds
         * @param destiny
         * @param passing
         * @return
         */
        public List<T> filterToCalendarPassingTest(List<T> reminds,
                                                   Calendar destiny,
                                                   PassingTest<T> passing) {
            List<T> result = new LinkedList<T>();
            if (!isNull(reminds)) {
                for (T rd : reminds) {
                    boolean isPass = null != passing ? passing.passingTestStep(rd) : true;
                    if (!isPass) continue;
                    Calendar c1 = Calendar.getInstance();
                    c1.setTimeInMillis(rd.getmRemindTime());
                    rd.setmRemindTime(toDate(c1, destiny).getTimeInMillis());
                    result.add(rd);
                }
            }
            return result;
        }

        public static interface PassingTest<T extends TYRemindData> {
            /**
             * will to be invoked in loop every step.
             * @param obj
             * @return if false, the param will not add to result.
             */
            public boolean passingTestStep(T obj);
        }
    }

}
