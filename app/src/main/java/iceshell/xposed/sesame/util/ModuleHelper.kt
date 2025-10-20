// 芝麻开花节节高
package iceshell.xposed.sesame.util

/**
 * 模块帮助类
 * 
 * 检测模块激活状态
 */
object ModuleHelper {
    
    private var isActive = false
    
    /**
     * 设置模块激活状态
     * 
     * 此方法在Hook初始化时调用
     */
    fun setModuleActive() {
        isActive = true
    }
    
    /**
     * 检查模块是否激活
     * 
     * 支持LSPosed和LSPatch检测
     */
    fun isModuleActive(): Boolean {
        // 方法1: 检查内部标志
        if (isActive) return true
        
        // 方法2: 检查系统属性（LSPosed）
        try {
            val prop = System.getProperty("xposed.bridge.version")
            if (prop != null) return true
        } catch (e: Exception) {
            // 忽略
        }
        
        // 方法3: 检查堆栈（LSPosed/LSPatch）
        try {
            throw Exception("Check stack")
        } catch (e: Exception) {
            val stack = e.stackTraceToString()
            if (stack.contains("LSPosed") || 
                stack.contains("EdXposed") ||
                stack.contains("LSPatch")) {
                return true
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
