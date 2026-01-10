package com.yzq.application_demo

import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.AppStorage
import com.yzq.application.DefaultActivityLifecycleCallbacks
import com.yzq.application.getAppInstallTime
import com.yzq.application.getAppVersionCode
import com.yzq.application.getAppVersionName
import com.yzq.application.getCurrentProcessInfo
import com.yzq.application.getCurrentProcessName
import com.yzq.application.getPackageName
import com.yzq.application.isAppForeground
import com.yzq.application.isMainProcess
import com.yzq.application_demo.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), AppStateListener {

    private lateinit var binding: ActivityMainBinding
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initData()
        setupListeners()
    }

    private fun initViews() {
        binding.apply {
            btnSkip.setOnClickListener {
                Activity2.start(this@MainActivity)
            }

            btnFile.setOnClickListener {
                demonstrateStorage()
            }

            btnProcess.setOnClickListener {
                showProcessDetails()
            }

            btnExit.setOnClickListener {
                AppManager.exitApp()
            }

            btnClearLog.setOnClickListener {
                binding.tvConsole.text = ""
            }
        }
    }

    private fun initData() {
        updateStatusInfo()
        updateTopActivityInfo()

        // 注册监听
        AppManager.addAppStateListener(this)
        setupActivityCallbacks()

        // 打印初始信息
        appendLog("=== 应用启动 ===")
        logAppInfo()
    }

    private fun setupListeners() {
        // 已经在 initViews 中设置了点击事件
    }

    // 日志显示辅助方法
    private fun appendLog(msg: String) {
        val time = dateFormat.format(Date())
        val logMsg = "[$time] $msg\n"
        runOnUiThread {
            binding.tvConsole.append(logMsg)
            binding.svLog.post {
                binding.svLog.fullScroll(android.view.View.FOCUS_DOWN)
            }
        }
    }

    private val lifecycleCallbacks = object : DefaultActivityLifecycleCallbacks {
        override fun onActivityCreated(
            activity: android.app.Activity,
            savedInstanceState: android.os.Bundle?
        ) {
            appendLog("Activity创建: ${activity.javaClass.simpleName}")
            updateTopActivityInfo()
        }

        override fun onActivityDestroyed(activity: android.app.Activity) {
            appendLog("Activity销毁: ${activity.javaClass.simpleName}")
            updateTopActivityInfo()
        }

        override fun onActivityResumed(activity: android.app.Activity) {
            updateTopActivityInfo()
        }
    }

    private fun setupActivityCallbacks() {
        AppManager.addActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    private fun updateStatusInfo() {
        val isForeground = AppManager.isAppForeground()
        binding.tvStatus.text = if (isForeground) "前台运行" else "后台运行"
        // 移除紫色字体，使用默认或灰色
        binding.tvStatus.setTextColor(if (isForeground) 0xFF4CAF50.toInt() else 0xFF757575.toInt())
    }

    private fun updateTopActivityInfo() {
        val topActivityName = AppManager.topActivity?.javaClass?.simpleName ?: "无"
        binding.tvTopActivity.text = topActivityName
    }

    private fun showProcessDetails() {
        val isMain = AppManager.isMainProcess()
        val processName = AppManager.getCurrentProcessName()
        val processInfo = AppManager.getCurrentProcessInfo()
        
        val info = """
            进程信息:
            - 进程名称: $processName
            - 是否是主进程: $isMain
            - PID: ${Process.myPid()}
            - UID: ${processInfo?.uid}
        """.trimIndent()

        appendLog("查看进程信息:\n$info")
    }

    private fun logAppInfo() {
        val info = """
            应用信息:
            - 包名: ${AppManager.getPackageName()}
            - 版本: ${AppManager.getAppVersionName()} (${AppManager.getAppVersionCode()})
            - 安装时间: ${
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date(
                    AppManager.getAppInstallTime()
                )
            )
        }
            - 前台状态: ${AppManager.isAppForeground()}
        """.trimIndent()

        appendLog(info)
    }

    private fun demonstrateStorage() {
        appendLog("--- 开始文件操作测试 ---")

        // 显示所有路径信息
        appendLog("【完整存储路径列表】\n${AppStorage.logPathInfo}")

        // 测试写文件
        try {
            val fileName = "test_log.txt"
            val file = File(AppStorage.Internal.filesPath, fileName)
            val content = "Test Content at ${Date()}"
            file.writeText(content)
            appendLog("写入文件成功: ${file.absolutePath}")

            val readContent = file.readText()
            appendLog("读取文件成功: $readContent")

        } catch (e: Exception) {
            val error = "文件操作失败: ${e.message}"
            appendLog(error)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.removeAppStateListener(this)
        AppManager.removeActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    // AppStateListener 实现
    override fun onAppForeground() {
        runOnUiThread {
            updateStatusInfo()
            appendLog(">>> 应用切换到前台")
        }
    }

    override fun onAppBackground() {
        runOnUiThread {
            updateStatusInfo()
            appendLog("<<< 应用切换到后台")
        }
    }

    override fun onAppExit() {
        runOnUiThread {
            appendLog("XXX 应用退出")
        }
    }
}