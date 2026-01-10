package com.yzq.application

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process

/**
 * 获取当前进程信息
 *
 * @return [ActivityManager.RunningAppProcessInfo] 对象，如果获取失败则返回 null
 */
fun AppManager.getCurrentProcessInfo(): ActivityManager.RunningAppProcessInfo? {
    val activityManager = AppContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcesses = activityManager.runningAppProcesses ?: return null

    return runningAppProcesses.find {
        it.pid == Process.myPid()
    }
}

/**
 * 判断当前进程是否是主进程
 *
 * @return true 表示当前进程是主进程，false 表示是子进程
 */
fun AppManager.isMainProcess(): Boolean = getCurrentProcessName() == AppContext.packageName

/**
 * 获取当前进程名
 *
 * @return 进程名字符串，获取失败返回空字符串
 */
fun AppManager.getCurrentProcessName(): String = getCurrentProcessInfo()?.processName ?: ""

/**
 * 获取应用包名
 *
 * @return 包名字符串
 */
fun AppManager.getPackageName(): String = AppContext.packageName

/**
 * 判断应用是否安装
 *
 * @param packageName 目标应用的包名
 * @return true 表示已安装，false 表示未安装
 */
fun AppManager.isAppInstalled(packageName: String): Boolean {
    return try {
        AppContext.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * 获取应用的 Version Name
 *
 * @return 版本名称，获取失败返回 "Unknown"
 */
fun AppManager.getAppVersionName(): String {
    return try {
        val packageInfo = AppContext.packageManager.getPackageInfo(AppContext.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

/**
 * 获取应用的 Version Code
 *
 * 兼容处理：Android P (Api 28) 及以上使用 longVersionCode，以下使用 versionCode
 *
 * @return Version Code，获取失败返回 -1
 */
fun AppManager.getAppVersionCode(): Long {
    return try {
        val packageInfo = AppContext.packageManager.getPackageInfo(AppContext.packageName, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1L
    }
}

/**
 * 获取应用的名称 (Label)
 *
 * @return 应用名称，获取失败返回 "Unknown"
 */
fun AppManager.getAppName(): String {
    return try {
        val applicationInfo =
            AppContext.packageManager.getApplicationInfo(AppContext.packageName, 0)
        AppContext.packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: Exception) {
        "Unknown"
    }
}

/**
 * 检查应用是否为 Debug 模式
 *
 * @return true 表示为 Debug 模式，false 为 Release 模式
 */
fun AppManager.isDebuggable(): Boolean {
    return try {
        val applicationInfo = AppContext.applicationInfo
        applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    } catch (e: Exception) {
        false
    }
}

/**
 * 获取应用首次安装时间
 *
 * @return 安装时间戳，获取失败返回 -1
 */
fun AppManager.getAppInstallTime(): Long {
    return try {
        val packageInfo = AppContext.packageManager.getPackageInfo(AppContext.packageName, 0)
        packageInfo.firstInstallTime
    } catch (e: Exception) {
        -1
    }
}

/**
 * 获取应用最后更新时间
 *
 * @return 更新时间戳，获取失败返回 -1
 */
fun AppManager.getAppLastUpdateTime(): Long {
    return try {
        val packageInfo = AppContext.packageManager.getPackageInfo(AppContext.packageName, 0)
        packageInfo.lastUpdateTime
    } catch (e: Exception) {
        -1
    }
}

/**
 * 获取 Target SDK 版本
 *
 * @return Target SDK Version Code，获取失败返回 -1
 */
fun AppManager.getTargetSdkVersion(): Int {
    return try {
        AppContext.applicationInfo.targetSdkVersion
    } catch (e: Exception) {
        -1
    }
}

/**
 * 获取 Min SDK 版本
 *
 * 需要 Android N (API 24) 及以上版本支持
 *
 * @return Min SDK Version Code，如果设备版本低于 Android N 或获取失败则返回 -1
 */
fun AppManager.getMinSdkVersion(): Int {
    return try {
        val applicationInfo =
            AppContext.packageManager.getApplicationInfo(AppContext.packageName, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            applicationInfo.minSdkVersion
        } else {
            -1 // API 24 以下无此属性
        }
    } catch (e: Exception) {
        -1
    }
}

/**
 * 检查应用是否在前台
 *
 * 通过遍历运行进程列表判断，准确性较高
 *
 * @return true 表示在前台，false 表示在后台或获取失败
 */
fun AppManager.isAppForeground(): Boolean {
    val activityManager = AppContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningProcesses = activityManager.runningAppProcesses ?: return false
    return runningProcesses.any {
        it.pid == Process.myPid() && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}