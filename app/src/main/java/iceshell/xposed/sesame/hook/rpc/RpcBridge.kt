package iceshell.xposed.sesame.hook.rpc

import iceshell.xposed.sesame.entity.RpcEntity

/**
 * RPC桥接接口
 * 从 Sesame-TK3 迁移
 */
interface RpcBridge {
    
    fun getVersion(): String
    
    @Throws(Exception::class)
    fun load()
    
    fun unload()

    fun requestString(rpcEntity: RpcEntity, tryCount: Int, retryInterval: Int): String
    
    fun requestObject(rpcEntity: RpcEntity, tryCount: Int, retryInterval: Int): RpcEntity

    fun requestString(rpcEntity: RpcEntity): String {
        return requestString(rpcEntity, 3, -1)
    }

    /**
     * 发送RPC请求并获取响应字符串（使用默认重试参数）
     */
    fun requestString(method: String, data: String): String {
        return requestString(method, data, 3, 1500)
    }

    /**
     * 发送带关联数据的RPC请求并获取响应字符串
     */
    fun requestString(method: String, data: String, relation: String): String {
        return requestString(method, data, relation, 3, 1500)
    }
    
    fun requestString(method: String, data: String, appName: String, methodName: String, facadeName: String): String {
        return requestString(RpcEntity(method, data, null, appName, methodName, facadeName), 3, -1)
    }
    
    fun requestString(method: String, data: String, tryCount: Int, retryInterval: Int): String {
        return requestString(RpcEntity(method, data), tryCount, retryInterval)
    }
    
    fun requestString(method: String, data: String, relation: String, tryCount: Int, retryInterval: Int): String {
        return requestString(RpcEntity(method, data, relation), tryCount, retryInterval)
    }

    fun requestObject(method: String, data: String, relation: String): RpcEntity {
        return requestObject(method, data, relation, 3, -1)
    }
    
    fun requestObject(method: String, data: String, tryCount: Int, retryInterval: Int): RpcEntity {
        return requestObject(RpcEntity(method, data), tryCount, retryInterval)
    }
    
    fun requestObject(method: String, data: String, relation: String, tryCount: Int, retryInterval: Int): RpcEntity {
        return requestObject(RpcEntity(method, data, relation), tryCount, retryInterval)
    }
}
