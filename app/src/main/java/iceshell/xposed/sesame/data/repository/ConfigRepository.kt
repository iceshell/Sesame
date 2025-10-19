package iceshell.xposed.sesame.data.repository

import iceshell.xposed.sesame.data.local.PreferenceManager

/**
 * 配置数据仓库
 * 负责配置数据的读取和保存
 */
class ConfigRepository private constructor() {
    
    private val preferenceManager = PreferenceManager.getInstance()
    
    companion object {
        @Volatile
        private var INSTANCE: ConfigRepository? = null
        
        fun getInstance(): ConfigRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigRepository().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 获取配置
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return preferenceManager.getString(key, defaultValue)
    }
    
    /**
     * 保存配置
     * @param key 配置键
     * @param value 配置值
     */
    fun putString(key: String, value: String) {
        preferenceManager.putString(key, value)
    }
    
    /**
     * 获取布尔配置
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferenceManager.getBoolean(key, defaultValue)
    }
    
    /**
     * 保存布尔配置
     */
    fun putBoolean(key: String, value: Boolean) {
        preferenceManager.putBoolean(key, value)
    }
}
