package iceshell.xposed.sesame.core

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import iceshell.xposed.sesame.hook.HookManager
import iceshell.xposed.sesame.util.Logger

/**
 * Xposed 模块入口
 * 实现 IXposedHookLoadPackage 接口
 */
class XposedEntry : IXposedHookLoadPackage {
    
    companion object {
        private const val TAG = "XposedEntry"
        private const val TARGET_PACKAGE = "com.eg.android.AlipayGphone"
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 只处理支付宝包名
        if (lpparam.packageName != TARGET_PACKAGE) {
            return
        }
        
        try {
            Logger.system(TAG, "Sesame Xposed module loaded for ${lpparam.packageName}")
            Logger.system(TAG, "Process: ${lpparam.processName}")
            
            // 初始化 Hook 管理器
            HookManager.getInstance().init(lpparam)
            
            Logger.system(TAG, "All hooks initialized successfully")
        } catch (e: Exception) {
            Logger.error(TAG, "Failed to initialize hooks: ${e.message}")
            e.printStackTrace()
        }
    }
}
