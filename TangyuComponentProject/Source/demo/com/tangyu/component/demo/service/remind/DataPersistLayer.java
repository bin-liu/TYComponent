package com.tangyu.component.demo.service.remind;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * The best way is save data to DB. And pass {@link android.content.ContentProvider} to communicate with it.<br>
 * It's avoiding some big question which is happen multiple threads or process read same db file.<br>
 * I will change to use {@link android.content.ContentProvider} it in follow update.
 * @author binliu on 1/8/14.
 */
public class DataPersistLayer {

    public final String KEY_PREFS_BASELINE = "baseline";

    public List<RemindData> generalTestData() {

        final long currentTimeMills = System.currentTimeMillis();
        final long baseline = prefs.getLong(KEY_PREFS_BASELINE, currentTimeMills);
        List<RemindData> result = new LinkedList<RemindData>();
        Calendar baselineCalendar = Calendar.getInstance();
        for (int i = 0; i <= 10; i++) {
            baselineCalendar.setTimeInMillis(baseline);
            RemindData data = new RemindData();
            baselineCalendar.add(Calendar.MINUTE, i - 5);
            data.setmRemindTime(baselineCalendar.getTimeInMillis());
            data.setmRemindId(i);
            result.add(data);
        }
        if (currentTimeMills == baseline) {
            SharedPreferences.Editor editor = prefs.edit();
            if (null != editor) {
                editor.putLong(KEY_PREFS_BASELINE, baseline);
                editor.commit();
            }
        }
        return result;
    }

    SharedPreferences prefs;
    public DataPersistLayer(Context context) {
        prefs = context.getSharedPreferences("status-saver", Context.MODE_PRIVATE);
    }

    /**
     * the best way is save data to DB. And pass {@link android.content.ContentProvider} to communicate with it.<br>
     *
     * it's avoiding some big question which is happen multiple threads or process read same db file.
     * @param remindDatas
     */
    public void saveStatus(List<RemindData> remindDatas){
        SharedPreferences.Editor editor = prefs.edit();
        if (editor != null) {
            for (RemindData rd : remindDatas) {
                editor.putInt("" + rd.getmRemindId(), rd.getmRemindState());
            }
        }
        editor.commit();
    }

    /**
     * the best way is save data to DB. And pass {@link android.content.ContentProvider} to communicate with it.<br>
     *
     * it's avoiding some big question which is happen multiple threads or process read same db file.
     * @param remindDatas
     */
    public void restoreStatus(List<RemindData> remindDatas) {
        for (RemindData data : remindDatas) {
            int status = prefs.getInt(data.getmRemindId() + "", RemindData.REMIND_STATE_INVALID);
            if (status != RemindData.REMIND_STATE_INVALID) {
                data.setmRemindState(status);
            }
        }
    }

    public void reset() {
        SharedPreferences.Editor editor = prefs.edit();
        if (null != editor) {
            editor.remove(KEY_PREFS_BASELINE);
            editor.commit();
        }
    }

    public boolean isInitState() {
        return prefs.getLong(KEY_PREFS_BASELINE, -1) == -1;
    }
}
