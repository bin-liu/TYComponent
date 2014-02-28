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

import java.util.LinkedList;
import java.util.List;

/**
 * @author binliu on 12/30/13.
 */
public abstract class TYRemindServiceDataSource<T extends TYRemindData> implements TYRemindService.TYRemindServiceDataSourceAble{

    @Override
    public void cancelAll() {
    }

    @Override
    public T next(long currentTime) {
        T nextRemind = getNextRemind(currentTime);
        if (null == nextRemind) return null;
        nextRemind.setmRemindState(TYRemindData.REMIND_STATE_REMINDDING);
        return nextRemind;
    }

    @Override
    public List<T> missing(TYRemindData nextRemind, long remindTime) {
        if (null == nextRemind) return null;
        List<T> reminds = getAllRemind();
        if (isEmptyList(reminds)) return null;
        List<T> missing = new LinkedList<T>();
        for (T remind : reminds) {
            if (remind.equals(nextRemind)) continue;
            if (remind.mEnable && remind.mRemindTime <= remindTime &&
                    !remind.getmUUID().equals(nextRemind.getmUUID()) &&
                    remind.mRemindState == TYRemindData.REMIND_STATE_UNREMIND) {
                remind.setmRemindState(TYRemindData.REMIND_STATE_REMINDED);
                missing.add(remind);
            }
        }
        return missing;
    }

    @Override
    public void focusToRollAllRemindStatusToPropertyBaseOnTime(TYRemindData nextRemind) {
        if (null == nextRemind) return;
        List<T> reminds = getAllRemind();
        if (isEmptyList(reminds)) return ;
        for (T remind : reminds) {
            if (remind.equals(nextRemind)) continue;
            if (remind.getmRemindTime() < nextRemind.getmRemindTime()) {
                remind.setmRemindState(TYRemindData.REMIND_STATE_REMINDED);
            } else {
                remind.setmRemindState(TYRemindData.REMIND_STATE_UNREMIND);
            }
        }
    }

    @Override
    public void focusToRollRemindingStatusToPropertyIfNeed(TYRemindData nextRemind) {
        List<T> reminds = getAllRemind();
        if (isEmptyList(reminds)) return ;
        for (T remind : reminds) {
            if (null != nextRemind && remind.equals(nextRemind)) continue;
            if (remind.getmRemindState() == TYRemindData.REMIND_STATE_REMINDDING) {
                if (null == nextRemind || remind.getmRemindTime() <= nextRemind.getmRemindTime()) {
                    remind.setmRemindState(TYRemindData.REMIND_STATE_REMINDED);
                } else {
                    remind.setmRemindState(TYRemindData.REMIND_STATE_UNREMIND);
                }
            }
        }
    }

    public boolean isEmptyList(List list) {
        return null == list || list.isEmpty();
    }

    public abstract List<T> getAllRemind();

    public abstract T getNextRemind(long timeMills);

}
