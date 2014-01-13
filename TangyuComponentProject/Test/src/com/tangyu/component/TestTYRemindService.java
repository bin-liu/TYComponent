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
package com.tangyu.component;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

import com.tangyu.component.demo.service.remind.TYRemindServiceImpl;
import com.tangyu.component.service.remind.TYRemindService;

/**
 * @author binliu on 12/31/13.
 */
public class TestTYRemindService extends ServiceTestCase<TYRemindServiceImpl> {

    public TestTYRemindService() {
        super(TYRemindServiceImpl.class);
    }

    /**
     * Constructor
     *
     * @param serviceClass The type of the service under test.
     */
    public TestTYRemindService(Class<TYRemindServiceImpl> serviceClass) {
        super(serviceClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNextRemindCanStart() {
        Log.v(getContext().getPackageName(), "Test App pid = " + android.os.Process.myPid());
        Intent intent = new Intent(getContext(), TYRemindServiceImpl.class);
        intent.putExtra(TYRemindService.INTENT_REMIND_COMMAND, TYRemindService.CMD_REMIND_RESCHEDULE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }
}
