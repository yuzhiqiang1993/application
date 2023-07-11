package com.yzq.application

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.MainThread
import com.yzq.logger.Logger
import java.util.Stack
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


/**
 * @description AppManager
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

object AppManager : Application.ActivityLifecycleCallbacks {

    private const val TAG = "AppManager"


    private val initialized = AtomicBoolean(false)


    @JvmStatic
    private lateinit var _application: Application

    @JvmStatic
    val application by ::_application


    /*存放Activity的Stack*/
    private val activityStack: Stack<Activity> = Stack()

    /*ActivityCount*/
    @JvmStatic
    private val _activityCount = AtomicInteger(0)

    /*Activity的数量*/
    @JvmStatic
    val activityCount
        get() = _activityCount.get()

    /**
     * 栈顶的Activity
     */
    @JvmStatic
    val topActivity: Activity?
        get() {
            return if (activityStack.size > 0) {
                activityStack[0]
            } else {
                null
            }
        }


    private val _isForeground = AtomicBoolean(false)


    /**
     * 是否在前台
     */
    @JvmStatic
    val isForeground: Boolean
        get() = _isForeground.get()


    /**
     * App状态监听器列表
     */
    private val appStateListenerList: CopyOnWriteArrayList<AppStateListener> =
        CopyOnWriteArrayList()


    /**
     * 初始化
     * @param application Application
     */
    @JvmStatic
    @MainThread
    fun init(application: Application) {
        if (initialized.get()) {
            Logger.i("已经初始化过了")
            return
        }
        this._application = application
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 添加App状态监听
     * @param appStateListener AppStateListener
     */
    @JvmStatic
    @MainThread
    fun addAppStateListener(appStateListener: AppStateListener) {
        if (!appStateListenerList.contains(appStateListener)) {
            appStateListenerList.add(appStateListener)
        }
    }

    /**
     * 移除App状态监听
     * @param appStateListener AppStateListener
     */
    @JvmStatic
    @MainThread
    fun removeAppStateListener(appStateListener: AppStateListener) {
        if (appStateListenerList.contains(appStateListener)) {
            appStateListenerList.remove(appStateListener)
        }
    }

    /**
     * 清除App状态监听
     */
    @JvmStatic
    @MainThread
    fun clearAppStateListener() {
        appStateListenerList.clear()
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.i("onActivityCreated：${activity.javaClass.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.i("onActivityStarted: ${activity.javaClass.simpleName}")
        activityStack.add(activity)
        _activityCount.incrementAndGet()//自增
        if (!_isForeground.get()) {
            Logger.i("onAppForeground")
            _isForeground.compareAndSet(false, true)
            appStateListenerList.forEach {
                it.onAppForeground()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.i("onActivityResumed: ${activity.javaClass.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.i("onActivityPaused: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        _activityCount.decrementAndGet()//自减
        if (_activityCount.get() <= 0) {
            /*说明此时应用处于后台*/
            _isForeground.set(false)
            Logger.i("onAppBackground")
            appStateListenerList.forEach {
                it.onAppBackground()
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.i("onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.i("onActivityDestroyed: ${activity.javaClass.simpleName}")
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
        }
        if (_activityCount.get() <= 0) {
            Logger.i("onAppExit")
            appStateListenerList.forEach {
                it.onAppExit()
            }
        }
    }

    /**
     * 退出App
     */
    @JvmStatic
    fun exitApp() {
        activityStack.forEach {
            it.finish()
        }
    }

    /**
     * 判断当前进程是否是主进程
     * @return Boolean
     */
    @JvmStatic
    fun isMainProcess(): Boolean =
        getCurrentProcessName() == AppContext.packageName


    /**
     * 获取当前进程名
     * @return String?
     */
    @JvmStatic
    fun getCurrentProcessName(): String =
        getCurrrentProcessInfo()?.processName ?: ""


    /**
     * 获取当前进程信息
     * @return ActivityManager.RunningAppProcessInfo?
     */
    @JvmStatic
    fun getCurrrentProcessInfo(): ActivityManager.RunningAppProcessInfo? {
        val activityManager =
            AppContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses

        return runningAppProcesses.find {
            it.pid == android.os.Process.myPid()
        }

    }

}