package iceshell.xposed.sesame.feature.antforest.data

/**
 * 能量球数据模型
 * 
 * @property id 能量球ID
 * @property collected 是否已收取
 * @property createTime 创建时间（时间戳）
 * @property endTime 结束时间（时间戳）
 * @property value 能量值
 */
data class EnergyBubble(
    val id: Long,
    val collected: Boolean,
    val createTime: Long,
    val endTime: Long,
    val value: Int
)
