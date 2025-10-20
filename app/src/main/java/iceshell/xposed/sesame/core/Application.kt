package iceshell.xposed.sesame.core

import android.app.Application
import android.util.Log
import iceshell.xposed.sesame.BuildConfig
import iceshell.xposed.sesame.config.Config
import iceshell.xposed.sesame.util.FileHelper
import iceshell.xposed.sesame.util.Logger
import iceshell.xposed.sesame.util.NetworkUtils
import iceshell.xposed.sesame.manager.ConfigManager
import iceshell.xposed.sesame.manager.TaskManager
import iceshell.xposed.sesame.hook.RequestManager
import iceshell.xposed.sesame.hook.rpc.SimplifiedRpcBridge
import iceshell.xposed.sesame.feature.antforest.AntForestManager

/**
 * 应用程序入口点
 * 负责初始化核心组件和全局配置
 */
class Application : Application() {
    
    companion object {
        private const val TAG = "Sesame"
        
        @JvmStatic
        lateinit var instance: Application
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.d(TAG, "Application onCreate: 芝麻开花节节高")
        
        // 初始化配置
        try {
            Config.init()
            FileHelper.writeLog("runtime", "应用启动，配置初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "配置初始化失败", e)
        }
        
        initializeApp()
    }
    
    /**
     * 初始化应用程序核心组件
     */
    private fun initializeApp() {
        try {
            // 初始化日志系统
            Logger.system("Application", "Initializing Sesame v${BuildConfig.VERSION_NAME}")
            
            // 初始化网络工具
            NetworkUtils.init(applicationContext)
            Logger.system("Application", "Network utils initialized")
            
            // 初始化 RPC 桥接
            try {
                val rpcBridge = SimplifiedRpcBridge()
                rpcBridge.load()
                RequestManager.rpcBridge = rpcBridge
                Logger.system("Application", "RPC bridge initialized")
            } catch (e: Exception) {
                Logger.printStackTrace("Application", "RPC bridge initialization failed", e)
            }
            
            // 初始化配置管理器
            ConfigManager.getInstance().loadConfig()
            Logger.system("Application", "Config manager initialized")
            
            // 初始化任务管理器
            TaskManager.getInstance()
            Logger.system("Application", "Task manager initialized")
            
            // 初始化蚁蜓森林管理器
            AntForestManager.getInstance().init()
            Logger.system("Application", "AntForest manager initialized")
            
            Logger.system("Application", "Sesame initialization completed successfully")
        } catch (e: Exception) {
            Logger.printStackTrace("Application", "Sesame initialization failed", e)
        }
    }
}
