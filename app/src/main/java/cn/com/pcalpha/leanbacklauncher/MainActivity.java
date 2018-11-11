/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package cn.com.pcalpha.leanbacklauncher;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.TextView;

import cn.com.pcalpha.leanbacklauncher.apps.AppFragment;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {


    private TextView mTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initTimer();
        showAppFragment();
    }

    public void initView(){
        setContentView(R.layout.activity_main);
    }

    public void initTimer(){
        mTime = (TextView) findViewById(R.id.time);
        new TimeThread().start();
    }

    private AppFragment showAppFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.app_fragment);
        AppFragment appFragment = new AppFragment();
        if (null == fragment) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.app_fragment, appFragment)
                    .commit();
        }
        return appFragment;
    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();//获取系统时间
                    CharSequence sysTimeStr = DateFormat.format("hh:mm", sysTime);//时间显示格式
                    mTime.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };
}
