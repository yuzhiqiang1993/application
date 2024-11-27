package com.yzq.application_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.getAppInstallTime
import com.yzq.application.getAppVersionCode
import com.yzq.application.getAppVersionName
import com.yzq.application.getCurrentProcessName
import com.yzq.application.getCurrrentProcessInfo
import com.yzq.application.isAppForeground
import com.yzq.application.isMainProcess
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter

class App : Application(), AppStateListener {


    companion object {
        const val TAG = "App"
    }

    override fun onCreate() {
        super.onCreate()

        /*初始化*/
        AppManager.init(this, BuildConfig.DEBUG)

        Logger.addPrinter(
            ConsoleLogPrinter.getInstance(
                ConsoleLogConfig.Builder().enable(true).build()
            )
        )

        /*添加状态监听*/
        AppManager.addAppStateListener(object : AppStateListener {

            override fun onAppForeground() {
                /*App切换到前台*/
                Logger.i("App切换到前台")
            }

            override fun onAppExit() {
                /*App 退出*/
                Logger.i("App 退出")
            }

            override fun onAppBackground() {
                /*App切换到后台*/
                Logger.i("App切换到后台")
            }
        })

        /*是否是主进程*/
        val mainProcess = AppManager.isMainProcess()
        Logger.i("是否是主进程：$mainProcess")
        /*当前进程名称*/
        val currentProcessName = AppManager.getCurrentProcessName()
        Logger.i("当前进程名称：$currentProcessName")
        /*当前进程信息*/
        val currrentProcessInfo = AppManager.getCurrrentProcessInfo()
        Logger.i("当前进程信息：$currrentProcessInfo")

        Logger.i(
            "当前app version:${AppManager.getAppVersionCode()}",
            AppManager.getAppVersionName()
        )
        Logger.i(
            "当前app AppManager.isAppForeground()", AppManager.isAppForeground()
        )

        Logger.i("AppManager.getAppInstallTime():", AppManager.getAppInstallTime())


    }

}