package iceshell.xposed.sesame.hook

import iceshell.xposed.sesame.util.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * RPC错误处理器
 * 从 Sesame-TK3 迁移
 * 
 * 功能：
 * 1. 1009错误的退避机制（暂停10分钟）
 * 2. 动态并发控制
 * 3. 接口成功率统计
 */
object RpcErrorHandler {
    private const val TAG = "RpcErrorHandler"
    
    // ==================== 1009错误退避机制 ====================
    
    /**
     * 接口被暂停的截止时间（key: 接口名, value: 恢复时间戳）
     */
    private val suspendedApis = ConcurrentHashMap<String, Long>()
    
    /**
     * 1009错误暂停时长（10分钟）
     */
    private const val ERROR_1009_SUSPEND_DURATION = 10 * 60 * 1000L
    
    /**
     * 记录1009错误并暂停接口
     */
    @JvmStatic
    fun handle1009Error(apiMethod: String) {
        val resumeTime = System.currentTimeMillis() + ERROR_1009_SUSPEND_DURATION
        suspendedApis[apiMethod] = resumeTime
        
        val resumeTimeStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date(resumeTime))
        Logger.error(TAG, "接口[$apiMethod]触发1009错误，暂停10分钟至$resumeTimeStr")
    }
    
    /**
     * 检查接口是否被暂停
     */
    @JvmStatic
    fun isApiSuspended(apiMethod: String): Boolean {
        val resumeTime = suspendedApis[apiMethod] ?: return false
        val now = System.currentTimeMillis()
        
        if (now >= resumeTime) {
            // 已过暂停期，移除记录
            suspendedApis.remove(apiMethod)
            Logger.record(TAG, "接口[$apiMethod]暂停期结束，恢复调用")
            return false
        }
        
        val remainingTime = (resumeTime - now) / 1000
        Logger.debug(TAG, "接口[$apiMethod]仍在暂停中，剩余${remainingTime}秒")
        return true
    }
    
    /**
     * 获取被暂停的接口列表
     */
    @JvmStatic
    fun getSuspendedApis(): Map<String, Long> {
        return suspendedApis.filterValues { it > System.currentTimeMillis() }
    }
    
    // ==================== 接口成功率统计 ====================
    
    /**
     * 接口调用统计数据
     */
    data class ApiStats(
        var totalCalls: Long = 0,
        var successCalls: Long = 0,
        var failureCalls: Long = 0,
        var lastCallTime: Long = 0,
        var lastSuccessTime: Long = 0
    ) {
        val successRate: Double
            get() = if (totalCalls > 0) successCalls.toDouble() / totalCalls else 0.0
            
        val recentSuccessRate: Double
            get() {
                // 最近100次的成功率（用于动态调整）
                val recentCalls = minOf(totalCalls, 100)
                return if (recentCalls > 0) {
                    successCalls.toDouble() / recentCalls
                } else 0.0
            }
    }
    
    /**
     * 接口统计数据（key: 接口名）
     */
    private val apiStatsMap = ConcurrentHashMap<String, ApiStats>()
    
    /**
     * 记录接口调用成功
     */
    @JvmStatic
    fun recordApiSuccess(apiMethod: String) {
        val stats = apiStatsMap.computeIfAbsent(apiMethod) { ApiStats() }
        stats.totalCalls++
        stats.successCalls++
        stats.lastCallTime = System.currentTimeMillis()
        stats.lastSuccessTime = System.currentTimeMillis()
    }
    
    /**
     * 记录接口调用失败
     */
    @JvmStatic
    fun recordApiFailure(apiMethod: String, errorCode: Int? = null) {
        val stats = apiStatsMap.computeIfAbsent(apiMethod) { ApiStats() }
        stats.totalCalls++
        stats.failureCalls++
        stats.lastCallTime = System.currentTimeMillis()
        
        // 特殊处理1009错误
        if (errorCode == 1009) {
            handle1009Error(apiMethod)
        }
    }
    
    /**
     * 获取接口统计信息
     */
    @JvmStatic
    fun getApiStats(apiMethod: String): ApiStats? {
        return apiStatsMap[apiMethod]
    }
    
    /**
     * 获取所有接口统计
     */
    @JvmStatic
    fun getAllApiStats(): Map<String, ApiStats> {
        return apiStatsMap.toMap()
    }
    
    /**
     * 重置接口统计
     */
    @JvmStatic
    fun resetApiStats(apiMethod: String? = null) {
        if (apiMethod != null) {
            apiStatsMap.remove(apiMethod)
            Logger.record(TAG, "已重置接口[$apiMethod]的统计数据")
        } else {
            apiStatsMap.clear()
            Logger.record(TAG, "已重置所有接口的统计数据")
        }
    }
    
    // ==================== 动态并发控制 ====================
    
    /**
     * 当前并发数配置
     */
    private val currentConcurrency = AtomicLong(60) // 默认60
    
    /**
     * 根据成功率动态调整并发数
     * 
     * @param apiMethod 接口名（用于参考成功率）
     * @return 建议的并发数
     */
    @JvmStatic
    fun getDynamicConcurrency(apiMethod: String? = null): Int {
        // 如果有被暂停的接口，降低并发
        if (suspendedApis.isNotEmpty()) {
            return 30 // 有风控时降到30
        }
        
        // 根据接口成功率调整
        if (apiMethod != null) {
            val stats = apiStatsMap[apiMethod]
            if (stats != null && stats.totalCalls >= 10) {
                val successRate = stats.recentSuccessRate
                return when {
                    successRate >= 0.95 -> 60 // 成功率95%以上，全速
                    successRate >= 0.85 -> 45 // 成功率85-95%，略降
                    successRate >= 0.70 -> 30 // 成功率70-85%，降低
                    else -> 20 // 成功率低于70%，大幅降低
                }
            }
        }
        
        // 检查当前时间段
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 7..8, in 12..13, in 17..18 -> 40 // 高峰期降低到40
            else -> currentConcurrency.get().toInt()
        }
    }
    
    /**
     * 手动设置并发数
     */
    @JvmStatic
    fun setConcurrency(value: Int) {
        currentConcurrency.set(value.toLong())
        Logger.record(TAG, "并发数已调整为: $value")
    }
    
    /**
     * 清除错误计数缓存
     */
    @JvmStatic
    fun clearErrorCount() {
        suspendedApis.clear()
        apiStatsMap.clear()
        Logger.record(TAG, "已清除所有错误统计")
    }
}
