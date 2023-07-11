package com.yzq.application.demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.logger.LogCat

class App : Application(), AppStateListener {

    override fun onCreate() {
        super.onCreate()

        /*初始化*/
        AppManager.init(this)
        /*添加状态监听*/
        AppManager.addAppStateListener(object : AppStateListener {

            override fun onAppForeground() {
                /*App切换到前台*/
            }

            override fun onAppExit() {
                /*App 退出*/
            }

            override fun onAppBackground() {
                /*App切换到后台*/
            }
        })

        /*是否是主进程*/
        val mainProcess = AppManager.isMainProcess()
        LogCat.i("是否是主进程：$mainProcess")
        /*当前进程名称*/
        val currentProcessName = AppManager.getCurrentProcessName()
        LogCat.i("当前进程名称：$currentProcessName")
        /*当前进程信息*/
        val currrentProcessInfo = AppManager.getCurrrentProcessInfo()
        LogCat.i("当前进程信息：$currrentProcessInfo")
    }


}