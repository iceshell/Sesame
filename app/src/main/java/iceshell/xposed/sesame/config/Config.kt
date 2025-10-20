// 芝麻开花节节高
package iceshell.xposed.sesame.config

import android.os.Environment
import org.json.JSONObject
import java.io.File

/**
 * 配置管理类
 * 
 * 使用JSON格式存储配置文件
 * 支持森林和庄园的独立配置
 */
object Config {
    
    // 配置文件目录
    private val CONFIG_DIR = File(
        Environment.getExternalStorageDirectory(),
        "Android/data/iceshell.xposed.sesame/config"
    )
    
    // 配置文件
    private val CONFIG_FILE = File(CONFIG_DIR, "config.json")
    
    // 默认配置
    private val DEFAULT_CONFIG = JSONObject().apply {
        // 森林配置
        put("forest", JSONObject().apply {
            put("enabled", true)
            put("collectEnergy", true)
            put("helpFriendCollect", true)
            put("energyRain", true)
            put("collectWateringBubble", true)
            put("collectProp", true)
            put("useDoubleCard", false)
            put("receiveForestTaskAward", true)
            put("collectGiftBox", true)
        })
        
        // 庄园配置
        put("farm", JSONObject().apply {
            put("enabled", true)
            put("feedAnimal", true)
            put("receiveFarmTaskAward", true)
            put("sendBackAnimal", true)
            put("rewardFriend", true)
        })
        
        // 通用配置
        put("common", JSONObject().apply {
            put("showToast", true)
            put("immediateEffect", true)
        })
    }
    
    // 当前配置（内存缓存）
    private var currentConfig: JSONObject? = null
    
    /**
     * 初始化配置
     */
    fun init() {
        // 确保目录存在
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs()
        }
        
        // 加载配置
        load()
    }
    
    /**
     * 加载配置
     */
    fun load() {
        currentConfig = if (CONFIG_FILE.exists()) {
            try {
                JSONObject(CONFIG_FILE.readText())
            } catch (e: Exception) {
                e.printStackTrace()
                DEFAULT_CONFIG
            }
        } else {
            DEFAULT_CONFIG
        }
    }
    
    /**
     * 保存配置
     */
    fun save() {
        try {
            CONFIG_FILE.writeText(currentConfig?.toString(2) ?: "{}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 获取配置文件
     */
    fun getConfigFile(): File = CONFIG_FILE
    
    /**
     * 获取森林配置
     */
    fun getForestConfig(): JSONObject {
        return currentConfig?.optJSONObject("forest") ?: DEFAULT_CONFIG.getJSONObject("forest")
    }
    
    /**
     * 获取庄园配置
     */
    fun getFarmConfig(): JSONObject {
        return currentConfig?.optJSONObject("farm") ?: DEFAULT_CONFIG.getJSONObject("farm")
    }
    
    /**
     * 获取通用配置
     */
    fun getCommonConfig(): JSONObject {
        return currentConfig?.optJSONObject("common") ?: DEFAULT_CONFIG.getJSONObject("common")
    }
    
    /**
     * 更新森林配置
     */
    fun updateForestConfig(key: String, value: Boolean) {
        if (currentConfig == null) load()
        val forest = currentConfig!!.getJSONObject("forest")
        forest.put(key, value)
        save()
    }
    
    /**
     * 更新庄园配置
     */
    fun updateFarmConfig(key: String, value: Boolean) {
        if (currentConfig == null) load()
        val farm = currentConfig!!.getJSONObject("farm")
        farm.put(key, value)
        save()
    }
    
    /**
     * 更新通用配置
     */
    fun updateCommonConfig(key: String, value: Boolean) {
        if (currentConfig == null) load()
        val common = currentConfig!!.getJSONObject("common")
        common.put(key, value)
        save()
    }
    
    /**
     * 获取配置值（布尔）
     */
    fun getBoolean(module: String, key: String, defaultValue: Boolean = false): Boolean {
        if (currentConfig == null) load()
        return try {
            when (module) {
                "forest" -> getForestConfig().optBoolean(key, defaultValue)
                "farm" -> getFarmConfig().optBoolean(key, defaultValue)
                "common" -> getCommonConfig().optBoolean(key, defaultValue)
                else -> defaultValue
            }
        } catch (e: Exception) {
            defaultValue
        }
    }
    
    /**
     * 重置为默认配置
     */
    fun reset() {
        currentConfig = DEFAULT_CONFIG
        save()
    }
}
