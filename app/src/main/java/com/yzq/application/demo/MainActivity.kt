package com.yzq.application.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.demo.databinding.ActivityMainBinding

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

    }


    override fun onAppBackground() {

    }

    override fun onAppExit() {

    }

    override fun onAppForeground() {

    }
}