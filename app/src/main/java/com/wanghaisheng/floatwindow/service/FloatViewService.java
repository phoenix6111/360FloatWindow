package com.wanghaisheng.floatwindow.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wanghaisheng.floatwindow.engine.FloatWindowManager;

/**
 * Author: sheng on 2016/9/15 20:49
 * Email: 1392100700@qq.com
 */
public class FloatViewService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FloatWindowManager.getInstance(getApplicationContext()).showFloatCircleView();
    }
}
