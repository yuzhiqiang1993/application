package com.yzq.application.demo;

import android.app.Application;

import com.yzq.application.AppManager;
import com.yzq.logger.LogCat;

public class AppJava extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.init(this);
        String currentProcessName = AppManager.getCurrentProcessName();
        LogCat.i("currentProcessName = " + currentProcessName);
    }
}
