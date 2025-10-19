package iceshell.xposed.sesame.manager

/**
 * 配置管理器
 */
class ConfigManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: ConfigManager? = null
        
        fun getInstance(): ConfigManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigManager().also { INSTANCE = it }
            }
        }
    }
    
    fun loadConfig() {
        // 加载配置
    }
}
