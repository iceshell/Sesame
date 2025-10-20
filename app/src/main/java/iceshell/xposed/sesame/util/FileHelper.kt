// 芝麻开花节节高
package iceshell.xposed.sesame.util

import android.os.Environment
import java.io.File

/**
 * 文件帮助类
 * 
 * 管理日志文件和配置文件
 */
object FileHelper {
    
    // 基础目录
    private val BASE_DIR = File(
        Environment.getExternalStorageDirectory(),
        "Android/data/iceshell.xposed.sesame"
    )
    
    // 日志目录
    private val LOG_DIR = File(BASE_DIR, "logs")
    
    init {
        // 确保目录存在
        if (!LOG_DIR.exists()) {
            LOG_DIR.mkdirs()
        }
    }
    
    /**
     * 获取森林日志文件
     */
    fun getForestLogFile(): File {
        return File(LOG_DIR, "forest.log")
    }
    
    /**
     * 获取庄园日志文件
     */
    fun getFarmLogFile(): File {
        return File(LOG_DIR, "farm.log")
    }
    
    /**
     * 获取所有日志文件
     */
    fun getAllLogFile(): File {
        return File(LOG_DIR, "all.log")
    }
    
    /**
     * 获取运行时日志文件
     */
    fun getRuntimeLogFile(): File {
        return File(LOG_DIR, "runtime.log")
    }
    
    /**
     * 写入日志
     */
    fun writeLog(type: String, message: String) {
        val logFile = when (type) {
            "forest" -> getForestLogFile()
            "farm" -> getFarmLogFile()
            else -> getRuntimeLogFile()
        }
        
        try {
            val timestamp = java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date())
            
            logFile.appendText("[$timestamp] $message\n")
            
            // 同时写入到所有日志
            getAllLogFile().appendText("[$timestamp][$type] $message\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 清空日志
     */
    fun clearLog(type: String) {
        val logFile = when (type) {
            "forest" -> getForestLogFile()
            "farm" -> getFarmLogFile()
            "all" -> getAllLogFile()
            else -> return
        }
        
        try {
            logFile.writeText("")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 清空所有日志
     */
    fun clearAllLogs() {
        try {
            LOG_DIR.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "log") {
                    file.writeText("")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
