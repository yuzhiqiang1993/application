package com.yzq.application.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.DefaultActivityLifecycleCallbacks
import com.yzq.application.demo.databinding.ActivityMainBinding
import com.yzq.logger.Logger

class MainActivity : AppCompatActivity(), AppStateListener {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppManager.addAppStateListener(this)


        binding.btnSkip.setOnClickListener {
            Activity2.start(this)
        }

        AppManager.addActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: android.app.Activity, savedInstanceState: android.os.Bundle?
            ) {

                Logger.i("onActivityCreated:${activity.javaClass.simpleName}")/*Activity创建*/
                AppManager.topActivity?.let {
                    /*获取当前栈顶的Activity*/
                    Logger.i("topActivity:${it.javaClass.simpleName}")
                }
            }

            override fun onActivityDestroyed(activity: android.app.Activity) {/*Activity销毁*/
                Logger.i("onActivityDestroyed:${activity.javaClass.simpleName}")
            }
        })


        /*获取当前栈顶的Activity*/
        val topActivity = AppManager.topActivity/*是否是主进程*/
        val mainProcess = AppManager.isMainProcess()/*是否处于前台*/
        val foreground = AppManager.isForeground/*退出应用*/
//        AppManager.exitApp()


    }


    override fun onAppBackground() {

    }

    override fun onAppExit() {

    }

    override fun onAppForeground() {

    }
}