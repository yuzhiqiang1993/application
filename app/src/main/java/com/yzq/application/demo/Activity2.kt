package com.yzq.application.demo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Activity2 : AppCompatActivity() {
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