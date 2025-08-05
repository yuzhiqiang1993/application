package com.yzq.application_demo

import android.app.Application
import android.os.Process
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.AppStorage
import com.yzq.application.getAppInstallTime
import com.yzq.application.getAppVersionCode
import com.yzq.application.getAppVersionName
import com.yzq.application.getCurrentProcessName
import com.yzq.application.getCurrrentProcessInfo
import com.yzq.application.getPackageName
import com.yzq.application.isAppForeground
import com.yzq.application.isMainProcess
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter
import java.io.File

class App : Application() {

    companion object {
        const val TAG = "App"
    }

    override fun onCreate() {
        super.onCreate()

        // 1. 初始化 AppManager
        AppManager.init(this, BuildConfig.DEBUG)

        // 2. 配置日志
        Logger.addPrinter(
            ConsoleLogPrinter.getInstance(
                ConsoleLogConfig.Builder()
                    .enable(true)
                    .tag(TAG)
                    .build()
            )
        )

        // 3. 应用状态监听
        setupAppStateListener()

        // 4. 进程信息
        checkProcessInfo()

        // 5. 应用信息
        logAppInfo()

        // 6. 存储路径演示
        demonstrateStorage()


    }

    private fun setupAppStateListener() {
        AppManager.addAppStateListener(object : AppStateListener {
            override fun onAppForeground() {
                Logger.i("应用切换到前台")
                // 可以在这里处理应用回到前台时的逻辑
                // 例如：恢复动画、刷新数据等
            }

            override fun onAppBackground() {
                Logger.i("应用切换到后台")
                // 可以在这里处理应用进入后台时的逻辑
                // 例如：停止动画、保存数据等
            }

            override fun onAppExit() {
                Logger.i("应用退出")
                // 可以在这里处理应用退出时的逻辑
                // 例如：清理缓存、保存状态等
            }
        })
    }

    private fun checkProcessInfo() {
        val isMain = AppManager.isMainProcess()
        val processName = AppManager.getCurrentProcessName()
        val processInfo = AppManager.getCurrrentProcessInfo()
        val pid = Process.myPid()

        Logger.i("""
            进程信息:
            - 是否是主进程: $isMain
            - 当前进程名称: $processName
            - 进程 ID: $pid
            - 进程详细信息: $processInfo
        """.trimIndent())
    }

    private fun logAppInfo() {
        Logger.i("""
            应用信息:
            - 版本号: ${AppManager.getAppVersionCode()}
            - 版本名称: ${AppManager.getAppVersionName()}
            - 安装时间: ${AppManager.getAppInstallTime()}
            - 是否在前台: ${AppManager.isAppForeground()}
            - 包名: ${AppManager.getPackageName()}
        """.trimIndent())
    }

    private fun demonstrateStorage() {
        // 内部存储
        val internal = """
            内部存储路径:
            - 数据目录: ${AppStorage.Internal.dataPath}
            - 文件目录: ${AppStorage.Internal.filesPath}
            - 缓存目录: ${AppStorage.Internal.cachePath}
            - 数据库目录: ${AppStorage.Internal.dbPath}
            - SP目录: ${AppStorage.Internal.spPath}
        """.trimIndent()
        Logger.i(internal)

        // 外部私有存储
        val externalPrivate = """
            外部私有存储:
            - 根目录: ${AppStorage.External.Private.rootPath}
            - 文件目录: ${AppStorage.External.Private.filesPath}
            - 缓存目录: ${AppStorage.External.Private.cachePath}
            - 下载目录: ${AppStorage.External.Private.downloadPath}
            - 图片目录: ${AppStorage.External.Private.picturesPath}
        """.trimIndent()
        Logger.i(externalPrivate)

        // 外部公共存储
        val externalPublic = """
            外部公共存储:
            - 下载目录: ${AppStorage.External.Public.downloadPath}
            - 图片目录: ${AppStorage.External.Public.picturesPath}
            - 音乐目录: ${AppStorage.External.Public.musicPath}
            - 视频目录: ${AppStorage.External.Public.moviesPath}
        """.trimIndent()
        Logger.i(externalPublic)

        // 示例：创建文件
        try {
            // 在私有目录创建测试文件
            File(AppStorage.Internal.filesPath, "test.txt").apply {
                writeText("Hello Application Component!")
            }
            Logger.i("测试文件创建成功")
        } catch (e: Exception) {
            Logger.e("文件操作失败: ${e.message}")
        }
    }

}