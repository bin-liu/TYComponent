package com.tangyu.component.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author bin
 */
public class TimerStep {

    private Timer mTimer;
    private int mCurrentCount;

    private synchronized void add() {
        ++mCurrentCount;
    }

    private synchronized void remove() {
        --mCurrentCount;
    }

    public synchronized void startTimer(long delay, final ITimerListener lis) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                remove();
                if (lis != null) {
                    lis.timesUp();
                    if (mCurrentCount <= 0) {
                        lis.over();
                    }
                }
            }
        }, delay);

        add();
    }

    public synchronized void cancel() {
        if (mTimer != null && mCurrentCount > 0) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public interface ITimerListener {
        void timesUp();

        void over();
    }

}
