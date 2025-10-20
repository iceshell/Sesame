// 芝麻开花节节高
package iceshell.xposed.sesame.core

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import iceshell.xposed.sesame.hook.HookManager
import iceshell.xposed.sesame.util.Logger
import iceshell.xposed.sesame.util.ModuleHelper

/**
 * Xposed 模块入口点
 * 实现 IXposedHookLoadPackage 接口来拦截包加载
 * 
 * 支持 LSPosed 和 LSPatch
 */
class XposedEntry : IXposedHookLoadPackage {

    companion object {
        private const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
    }

    /**
     * 当目标应用加载时被调用
     * 
     * @param lpparam 包加载参数，包含包名、classLoader等信息
     */
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 只 Hook 支付宝应用
        if (lpparam.packageName != ALIPAY_PACKAGE) {
            return
        }

        try {
            // 设置模块激活状态
            ModuleHelper.setModuleActive()
            
            Logger.system("XposedEntry", "Sesame module loaded for ${lpparam.packageName}")
            Logger.system("XposedEntry", "Framework: ${ModuleHelper.getFrameworkName()}")
            Logger.system("XposedEntry", "芝麻开花节节高")
            
            // 初始化 Hook 管理器
            HookManager.init(lpparam)
            
            Logger.system("XposedEntry", "Sesame initialization completed")
        } catch (e: Exception) {
            Logger.printStackTrace("XposedEntry", "Failed to initialize Sesame", e)
        }
    }
}
