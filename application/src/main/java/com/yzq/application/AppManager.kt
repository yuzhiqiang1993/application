package com.yzq.application

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
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
     * 安装应用
     *
     * @param apkPath String apk路径
     * @param authority String FileProvider的authority
     */
    @JvmStatic
    fun installApk(apkPath: String, authority: String) {
        kotlin.runCatching {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                throw Exception("apkPath:${apkPath} not exists")
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val apkUri: Uri =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        // 使用 FileProvider 生成 Uri
                        FileProvider.getUriForFile(application, authority, apkFile)
                    } else {
                        // 直接生成 Uri
                        Uri.fromFile(apkFile)
                    }

                setDataAndType(apkUri, "application/vnd.android.package-archive")
            }

            // 检查设备是否有处理此Intent的活动
            if (intent.resolveActivity(application.packageManager) != null) {
                application.startActivity(intent)
            } else {
                throw Exception("No Activity found to handle install apk Intent")
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

}