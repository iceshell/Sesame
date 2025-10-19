package iceshell.xposed.sesame.hook.rpc

import iceshell.xposed.sesame.entity.RpcEntity
import iceshell.xposed.sesame.util.CoroutineUtils
import iceshell.xposed.sesame.util.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * 简化的 RPC 桥接实现
 * 用于项目编译和基础功能测试
 * 
 * 注意：这是一个框架实现，真正的 Hook 逻辑需要根据支付宝版本动态实现
 */
class SimplifiedRpcBridge : RpcBridge {
    
    private val TAG = "SimplifiedRpcBridge"
    
    // 请求去重
    private val pendingRequests = ConcurrentHashMap<String, RpcEntity>()
    private val requestLocks = ConcurrentHashMap<String, ReentrantLock>()
    
    // 错误标记
    private val errorMark = setOf("1004", "1009", "2000", "46", "48")
    private val errorStringMark = setOf("繁忙", "拒绝", "网络不可用", "重试")
    
    // 静默错误的方法
    private val silentErrorMethods = setOf(
        "com.alipay.adexchange.ad.facade.xlightPlugin",
        "alipay.antforest.forest.h5.takeLook"
    )
    
    @Volatile
    private var isLoaded = false
    
    override fun getVersion(): String {
        return "Simplified-1.0"
    }
    
    @Throws(Exception::class)
    override fun load() {
        try {
            Logger.system(TAG, "初始化简化版 RPC 桥接")
            
            // TODO: 实现真正的 Xposed Hook 逻辑
            // 1. 获取 ClassLoader
            // 2. 找到支付宝的 RPC 类
            // 3. Hook RPC 方法
            
            // 暂时标记为已加载（用于测试）
            isLoaded = true
            
            Logger.system(TAG, "RPC 桥接初始化完成（简化版）")
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "RPC 桥接初始化失败", e)
            throw e
        }
    }
    
    override fun unload() {
        isLoaded = false
        pendingRequests.clear()
        requestLocks.clear()
        Logger.system(TAG, "RPC 桥接已卸载")
    }
    
    /**
     * 生成请求签名用于去重
     */
    private fun generateRequestSignature(rpcEntity: RpcEntity): String {
        val method = rpcEntity.requestMethod ?: "unknown"
        val data = rpcEntity.requestData
        return "$method:${data?.hashCode() ?: 0}"
    }
    
    /**
     * 获取或创建请求锁
     */
    private fun getOrCreateLock(requestKey: String): ReentrantLock {
        return requestLocks.computeIfAbsent(requestKey) { ReentrantLock() }
    }
    
    /**
     * 清理已完成的请求
     */
    private fun cleanupRequest(requestKey: String) {
        pendingRequests.remove(requestKey)
        // 延迟清理锁
        Thread {
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            requestLocks.remove(requestKey)
        }.start()
    }
    
    /**
     * 检查是否应该显示错误日志
     */
    private fun shouldShowErrorLog(methodName: String?): Boolean {
        return methodName != null && !silentErrorMethods.contains(methodName)
    }
    
    override fun requestString(rpcEntity: RpcEntity, tryCount: Int, retryInterval: Int): String {
        val resRpcEntity = requestObject(rpcEntity, tryCount, retryInterval)
        return resRpcEntity?.responseString ?: ""
    }
    
    override fun requestObject(rpcEntity: RpcEntity, tryCount: Int, retryInterval: Int): RpcEntity {
        if (!isLoaded) {
            Logger.error(TAG, "RPC 桥接未初始化")
            return rpcEntity.apply { setError() }
        }
        
        // 请求去重
        val requestKey = generateRequestSignature(rpcEntity)
        val requestLock = getOrCreateLock(requestKey)
        
        requestLock.lock()
        try {
            // 检查是否有相同请求正在进行
            val pendingEntity = pendingRequests[requestKey]
            if (pendingEntity != null && !pendingEntity.hasResult) {
                Logger.debug(TAG, "检测到重复请求，等待: ${rpcEntity.requestMethod}")
                val startWait = System.currentTimeMillis()
                // 最多等待3秒
                while (!pendingEntity.hasResult && (System.currentTimeMillis() - startWait) < 3000) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }
                }
                if (pendingEntity.hasResult) {
                    Logger.debug(TAG, "复用请求结果: ${rpcEntity.requestMethod}")
                    return pendingEntity
                }
            }
            
            // 标记请求正在进行
            pendingRequests[requestKey] = rpcEntity
        } finally {
            requestLock.unlock()
        }
        
        try {
            // 执行请求（带重试）
            var currentTry = 0
            while (currentTry < tryCount) {
                currentTry++
                
                try {
                    // TODO: 实现真正的 RPC 调用
                    // 这里需要使用反射调用支付宝的 RPC 接口
                    
                    // 暂时返回模拟数据（用于测试编译）
                    val mockResponse = """{"success":true,"data":"mock"}"""
                    rpcEntity.setResponseObject(null, mockResponse)
                    
                    Logger.debug(TAG, "RPC 请求成功: ${rpcEntity.requestMethod}")
                    break
                    
                } catch (e: Exception) {
                    Logger.printStackTrace(TAG, "RPC 请求失败 (${currentTry}/${tryCount})", e)
                    
                    if (currentTry < tryCount) {
                        // 重试延迟
                        val delay = when {
                            retryInterval < 0 -> 600L + (Math.random() * 400).toLong()
                            retryInterval == 0 -> 0L
                            else -> retryInterval.toLong()
                        }
                        
                        if (delay > 0) {
                            CoroutineUtils.sleepCompat(delay)
                        }
                    } else {
                        // 最后一次尝试也失败了
                        rpcEntity.setError()
                        if (shouldShowErrorLog(rpcEntity.requestMethod)) {
                            Logger.error(TAG, "RPC 请求最终失败: ${rpcEntity.requestMethod}")
                        }
                    }
                }
            }
            
            return rpcEntity
            
        } finally {
            // 清理请求
            cleanupRequest(requestKey)
        }
    }
}
