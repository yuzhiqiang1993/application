package com.yzq.application_demo
import android.app.Application
import com.yzq.application.AppManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化 AppManager
        AppManager.init(this, BuildConfig.DEBUG)
    }
}