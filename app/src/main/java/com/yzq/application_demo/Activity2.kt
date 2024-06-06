package com.yzq.application_demo

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.application.DefaultActivityLifecycleCallbacks

class Activity2 : AppCompatActivity(), Application.ActivityLifecycleCallbacks,
    DefaultActivityLifecycleCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

    }

    companion object {
        fun start(context: Context) {
            /*启动当前页面*/
            context.startActivity(android.content.Intent(context, Activity2::class.java))
        }
    }


}