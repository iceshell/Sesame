package iceshell.xposed.sesame.core

import android.app.Application
import android.util.Log
import iceshell.xposed.sesame.BuildConfig
import iceshell.xposed.sesame.config.Config
import iceshell.xposed.sesame.util.FileHelper
import iceshell.xposed.sesame.util.Logger

/**
 * 应用程序入口点
 * 负责初始化核心组件和全局配置
 * 
 * 注意：区分两种环境：
 * 1. 自身应用进程：只初始化UI和配置
 * 2. Xposed宿主进程：通过XposedEntry初始化Hook组件
 */
class Application : Application() {
    
    companion object {
        private const val TAG = "Sesame"
        
        @JvmStatic
        lateinit var instance: Application
            private set
            
        /**
         * 是否在Xposed环境中运行
         */
        @JvmStatic
        var isXposedEnvironment = false
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 检测运行环境
        isXposedEnvironment = isRunningInXposed()
        
        Log.d(TAG, "Application onCreate: 芝麻开花节节高")
        Log.d(TAG, "Environment: ${if (isXposedEnvironment) "Xposed Host" else "Standalone App"}")
        
        // 只在自身应用进程初始化基础组件
        if (!isXposedEnvironment) {
            initializeApp()
        }
    }
    
    /**
     * 初始化应用程序核心组件（只在自身应用进程中调用）
     */
    private fun initializeApp() {
        try {
            Logger.system("Application", "初始化芝麻球 v${BuildConfig.VERSION_NAME}")
            
            // 初始化配置
            Config.init()
            Logger.system("Application", "配置系统初始化完成")
            
            // 写入启动日志
            FileHelper.writeLog("runtime", "应用启动成功 - 芝麻开花节节高")
            
            Logger.system("Application", "应用初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "应用初始化失败", e)
            Logger.printStackTrace("Application", "初始化失败", e)
        }
    }
    
    /**
     * 检测是否在Xposed环境中运行
     */
    private fun isRunningInXposed(): Boolean {
        // 检测包名，如果在支付宝进程则为Xposed环境
        return packageName == Constants.ALIPAY_PACKAGE
    }
}
