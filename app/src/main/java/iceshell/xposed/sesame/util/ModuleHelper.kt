// 芝麻开花节节高
package iceshell.xposed.sesame.util

import android.content.Context
import java.io.File

/**
 * 模块帮助类
 * 
 * 检测模块激活状态
 */
object ModuleHelper {
    
    private var isActive = false
    private const val FLAG_FILE = "module_active.flag"
    
    /**
     * 设置模块激活状态
     * 
     * 此方法在Hook初始化时调用
     * 同时在外部存储创建标志文件供应用检测
     */
    fun setModuleActive() {
        isActive = true
        
        // 创建标志文件
        try {
            val flagFile = File("/sdcard/Android/data/iceshell.xposed.sesame", FLAG_FILE)
            flagFile.parentFile?.mkdirs()
            flagFile.writeText(System.currentTimeMillis().toString())
        } catch (e: Exception) {
            // 忽略文件创建失败
        }
    }
    
    /**
     * 检查模块是否激活
     * 
     * 支持LSPosed和LSPatch检测
     * 优先检查标志文件（最可靠）
     */
    fun isModuleActive(): Boolean {
        // 方法1: 检查内部标志（Xposed进程）
        if (isActive) return true
        
        // 方法2: 检查标志文件（应用进程）
        try {
            val flagFile = File("/sdcard/Android/data/iceshell.xposed.sesame", FLAG_FILE)
            if (flagFile.exists()) {
                val timestamp = flagFile.readText().toLongOrNull() ?: 0
                // 标志文件在1小时内创建的视为有效
                if (System.currentTimeMillis() - timestamp < 3600000) {
                    return true
                }
            }
        } catch (e: Exception) {
            // 忽略
        }
        
        // 方法3: 检查系统属性（LSPosed）
        try {
            val prop = System.getProperty("xposed.bridge.version")
            if (prop != null) {
                // 只是检测到Xposed框架，不代表本模块已激活
                // 但可以作为辅助判断
            }
        } catch (e: Exception) {
            // 忽略
        }
        
        // 方法4: 检查堆栈（LSPosed/LSPatch）
        try {
            throw Exception("Check stack")
        } catch (e: Exception) {
            val stack = e.stackTraceToString()
            if (stack.contains("LSPosed") || 
                stack.contains("EdXposed") ||
                stack.contains("LSPatch")) {
                // 检测到框架但不一定激活本模块
            }
        }
        
        return false
    }
    
    /**
     * 获取框架名称
     */
    fun getFrameworkName(): String {
        try {
            val prop = System.getProperty("xposed.bridge.version")
            if (prop != null) {
                return when {
                    prop.contains("LSPosed") -> "LSPosed"
                    prop.contains("EdXposed") -> "EdXposed"
                    else -> "Xposed"
                }
            }
        } catch (e: Exception) {
            // 忽略
        }
        
        // 检查堆栈
        try {
            throw Exception("Check framework")
        } catch (e: Exception) {
            val stack = e.stackTraceToString()
            return when {
                stack.contains("LSPatch") -> "LSPatch"
                stack.contains("LSPosed") -> "LSPosed"
                stack.contains("EdXposed") -> "EdXposed"
                else -> "Unknown"
            }
        }
        
        return "Unknown"
    }
}
