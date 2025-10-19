package iceshell.xposed.sesame.feature.antforest.data

/**
 * 好友信息数据模型
 * 
 * @property userId 用户ID
 * @property userName 用户名称
 * @property canCollect 是否可以收取能量
 * @property energyBubbles 能量球列表
 * @property updateTime 更新时间（时间戳）
 */
data class FriendInfo(
    val userId: String,
    val userName: String,
    val canCollect: Boolean,
    val energyBubbles: List<EnergyBubble>,
    val updateTime: Long = System.currentTimeMillis()
)
