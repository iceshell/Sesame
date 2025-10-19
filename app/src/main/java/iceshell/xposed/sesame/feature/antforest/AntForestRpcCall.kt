package iceshell.xposed.sesame.feature.antforest

import iceshell.xposed.sesame.entity.RpcEntity
import iceshell.xposed.sesame.hook.RequestManager
import iceshell.xposed.sesame.util.Logger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

/**
 * 蚂蚁森林 RPC 调用类
 * 从 Sesame-TK3 完整迁移
 * 包含所有森林功能的API调用方法
 */
object AntForestRpcCall {
    private var VERSION = "20250813"
    private const val TAG = "AntForestRpcCall"

    @JvmStatic
    fun init() {
        // 暂时使用默认版本，后续可根据支付宝版本动态调整
        Logger.record(TAG, "使用API版本: $VERSION")
    }

    /**
     * 查询好友能量排行榜
     */
    @JvmStatic
    fun queryFriendsEnergyRanking(): String {
        return try {
            val arg = JSONObject().apply {
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("periodType", "total")
                put("rankType", "energyRank")
                put("version", VERSION)
            }
            val param = "[$arg]"
            val correlationLocal = JSONObject().apply {
                put("pathList", JSONArray().put("friendRanking").put("myself").put("totalDatas"))
            }
            val relationLocal = "[$correlationLocal]"
            RequestManager.requestString("alipay.antmember.forest.h5.queryEnergyRanking", param, relationLocal)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 批量获取好友能量信息（标准版）
     * 用途：批量查询多个好友的蚂蚁森林信息
     * 
     * @param userIdList 用户ID列表（JSONArray）
     * @return JSON字符串，包含 friendRanking 数组
     */
    @JvmStatic
    fun fillUserRobFlag(userIdList: JSONArray): String {
        return try {
            val arg = JSONObject().apply {
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("userIdList", userIdList)
            }
            val param = "[$arg]"
            val joRelationLocal = JSONObject().apply {
                put("pathList", JSONArray().put("friendRanking"))
            }
            val relationLocal = "[$joRelationLocal]"
            RequestManager.requestString("alipay.antforest.forest.h5.fillUserRobFlag", param, relationLocal)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 批量获取好友能量信息（增强版 - PK排行榜专用）
     */
    @JvmStatic
    fun fillUserRobFlag(userIdList: JSONArray, needFillUserInfo: Boolean): String {
        return try {
            val arg = JSONObject().apply {
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("userIdList", userIdList)
                put("needFillUserInfo", needFillUserInfo)
            }
            val param = "[$arg]"
            RequestManager.requestString("alipay.antforest.forest.h5.fillUserRobFlag", param)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 查询自己的主页
     */
    @JvmStatic
    @Throws(JSONException::class)
    fun queryHomePage(): String {
        val requestObject = JSONObject()
            .put("activityParam", JSONObject())
            .put("configVersionMap", JSONObject().put("wateringBubbleConfig", "0"))
            .put("skipWhackMole", false)
            .put("source", "chInfo_ch_appcenter__chsub_9patch")
            .put("version", VERSION)
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.queryHomePage",
            JSONArray().put(requestObject).toString(),
            3,
            1000
        )
    }

    /**
     * 查询好友主页
     */
    @JvmStatic
    fun queryFriendHomePage(userId: String, fromAct: String? = "TAKE_LOOK_FRIEND"): String {
        return try {
            val arg = JSONObject().apply {
                put("canRobFlags", "T,F,F,F,F")
                put("configVersionMap", JSONObject().put("wateringBubbleConfig", "0"))
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("userId", userId)
                put("fromAct", fromAct)
                put("version", VERSION)
            }
            val param = "[$arg]"
            RequestManager.requestString("alipay.antforest.forest.h5.queryFriendHomePage", param, 3, 1000)
        } catch (e: Exception) {
            Logger.printStackTrace(e)
            ""
        }
    }

    /**
     * 找能量方法 - 查找可收取能量的好友
     *
     * @param skipUsers 跳过的用户列表
     */
    @JvmStatic
    fun takeLook(skipUsers: JSONObject): String {
        return try {
            val requestData = JSONObject().apply {
                put("contactsStatus", "N")
                put("exposedUserId", "")
                put("skipUsers", skipUsers)
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("takeLookEnd", false)
                put("takeLookStart", true)
                put("version", VERSION)
            }
            RequestManager.requestString("alipay.antforest.forest.h5.takeLook", "[$requestData]")
        } catch (e: JSONException) {
            Logger.printStackTrace(TAG, "takeLook构建请求参数失败", e)
            ""
        }
    }

    /**
     * 创建能量收取RPC实体
     */
    @JvmStatic
    fun energyRpcEntity(bizType: String, userId: String, bubbleId: Long): RpcEntity? {
        return try {
            val args = JSONObject().apply {
                put("bizType", bizType)
                put("bubbleIds", JSONArray().put(bubbleId))
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("userId", userId)
                put("version", VERSION)
            }
            val param = "[$args]"
            RpcEntity("alipay.antmember.forest.h5.collectEnergy", param, null)
        } catch (e: Exception) {
            Logger.printStackTrace(e)
            null
        }
    }

    /**
     * 收取能量
     */
    @JvmStatic
    fun collectEnergy(bizType: String, userId: String, bubbleId: Long): String {
        val r = energyRpcEntity(bizType, userId, bubbleId) ?: return ""
        return RequestManager.requestString(r)
    }

    /**
     * 批量收取能量RPC实体
     */
    @JvmStatic
    @Throws(JSONException::class)
    fun batchEnergyRpcEntity(bizType: String, userId: String, bubbleIds: List<Long>): RpcEntity {
        val arg = JSONObject().apply {
            put("bizType", bizType)
            put("bubbleIds", JSONArray(bubbleIds))
            put("fromAct", "BATCH_ROB_ENERGY")
            put("source", "chInfo_ch_appcenter__chsub_9patch")
            put("userId", userId)
            put("version", VERSION)
        }
        val param = "[$arg]"
        return RpcEntity("alipay.antmember.forest.h5.collectEnergy", param)
    }

    /**
     * 收取复活能量
     */
    @JvmStatic
    fun collectRebornEnergy(): String {
        return try {
            val arg = JSONObject().apply {
                put("source", "chInfo_ch_appcenter__chsub_9patch")
            }
            val param = "[$arg]"
            RequestManager.requestString("alipay.antforest.forest.h5.collectRebornEnergy", param)
        } catch (e: Exception) {
            Logger.printStackTrace(e)
            ""
        }
    }

    /**
     * 转赠能量
     */
    @JvmStatic
    fun transferEnergy(targetUser: String, bizNo: String, energyId: Int, notifyFriend: Boolean): String {
        return try {
            val arg = JSONObject().apply {
                put("bizNo", bizNo + UUID.randomUUID().toString())
                put("energyId", energyId)
                put("extInfo", JSONObject().put("sendChat", if (notifyFriend) "Y" else "N"))
                put("from", "friendIndex")
                put("source", "chInfo_ch_appcenter__chsub_9patch")
                put("targetUser", targetUser)
                put("transferType", "WATERING")
                put("version", VERSION)
            }
            val param = "[$arg]"
            RequestManager.requestString("alipay.antmember.forest.h5.transferEnergy", param)
        } catch (e: Exception) {
            Logger.printStackTrace(e)
            ""
        }
    }

    /**
     * 帮好友收取能量
     */
    @JvmStatic
    fun forFriendCollectEnergy(targetUserId: String, bubbleId: Long): String {
        val args1 = "[{\"bubbleIds\":[$bubbleId],\"targetUserId\":\"$targetUserId\"}]"
        return RequestManager.requestString("alipay.antmember.forest.h5.forFriendCollectEnergy", args1)
    }

    /**
     * 森林签到
     */
    @JvmStatic
    fun vitalitySign(): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.vitalitySign",
            "[{\"source\":\"chInfo_ch_appcenter__chsub_9patch\"}]"
        )
    }

    /**
     * 查询能量雨主页
     */
    @JvmStatic
    fun queryEnergyRainHome(): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.queryEnergyRainHome",
            "[{\"source\":\"senlinguangchuangrukou\",\"version\":\"$VERSION\"}]"
        )
    }

    /**
     * 查询能量雨可赠送列表
     */
    @JvmStatic
    fun queryEnergyRainCanGrantList(): String {
        return RequestManager.requestString("alipay.antforest.forest.h5.queryEnergyRainCanGrantList", "[{}]")
    }

    /**
     * 赠送能量雨机会
     */
    @JvmStatic
    fun grantEnergyRainChance(targetUserId: String): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.grantEnergyRainChance",
            "[{\"targetUserId\":$targetUserId}]"
        )
    }

    /**
     * 开始能量雨
     */
    @JvmStatic
    fun startEnergyRain(): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.startEnergyRain",
            "[{\"version\":\"$VERSION\"}]"
        )
    }

    /**
     * 能量雨结算
     */
    @JvmStatic
    fun energyRainSettlement(saveEnergy: Int, token: String): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.energyRainSettlement",
            "[{\"activityPropNums\":0,\"saveEnergy\":$saveEnergy,\"token\":\"$token\",\"version\":\"$VERSION\"}]"
        )
    }

    /**
     * 查询任务列表
     */
    @JvmStatic
    @Throws(JSONException::class)
    fun queryTaskList(): String {
        val jo = JSONObject().apply {
            put("extend", JSONObject())
            put("fromAct", "home_task_list")
            put("source", "chInfo_ch_appcenter__chsub_9patch")
            put("version", VERSION)
        }
        return RequestManager.requestString("alipay.antforest.forest.h5.queryTaskList", JSONArray().put(jo).toString())
    }

    /**
     * 查询背包道具列表
     */
    @JvmStatic
    @Throws(JSONException::class)
    fun queryPropList(onlyGive: Boolean): String {
        val jo = JSONObject().apply {
            put("onlyGive", if (onlyGive) "Y" else "")
            put("source", "chInfo_ch_appcenter__chsub_9patch")
            put("version", VERSION)
        }
        return RequestManager.requestString("alipay.antforest.forest.h5.queryPropList", JSONArray().put(jo).toString())
    }

    /**
     * 使用道具
     */
    @JvmStatic
    @Throws(JSONException::class)
    fun consumeProp(propId: String, propType: String): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.consumeProp",
            "[{\"propId\":\"$propId\",\"propType\":\"$propType\",\"source\":\"chInfo_ch_appcenter__chsub_9patch\",\"timezoneId\":\"Asia/Shanghai\",\"version\":\"$VERSION\"}]"
        )
    }

    /**
     * 复活能量
     */
    @JvmStatic
    fun protectBubble(targetUserId: String): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.protectBubble",
            "[{\"source\":\"ANT_FOREST_H5\",\"targetUserId\":\"$targetUserId\",\"version\":\"$VERSION\"}]"
        )
    }

    /**
     * 收取好友礼盒
     */
    @JvmStatic
    fun collectFriendGiftBox(targetId: String, targetUserId: String): String {
        return RequestManager.requestString(
            "alipay.antforest.forest.h5.collectFriendGiftBox",
            "[{\"source\":\"chInfo_ch_appcenter__chsub_9patch\",\"targetId\":\"$targetId\",\"targetUserId\":\"$targetUserId\"}]"
        )
    }

    /**
     * 根据道具类型获取道具组
     */
    @JvmStatic
    fun getPropGroup(propType: String): String {
        return when {
            propType.contains("SHIELD") -> "shield"
            propType.contains("DOUBLE_CLICK") -> "doubleClick"
            propType.contains("STEALTH") -> "stealthCard"
            propType.contains("BOMB_CARD") || propType.contains("NO_EXPIRE") -> "energyBombCard"
            propType.contains("ROB_EXPAND") -> "robExpandCard"
            propType.contains("BUBBLE_BOOST") -> "boost"
            else -> ""
        }
    }
}
