package com.yzq.application

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process

/**
 * 获取当前进程信息
 * @return ActivityManager.RunningAppProcessInfo?
 */
fun AppManager.getCurrrentProcessInfo(): ActivityManager.RunningAppProcessInfo? {
    val activityManager = AppContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcesses = activityManager.runningAppProcesses

    return runningAppProcesses.find {
        it.pid == Process.myPid()
    }
}

/**
 * 判断当前进程是否是主进程
 * @return Boolean
 */
fun AppManager.isMainProcess(): Boolean = getCurrentProcessName() == AppContext.packageName

/**
 * 获取当前进程名
 * @return String?
 */
fun AppManager.getCurrentProcessName(): String = getCurrrentProcessInfo()?.processName ?: ""

/**
 * 获取包名
 * @return String
 */
fun AppManager.getPackageName(): String = AppContext.packageName

/**
 * 判断应用是否安装
 * @param packageName String
 * @return Boolean
 */
fun AppManager.isAppInstalled(packageName: String): Boolean {
    return try {
        application.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 获取应用的 Version Name
 * @return String
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
 * @return Int
 */
fun AppManager.getAppVersionCode(): Int {
    return try {
        val packageInfo = AppContext.packageManager.getPackageInfo(AppContext.packageName, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toInt() // API 28 及以上使用 longVersionCode
        } else {
            packageInfo.versionCode // Deprecated in API 28，但适用于旧版本
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

/**
 * 获取应用的名称
 * @return String
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
 * 检查是否为 Debug 模式
 * @return Boolean
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
 * 获取安装时间
 * @return Long
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
 * 获取最后更新时间
 * @return Long
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
 * 获取目标 SDK 版本
 * @return Int
 */
fun AppManager.getTargetSdkVersion(): Int {
    return try {
        AppContext.applicationInfo.targetSdkVersion
    } catch (e: Exception) {
        -1
    }
}

/**
 * 获取最小 SDK 版本
 * @return Int
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
 * 检查应用是否在前台运行
 * @return Boolean
 */
fun AppManager.isAppForeground(): Boolean {
    val activityManager = AppContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningProcesses = activityManager.runningAppProcesses ?: return false
    return runningProcesses.any {
        it.pid == Process.myPid() && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}