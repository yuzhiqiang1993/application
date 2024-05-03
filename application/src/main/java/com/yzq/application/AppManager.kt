package com.yzq.application

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import java.util.Stack
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


/**
 * @description AppManager
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

object AppManager : DefaultActivityLifecycleCallbacks {

    private const val TAG = "AppManager"


    private val initialized = AtomicBoolean(false)

    @Volatile
    var isDebug = false


    @JvmStatic
    private lateinit var _application: Application

    @JvmStatic
    val application by ::_application

    private val _isForeground = AtomicBoolean(false)

    /**
     * 是否在前台
     */
    @JvmStatic
    val isForeground: Boolean
        get() = _isForeground.get()


    /*存放Activity的Stack*/
    private val activityStack: Stack<Activity> = Stack()

    /*前台Activity的数量*/
    @JvmStatic
    private val foregroundActivityCount = AtomicInteger(0)

    /*Activity的数量*/
    @JvmStatic
    val activityCount
        get() = activityStack.size

    /**
     * 栈顶的Activity
     */
    @JvmStatic
    val topActivity: Activity?
        get() {
            return if (activityStack.size > 0) {
                activityStack[activityStack.size - 1]
            } else {
                null
            }
        }


    /**
     * App状态监听器列表
     */
    private val appStateListenerList: CopyOnWriteArrayList<AppStateListener> =
        CopyOnWriteArrayList()


    /**
     * @description 初始化
     * @param application Application
     * @param debug Boolean
     */
    @JvmStatic
    @JvmOverloads
    fun init(application: Application, debug: Boolean = false) {
        if (initialized.get()) {
            log("已经初始化过了")
            return
        }
        this._application = application
        this.isDebug = debug
        addActivityLifecycleCallbacks(this)
    }

    /**
     * @description 添加App状态监听
     * @param appStateListener AppStateListener
     */
    @JvmStatic
    fun addAppStateListener(appStateListener: AppStateListener) {
        if (!appStateListenerList.contains(appStateListener)) {
            appStateListenerList.add(appStateListener)
        }
    }

    /**
     * @description 移除App状态监听
     * @param appStateListener AppStateListener
     */
    @JvmStatic
    fun removeAppStateListener(appStateListener: AppStateListener) {
        if (appStateListenerList.contains(appStateListener)) {
            appStateListenerList.remove(appStateListener)
        }
    }


    /**
     * @description 清除App状态监听
     */
    @JvmStatic
    fun clearAppStateListener() {
        appStateListenerList.clear()
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        log("onActivityCreated：${activity.javaClass.simpleName}")
        activityStack.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        log("onActivityStarted: ${activity.javaClass.simpleName}")
        foregroundActivityCount.incrementAndGet()//自增
        if (!_isForeground.get()) {
            log("onAppForeground")
            _isForeground.compareAndSet(false, true)
            appStateListenerList.forEach {
                it.onAppForeground()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        log("onActivityResumed: ${activity.javaClass.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        log("onActivityPaused: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        log("onActivityStopped: ${activity.javaClass.simpleName}")
        foregroundActivityCount.decrementAndGet()//自减
        if (foregroundActivityCount.get() <= 0) {
            //说明App切换到了后台
            _isForeground.set(false)
            log("onAppBackground")
            appStateListenerList.forEach {
                it.onAppBackground()
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        log("onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        log("onActivityDestroyed: ${activity.javaClass.simpleName}")
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
        }
        if (foregroundActivityCount.get() <= 0) {
            log("onAppExit")
            appStateListenerList.forEach {
                //需要注意该方法只有在用户主动退出App时才会调用，如果是App被强杀可能不会被调用，跟设备和系统有关
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
    fun isMainProcess(): Boolean = getCurrentProcessName() == AppContext.packageName


    /**
     * 获取当前进程名
     * @return String?
     */
    @JvmStatic
    fun getCurrentProcessName(): String = getCurrrentProcessInfo()?.processName ?: ""


    /**
     * 获取包名
     */
    @JvmStatic
    fun getPackageName(): String = AppContext.packageName


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


    /**
     * 添加Activity生命周期回调
     * @param callbacks DefaultActivityLifecycleCallbacks
     */
    @JvmStatic
    fun addActivityLifecycleCallbacks(callbacks: DefaultActivityLifecycleCallbacks) {
        application.registerActivityLifecycleCallbacks(callbacks)
    }

    /**
     * 移除Activity生命周期回调
     * @param callbacks DefaultActivityLifecycleCallbacks
     */
    @JvmStatic
    fun removeActivityLifecycleCallbacks(callbacks: DefaultActivityLifecycleCallbacks) {
        application.unregisterActivityLifecycleCallbacks(callbacks)
    }


    private fun log(msg: String) {
        if (isDebug) {
            Log.i(TAG, msg)
        }
    }


    /**
     * @description 判断应用是否安装
     * @param packageName String
     * @return Boolean
     */
    @JvmStatic
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            application.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }

    }


    /**
     * @description 安装应用
     * @param apkPath String
     */
    @JvmStatic
    fun installApk(apkPath: String) {
        kotlin.runCatching {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(Uri.parse(apkPath), "application/vnd.android.package-archive")
            application.startActivity(intent)
        }

    }

}