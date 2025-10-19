package iceshell.xposed.sesame.hook

import android.Manifest
import androidx.annotation.RequiresPermission
import iceshell.xposed.sesame.entity.RpcEntity
import iceshell.xposed.sesame.hook.rpc.RpcBridge
import iceshell.xposed.sesame.util.Logger
import iceshell.xposed.sesame.util.NetworkUtils
import iceshell.xposed.sesame.util.CoroutineUtils
import org.json.JSONObject

/**
 * 请求管理器
 * 从 Sesame-TK3 迁移
 * 负责管理所有RPC请求
 */
object RequestManager {
    private const val TAG = "RequestManager"
    
    // RPC桥接实例（由ApplicationHook设置）
    var rpcBridge: RpcBridge? = null
    
    /**
     * 检查并处理RPC响应结果
     * 集成RpcErrorHandler进行错误统计和退避控制
     */
    private fun checkResult(result: String?, method: String?): String {
        val methodName = method ?: "unknown"
        
        // 检查接口是否被暂停（1009错误退避）
        if (RpcErrorHandler.isApiSuspended(methodName)) {
            Logger.debug(TAG, "接口[$methodName]被暂停中，跳过调用")
            return ""
        }
        
        // 处理 null 返回值
        if (result == null) {
            Logger.runtime(TAG, "RPC 返回 null: $methodName")
            RpcErrorHandler.recordApiFailure(methodName)
            return ""
        }
        
        // 检查是否为空字符串
        if (result.trim { it <= ' ' }.isEmpty()) {
            Logger.runtime(TAG, "RPC 返回空字符串: $methodName")
            RpcErrorHandler.recordApiFailure(methodName)
            return ""
        }
        
        // 尝试解析响应，检查错误码
        try {
            val jo = JSONObject(result)
            if (jo.has("error")) {
                val errorCode = jo.optString("error")
                when (errorCode) {
                    "1009" -> {
                        Logger.error(TAG, "接口[$methodName]触发1009错误，暂停10分钟")
                        RpcErrorHandler.recordApiFailure(methodName, 1009)
                        return ""
                    }
                    "1004" -> {
                        Logger.error(TAG, "接口[$methodName]触发1004错误（系统繁忙）")
                        RpcErrorHandler.recordApiFailure(methodName, 1004)
                        return result // 1004返回结果，由上层处理
                    }
                    else -> {
                        if (errorCode.isNotEmpty()) {
                            Logger.error(TAG, "接口[$methodName]返回错误: $errorCode")
                            RpcErrorHandler.recordApiFailure(methodName)
                        }
                    }
                }
            } else {
                // 无错误，记录成功
                RpcErrorHandler.recordApiSuccess(methodName)
            }
        } catch (e: Exception) {
            // JSON解析失败，可能不是JSON格式或格式不标准，视为成功
            RpcErrorHandler.recordApiSuccess(methodName)
        }
        
        return result
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getRpcBridge(): RpcBridge? {
        if (!NetworkUtils.isNetworkAvailable()) {
            Logger.record("网络未连接，等待5秒")
            CoroutineUtils.sleepCompat(5000)
            if (!NetworkUtils.isNetworkAvailable()) {
                val networkType = NetworkUtils.getNetworkType()
                Logger.record("网络仍未连接，当前网络类型: $networkType，放弃本次请求...")
                return null
            }
        }
        var bridge = rpcBridge
        if (bridge == null) {
            Logger.record("RequestManager.rpcBridge 为空，等待5秒")
            CoroutineUtils.sleepCompat(5000)
            bridge = rpcBridge
        }
        return bridge
    }

    @JvmStatic
    fun requestString(rpcEntity: RpcEntity): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(rpcEntity, 3, 1200)
        return checkResult(result, rpcEntity.requestMethod)
    }

    @JvmStatic
    fun requestString(rpcEntity: RpcEntity, tryCount: Int, retryInterval: Int): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(rpcEntity, tryCount, retryInterval)
        return checkResult(result, rpcEntity.requestMethod)
    }

    @JvmStatic
    fun requestString(method: String?, data: String?): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(method ?: "", data ?: "")
        return checkResult(result, method)
    }

    @JvmStatic
    fun requestString(method: String?, data: String?, relation: String?): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(method ?: "", data ?: "", relation ?: "")
        return checkResult(result, method)
    }

    @JvmStatic
    fun requestString(method: String?, data: String?, appName: String?, methodName: String?, facadeName: String?): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(method ?: "", data ?: "", appName ?: "", methodName ?: "", facadeName ?: "")
        return checkResult(result, method)
    }

    @JvmStatic
    fun requestString(method: String?, data: String?, tryCount: Int, retryInterval: Int): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(method ?: "", data ?: "", tryCount, retryInterval)
        return checkResult(result, method)
    }

    @JvmStatic
    fun requestString(method: String?, data: String?, relation: String?, tryCount: Int, retryInterval: Int): String {
        val bridge = getRpcBridge() ?: return ""
        val result = bridge.requestString(method ?: "", data ?: "", relation ?: "", tryCount, retryInterval)
        return checkResult(result, method)
    }

    @JvmStatic
    fun requestObject(rpcEntity: RpcEntity?, tryCount: Int, retryInterval: Int): RpcEntity? {
        val bridge = getRpcBridge() ?: return null
        return bridge.requestObject(rpcEntity ?: return null, tryCount, retryInterval)
    }
}
