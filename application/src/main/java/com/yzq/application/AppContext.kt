package com.yzq.application

import android.app.Application
import android.content.ContextWrapper


/**
 * @description 全局的Context，用于获取全局的Context，使用[AppContext]代替[Application]
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */
object AppContext : ContextWrapper(AppManager.application)