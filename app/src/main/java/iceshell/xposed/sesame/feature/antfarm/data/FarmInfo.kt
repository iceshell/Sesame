package iceshell.xposed.sesame.feature.antfarm.data

/**
 * 庄园信息数据模型
 * 
 * @property farmId 庄园ID
 * @property chickenCount 小鸡数量
 * @property foodStock 饲料库存
 * @property eggCount 鸡蛋数量
 * @property updateTime 更新时间（时间戳）
 */
data class FarmInfo(
    val farmId: String,
    val chickenCount: Int,
    val foodStock: Int,
    val eggCount: Int,
    val updateTime: Long = System.currentTimeMillis()
)
