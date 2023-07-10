package com.yzq.application.demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.logger.LogCat

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this)

        val mainProcess = AppManager.isMainProcess()
        LogCat.i("是否是主进程：$mainProcess")
        val currentProcessName = AppManager.getCurrentProcessName()
        LogCat.i("当前进程名称：$currentProcessName")
        val currrentProcessInfo = AppManager.getCurrrentProcessInfo()
        LogCat.i("当前进程信息：$currrentProcessInfo")
    }
}