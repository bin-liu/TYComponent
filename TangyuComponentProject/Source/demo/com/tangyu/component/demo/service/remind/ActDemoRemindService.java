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
package com.tangyu.component.demo.service.remind;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tangyu.component.R;
import com.tangyu.component.Util;
import com.tangyu.component.service.remind.TYRemindData;
import com.tangyu.component.service.remind.TYRemindService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author binliu on 12/31/13.
 */
public class ActDemoRemindService extends Activity implements View.OnClickListener {

    private TextView mVMsg;

    private Button mVBtnStart, mVBtnStop, mVBtnReset;

    private DataPersistLayer mDataPersistLayer;

    private TYRemindData.RemindDataUtil mRemindDataUtil;

    public final int CMD_INIT = -1;
    public final int CMD_START = 0;
    public final int CMD_STOP = 1;
    public final int CMD_RESET = 2;

    private final Map<Integer, Integer> mMapIDWithCMD = new HashMap<Integer, Integer>();

    private Handler mRefreshRequestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (createDataPersistIfNeed().isInitState()) {
                mVMsg.setText("preparing");
            } else {
                StringBuilder sb = new StringBuilder("current : " + DateFormat.format("kk:mm:ss", System.currentTimeMillis()) + "\n\n") ;
                List<RemindData> data = createDataPersistIfNeed().generalTestData();
                createDataPersistIfNeed().restoreStatus(data);
                if (!Util.isNull(data)) {
                    for (RemindData item : data) {
                        sb.append(item.toString() + "\n");
                    }
                }

                if (createRemindDataUtilIfNeed().isAllCompleted(data)) {
                    sb.append("Complete!!!!");
                }

                mVMsg.setText(sb);
            }

            mRefreshRequestHandler.removeMessages(0);
            mRefreshRequestHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_remind_service);

        mVMsg = (TextView) findViewById(R.id.demo_remind_service_show_msg);
        mVBtnReset = (Button) findViewById(R.id.demo_remind_service_reset_btn);
        mVBtnStart = (Button) findViewById(R.id.demo_remind_service_start);
        mVBtnStop = (Button) findViewById(R.id.demo_remind_service_stop);

        mVBtnReset.setOnClickListener(this);
        mVBtnStart.setOnClickListener(this);
        mVBtnStop.setOnClickListener(this);

        changeButtonStats(CMD_INIT);

        mMapIDWithCMD.put(R.id.demo_remind_service_start, CMD_START);
        mMapIDWithCMD.put(R.id.demo_remind_service_stop, CMD_STOP);
        mMapIDWithCMD.put(R.id.demo_remind_service_reset_btn, CMD_RESET);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private DataPersistLayer createDataPersistIfNeed() {
        return mDataPersistLayer == null ? mDataPersistLayer = new DataPersistLayer(this) : mDataPersistLayer;
    }

    private TYRemindData.RemindDataUtil createRemindDataUtilIfNeed() {
        return mRemindDataUtil == null ? mRemindDataUtil = new TYRemindData.RemindDataUtil<RemindData>() : mRemindDataUtil;
    }

    @Override
    public void onClick(View v) {

        int command = mMapIDWithCMD.get(v.getId());
        int commandOfService = changeButtonStats(command);

        Intent intent = new Intent(this, TYRemindServiceImpl.class);
        intent.putExtra(TYRemindService.INTENT_REMIND_COMMAND, commandOfService);
//        intent.putExtra(TYRemindService.INTENT_SERVICE_FOCUSES_STOP, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }

    public int changeButtonStats(int command) {
        boolean[] status = null;
        int commandOfService = 0;
        switch (command) {
            case CMD_INIT:
                status = new boolean[]{true, false, false};
                break;
            case CMD_STOP:
                status = new boolean[]{true, false, false};
                commandOfService = TYRemindService.CMD_REMIND_CANCEL;
                break;
            case CMD_START:
            case CMD_RESET:
                commandOfService = TYRemindService.CMD_REMIND_RESCHEDULE;
                status = new boolean[]{false, true, true};

                createDataPersistIfNeed().reset();
                mRefreshRequestHandler.removeMessages(0);
                mRefreshRequestHandler.sendEmptyMessage(0);
                break;
        }

        mVBtnStart.setEnabled(status[0]);
        mVBtnStop.setEnabled(status[1]);
        mVBtnReset.setEnabled(status[2]);

        return commandOfService;
    }

}
