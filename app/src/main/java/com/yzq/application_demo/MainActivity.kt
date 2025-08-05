package com.yzq.application_demo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.DefaultActivityLifecycleCallbacks
import com.yzq.application.AppStorage
import com.yzq.application.getCurrentProcessName
import com.yzq.application.getCurrrentProcessInfo
import com.yzq.application.isMainProcess
import com.yzq.application_demo.databinding.ActivityMainBinding
import com.yzq.logger.Logger
import java.io.File

class MainActivity : AppCompatActivity(), AppStateListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置标题
        title = "Application Demo"

        // 1. 初始化UI
        setupUI()

        // 2. 添加应用状态监听
        AppManager.addAppStateListener(this)

        // 3. 添加Activity生命周期回调
        setupActivityCallbacks()

        // 4. 更新状态信息
        updateStatusInfo()
    }

    private fun setupUI() {
        // 跳转到Activity2
        binding.btnSkip.setOnClickListener {
            Activity2.start(this)
        }

        // 演示文件操作
        binding.btnFile.setOnClickListener {
            demonstrateFileOperations()
        }

        // 显示进程信息
        binding.btnProcess.setOnClickListener {
            showProcessInfo()
        }

        // 退出应用
        binding.btnExit.setOnClickListener {
            AppManager.exitApp()
        }
    }

    private fun setupActivityCallbacks() {
        AppManager.addActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: android.os.Bundle?) {
                Logger.i("Activity创建: ${activity.javaClass.simpleName}")
                updateTopActivityInfo()
            }

            override fun onActivityDestroyed(activity: android.app.Activity) {
                Logger.i("Activity销毁: ${activity.javaClass.simpleName}")
                updateTopActivityInfo()
            }
        })
    }

    private fun updateStatusInfo() {
        val info = """
            应用状态信息:
            - 栈顶Activity: ${AppManager.topActivity?.javaClass?.simpleName}
            - Activity数量: ${AppManager.activityCount}
            - 是否是主进程: ${AppManager.isMainProcess()}
            - 是否在前台: ${AppManager.isForeground}
        """.trimIndent()
        
        binding.tvInfo.text = info
    }

    private fun updateTopActivityInfo() {
        AppManager.topActivity?.let {
            binding.tvTopActivity.text = "当前页面: ${it.javaClass.simpleName}"
        }
    }

    private fun demonstrateFileOperations() {
        try {
            // 在私有目录创建文件
            val file = File(AppStorage.Internal.filesPath, "demo.txt")
            file.writeText("这是一个测试文件 - ${System.currentTimeMillis()}")
            
            // 读取文件内容
            val content = file.readText()
            binding.tvFileContent.text = "文件内容: $content"
            
            Logger.i("文件操作成功")
        } catch (e: Exception) {
            Logger.e("文件操作失败: ${e.message}")
            binding.tvFileContent.text = "文件操作失败: ${e.message}"
        }
    }

    private fun showProcessInfo() {
        val info = """
            进程信息:
            - 进程名称: ${AppManager.getCurrentProcessName()}
            - 是否是主进程: ${AppManager.isMainProcess()}
            - 详细信息: ${AppManager.getCurrrentProcessInfo()}
        """.trimIndent()
        
        binding.tvProcessInfo.text = info
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                updateStatusInfo()
                true
            }
            R.id.action_clear -> {
                binding.tvFileContent.text = ""
                binding.tvProcessInfo.text = ""
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusInfo()
        updateTopActivityInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.removeAppStateListener(this)
    }

    // AppStateListener 实现
    override fun onAppBackground() {
        Logger.i("应用进入后台")
        binding.tvStatus.text = "状态: 后台"
    }

    override fun onAppExit() {
        Logger.i("应用退出")
    }

    override fun onAppForeground() {
        Logger.i("应用进入前台")
        binding.tvStatus.text = "状态: 前台"
    }
}