# application

简单易用，无侵入的Application组件。

提供了常用的App状态监听以及全局ApplicationContext.

## 使用方式

### 添加依赖

```kotlin
implementation("com.xeonyu:application:1.0.1")
```

### 在你的Application类中初始化

```kotlin
AppManager.init(this)
```

### 全局的Application上下文

注意包名：

```
import com.yzq.application.AppContext
//示例 ApplicationContext
AppContext.checkSelfPermission( android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//示例 Application
Utils.init(AppManager.application)
```

### App状态监听

```kotlin
AppManager.addAppStateListener(object : AppStateListener {

    override fun onAppForeground() {
        /*App切换到前台*/
    }

    override fun onAppExit() {
        /*App 退出*/
    }

    override fun onAppBackground() {
        /*App切换到后台*/
    }
})
```

### 其他常用

```kotlin
/*获取当前栈顶的Activity*/
val topActivity = AppManager.topActivity
/*是否是主进程*/
val mainProcess = AppManager.isMainProcess()
/*是否处于前台*/
val foreground = AppManager.isForeground
/*退出应用*/
AppManager.exitApp()
```



