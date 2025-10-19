package iceshell.xposed.sesame.util

import android.util.Log as AndroidLog
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * 日志工具类
 * 从 Sesame-TK3 迁移并优化
 */
object Logger {
    private const val TAG = "Sesame"
    
    // 错误去重机制：记录错误特征和出现次数
    private val errorCountMap = ConcurrentHashMap<String, AtomicInteger>()
    private const val MAX_DUPLICATE_ERRORS = 3 // 最多打印3次相同错误
    
    @JvmStatic
    fun system(msg: String) {
        AndroidLog.i(TAG, msg)
    }
    
    @JvmStatic
    fun system(tag: String, msg: String) {
        AndroidLog.i("$TAG-$tag", msg)
    }
    
    @JvmStatic
    fun runtime(msg: String) {
        system(msg)
    }
    
    @JvmStatic
    fun runtime(tag: String, msg: String) {
        system(tag, msg)
    }
    
    @JvmStatic
    fun record(msg: String) {
        runtime(msg)
    }
    
    @JvmStatic
    fun record(tag: String, msg: String) {
        runtime(tag, msg)
    }
    
    @JvmStatic
    fun forest(msg: String) {
        record(msg)
        AndroidLog.i("$TAG-Forest", msg)
    }
    
    @JvmStatic
    fun forest(tag: String, msg: String) {
        forest("[$tag]: $msg")
    }
    
    @JvmStatic
    fun farm(msg: String) {
        record(msg)
        AndroidLog.i("$TAG-Farm", msg)
    }
    
    @JvmStatic
    fun farm(tag: String, msg: String) {
        farm("[$tag]: $msg")
    }
    
    @JvmStatic
    fun other(msg: String) {
        record(msg)
        AndroidLog.i("$TAG-Other", msg)
    }
    
    @JvmStatic
    fun other(tag: String, msg: String) {
        other("[$tag]: $msg")
    }
    
    @JvmStatic
    fun debug(msg: String) {
        AndroidLog.d(TAG, msg)
    }
    
    @JvmStatic
    fun debug(tag: String, msg: String) {
        AndroidLog.d("$TAG-$tag", msg)
    }
    
    @JvmStatic
    fun error(msg: String) {
        runtime(msg)
        AndroidLog.e(TAG, msg)
    }
    
    @JvmStatic
    fun error(tag: String, msg: String) {
        error("[$tag]: $msg")
    }
    
    @JvmStatic
    fun capture(msg: String) {
        AndroidLog.i("$TAG-Capture", msg)
    }
    
    @JvmStatic
    fun capture(tag: String, msg: String) {
        capture("[$tag]: $msg")
    }
    
    /**
     * 检查是否应该打印此错误（去重机制）
     */
    private fun shouldPrintError(th: Throwable?): Boolean {
        if (th == null) return true
        
        // 提取错误特征
        val errorSignature = th.javaClass.simpleName + ":" +
                (th.message?.take(50) ?: "null")
        
        // 特殊处理：JSON解析空字符串错误
        val signature = if (th.message?.contains("End of input at character 0") == true) {
            "JSONException:EmptyResponse"
        } else {
            errorSignature
        }
        
        val count = errorCountMap.computeIfAbsent(signature) { AtomicInteger(0) }
        val currentCount = count.incrementAndGet()
        
        // 如果是第3次，记录汇总信息
        if (currentCount == MAX_DUPLICATE_ERRORS) {
            runtime("⚠️ 错误【$signature】已出现${currentCount}次，后续将不再打印详细堆栈")
            return false
        }
        
        // 超过最大次数后不再打印
        return currentCount <= MAX_DUPLICATE_ERRORS
    }
    
    @JvmStatic
    fun printStackTrace(th: Throwable) {
        if (!shouldPrintError(th)) return
        val stackTrace = "error: " + AndroidLog.getStackTraceString(th)
        error(stackTrace)
    }
    
    @JvmStatic
    fun printStackTrace(msg: String, th: Throwable) {
        if (!shouldPrintError(th)) return
        val stackTrace = "Throwable error: " + AndroidLog.getStackTraceString(th)
        error(msg, stackTrace)
    }
    
    @JvmStatic
    fun printStackTrace(tag: String, msg: String, th: Throwable) {
        if (!shouldPrintError(th)) return
        val stackTrace = "[$tag] Throwable error: " + AndroidLog.getStackTraceString(th)
        error(msg, stackTrace)
    }
    
    @JvmStatic
    fun printStackTrace(e: Exception) {
        if (!shouldPrintError(e)) return
        val stackTrace = "Exception error: " + AndroidLog.getStackTraceString(e)
        error(stackTrace)
    }
    
    @JvmStatic
    fun printStackTrace(msg: String, e: Exception) {
        if (!shouldPrintError(e)) return
        val stackTrace = "Exception error: " + AndroidLog.getStackTraceString(e)
        error(msg, stackTrace)
    }
    
    @JvmStatic
    fun printStackTrace(tag: String, msg: String, e: Exception) {
        if (!shouldPrintError(e)) return
        val stackTrace = "[$tag] Exception error: " + AndroidLog.getStackTraceString(e)
        error(msg, stackTrace)
    }
    
    /**
     * 清除错误计数缓存
     */
    @JvmStatic
    fun clearErrorCount() {
        errorCountMap.clear()
    }
    
    @JvmStatic
    fun printStack(tag: String) {
        val stackTrace = "stack: " + AndroidLog.getStackTraceString(Exception("获取当前堆栈$tag:"))
        system(stackTrace)
    }
}
