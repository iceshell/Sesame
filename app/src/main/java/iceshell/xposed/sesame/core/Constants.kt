package iceshell.xposed.sesame.core

/**
 * 核心常量定义
 */
object Constants {
    const val TAG = "Sesame"
    
    // 版本信息
    const val VERSION_NAME = "0.1.0"
    const val VERSION_CODE = 1
    
    // 支付宝包名
    const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
    
    // 网络请求相关
    const val DEFAULT_TIMEOUT = 30_000L
    const val MAX_RETRY_COUNT = 3
    
    // 统一文件路径：/storage/emulated/0/Android/media/com.eg.android.AlipayGphone/sesame
    const val BASE_DIR_PATH = "/storage/emulated/0/Android/media/com.eg.android.AlipayGphone/sesame"
    const val CONFIG_DIR_NAME = "config"
    const val LOG_DIR_NAME = "logs"
}
