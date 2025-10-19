package iceshell.xposed.sesame.data.local

/**
 * 偏好设置管理器
 * 负责本地数据存储
 */
class PreferenceManager private constructor() {
    
    private val cache = mutableMapOf<String, Any>()
    
    companion object {
        @Volatile
        private var INSTANCE: PreferenceManager? = null
        
        fun getInstance(): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 获取字符串
     */
    fun getString(key: String, defaultValue: String): String {
        return cache[key] as? String ?: defaultValue
    }
    
    /**
     * 保存字符串
     */
    fun putString(key: String, value: String) {
        cache[key] = value
        // TODO: 持久化到文件或 SharedPreferences
    }
    
    /**
     * 获取布尔值
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return cache[key] as? Boolean ?: defaultValue
    }
    
    /**
     * 保存布尔值
     */
    fun putBoolean(key: String, value: Boolean) {
        cache[key] = value
        // TODO: 持久化到文件或 SharedPreferences
    }
    
    /**
     * 获取整数
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return cache[key] as? Int ?: defaultValue
    }
    
    /**
     * 保存整数
     */
    fun putInt(key: String, value: Int) {
        cache[key] = value
        // TODO: 持久化到文件或 SharedPreferences
    }
    
    /**
     * 清除所有数据
     */
    fun clear() {
        cache.clear()
        // TODO: 清除持久化数据
    }
}
