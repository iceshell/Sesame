package iceshell.xposed.sesame.feature.antforest

import iceshell.xposed.sesame.feature.antforest.data.FriendInfo
import iceshell.xposed.sesame.util.Logger
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger

/**
 * 蚁蜓森林管理器
 * 从 Sesame-TK3 迁移并优化
 * 负责蚁蜓森林的主要业务逻辑
 * 
 * 功能：
 * - 收取自己和好友的能量
 * - 查询好友排行榜
 * - 完成森林任务
 * - 使用道具和卡片
 */
class AntForestManager private constructor() {
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val collectCount = AtomicInteger(0)
    private var selfUserId: String? = null
    
    companion object {
        @Volatile
        private var INSTANCE: AntForestManager? = null
        
        @JvmStatic
        fun getInstance(): AntForestManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AntForestManager().also { INSTANCE = it }
            }
        }
        
        private const val TAG = "AntForestManager"
    }
    
    /**
     * 初始化
     */
    fun init() {
        AntForestRpcCall.init()
        Logger.system(TAG, "蚁蜓森林管理器初始化完成")
    }
    
    /**
     * 查询自己的主页
     * @return 是否成功
     */
    suspend fun queryHomePage(): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.forest("查询自己的主页")
            val result = AntForestRpcCall.queryHomePage()
            
            if (result.isNotEmpty()) {
                val jo = JSONObject(result)
                // 解析用户ID
                selfUserId = jo.optString("userId")
                
                // 解析能量球
                val bubbles = jo.optJSONArray("bubbles")
                if (bubbles != null && bubbles.length() > 0) {
                    Logger.forest("发现 ${bubbles.length()} 个能量球")
                    // TODO: 收取自己的能量
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "查询主页失败", e)
            false
        }
    }
    
    /**
     * 收集好友能量
     * @param userId 好友用户ID
     * @return 收取的能量值
     */
    suspend fun collectFriendEnergy(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            Logger.forest("开始收取好友[$userId]能量")
            
            // 查询好友主页
            val result = AntForestRpcCall.queryFriendHomePage(userId)
            if (result.isEmpty()) {
                Logger.forest("好友[$userId]主页查询失败")
                return@withContext 0
            }
            
            val jo = JSONObject(result)
            val bubbles = jo.optJSONArray("bubbles") ?: return@withContext 0
            
            var totalEnergy = 0
            val bubbleList = mutableListOf<Long>()
            
            // 解析能量球
            for (i in 0 until bubbles.length()) {
                val bubble = bubbles.getJSONObject(i)
                val canCollect = bubble.optBoolean("canCollect", false)
                if (canCollect) {
                    val bubbleId = bubble.optLong("id")
                    val energy = bubble.optInt("produceEnergy", 0)
                    if (bubbleId > 0 && energy > 0) {
                        bubbleList.add(bubbleId)
                        totalEnergy += energy
                    }
                }
            }
            
            // 收取能量
            if (bubbleList.isNotEmpty()) {
                Logger.forest("好友[$userId]发现 ${bubbleList.size} 个可收取能量球，总计 ${totalEnergy}g")
                
                for (bubbleId in bubbleList) {
                    val collectResult = AntForestRpcCall.collectEnergy("jiazhanglin", userId, bubbleId)
                    if (collectResult.isNotEmpty()) {
                        collectCount.incrementAndGet()
                        delay(500) // 避免请求过快
                    }
                }
            }
            
            totalEnergy
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "收取好友能量失败", e)
            0
        }
    }
    
    /**
     * 批量收集好友能量
     * @param userIds 好友用户ID列表
     * @return 收取结果映射
     */
    suspend fun collectFriendsEnergy(userIds: List<String>): Map<String, Int> = 
        withContext(Dispatchers.IO) {
            val results = mutableMapOf<String, Int>()
            
            Logger.forest("开始批量收取 ${userIds.size} 个好友的能量")
            
            userIds.forEach { userId ->
                val energy = collectFriendEnergy(userId)
                results[userId] = energy
                delay(1000) // 避免请求过快
            }
            
            val totalEnergy = results.values.sum()
            Logger.forest("批量收取完成，总计收取 ${totalEnergy}g 能量")
            
            results
        }
    
    /**
     * 查询好友排行榜
     * @return 好友信息列表
     */
    suspend fun queryRankingFriends(): List<FriendInfo> = withContext(Dispatchers.IO) {
        try {
            Logger.forest("查询好友排行榜")
            
            val result = AntForestRpcCall.queryFriendsEnergyRanking()
            if (result.isEmpty()) {
                return@withContext emptyList()
            }
            
            val jo = JSONObject(result)
            val friendRanking = jo.optJSONArray("friendRanking") ?: return@withContext emptyList()
            
            val friends = mutableListOf<FriendInfo>()
            for (i in 0 until friendRanking.length()) {
                val friend = friendRanking.getJSONObject(i)
                val userId = friend.optString("userId")
                val userName = friend.optString("displayName", "未知")
                val canCollect = friend.optBoolean("canCollectEnergy", false)
                
                if (userId.isNotEmpty()) {
                    friends.add(
                        FriendInfo(
                            userId = userId,
                            userName = userName,
                            canCollect = canCollect,
                            energyBubbles = emptyList()
                        )
                    )
                }
            }
            
            Logger.forest("查询到 ${friends.size} 个好友")
            friends
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "查询好友排行榜失败", e)
            emptyList()
        }
    }
    
    /**
     * 完成森林任务
     * @return 完成的任务数量
     */
    suspend fun completeForestTasks(): Int = withContext(Dispatchers.IO) {
        try {
            Logger.forest("开始完成森林任务")
            
            // 查询任务列表
            val result = AntForestRpcCall.queryTaskList()
            if (result.isEmpty()) {
                return@withContext 0
            }
            
            // TODO: 解析并完成任务
            
            0
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "完成森林任务失败", e)
            0
        }
    }
    
    /**
     * 森林签到
     */
    suspend fun vitalitySign(): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.forest("森林签到")
            val result = AntForestRpcCall.vitalitySign()
            result.isNotEmpty()
        } catch (e: Exception) {
            Logger.printStackTrace(TAG, "森林签到失败", e)
            false
        }
    }
    
    /**
     * 获取统计信息
     */
    fun getStatistics(): String {
        return "已收取 ${collectCount.get()} 次能量"
    }
    
    /**
     * 释放资源
     */
    fun release() {
        scope.cancel()
    }
}
