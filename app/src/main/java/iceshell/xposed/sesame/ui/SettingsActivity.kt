// 芝麻开花节节高
package iceshell.xposed.sesame.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iceshell.xposed.sesame.R
import iceshell.xposed.sesame.config.Config

/**
 * 设置界面
 * 
 * 动态生成配置选项
 */
class SettingsActivity : AppCompatActivity() {

    private var settingsType: String = "forest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsType = intent.getStringExtra("type") ?: "forest"
        
        // 设置标题
        title = when (settingsType) {
            "forest" -> getString(R.string.forest_configuration)
            "farm" -> getString(R.string.farm_configuration)
            else -> getString(R.string.settings)
        }
        
        // 初始化配置
        Config.init()
        
        // 创建布局
        createLayout()
    }

    private fun createLayout() {
        val scrollView = android.widget.ScrollView(this)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // 根据类型添加配置选项
        when (settingsType) {
            "forest" -> addForestSettings(linearLayout)
            "farm" -> addFarmSettings(linearLayout)
        }

        scrollView.addView(linearLayout)
        setContentView(scrollView)
    }

    /**
     * 添加森林配置
     */
    private fun addForestSettings(layout: LinearLayout) {
        val config = Config.getForestConfig()

        addSwitch(layout, "enabled", "启用森林", config.optBoolean("enabled", true))
        addSwitch(layout, "collectEnergy", getString(R.string.collect_energy), config.optBoolean("collectEnergy", true))
        addSwitch(layout, "helpFriendCollect", getString(R.string.help_friend_collect), config.optBoolean("helpFriendCollect", true))
        addSwitch(layout, "energyRain", getString(R.string.energy_rain), config.optBoolean("energyRain", true))
        addSwitch(layout, "collectWateringBubble", getString(R.string.collect_watering_bubble), config.optBoolean("collectWateringBubble", true))
        addSwitch(layout, "collectProp", getString(R.string.collect_prop), config.optBoolean("collectProp", true))
        addSwitch(layout, "useDoubleCard", getString(R.string.use_double_card), config.optBoolean("useDoubleCard", false))
        addSwitch(layout, "receiveForestTaskAward", getString(R.string.receive_forest_task_award), config.optBoolean("receiveForestTaskAward", true))
        addSwitch(layout, "collectGiftBox", getString(R.string.collect_gift_box), config.optBoolean("collectGiftBox", true))
    }

    /**
     * 添加庄园配置
     */
    private fun addFarmSettings(layout: LinearLayout) {
        val config = Config.getFarmConfig()

        addSwitch(layout, "enabled", getString(R.string.enable_farm), config.optBoolean("enabled", true))
        addSwitch(layout, "feedAnimal", getString(R.string.feed_animal), config.optBoolean("feedAnimal", true))
        addSwitch(layout, "receiveFarmTaskAward", getString(R.string.receive_farm_task_award), config.optBoolean("receiveFarmTaskAward", true))
        addSwitch(layout, "sendBackAnimal", getString(R.string.send_back_animal), config.optBoolean("sendBackAnimal", true))
        addSwitch(layout, "rewardFriend", getString(R.string.reward_friend), config.optBoolean("rewardFriend", true))
    }

    /**
     * 添加开关
     */
    private fun addSwitch(layout: LinearLayout, key: String, label: String, defaultValue: Boolean) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val textView = TextView(this).apply {
            text = label
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val switch = Switch(this).apply {
            isChecked = defaultValue
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            
            setOnCheckedChangeListener { _, isChecked ->
                onSwitchChanged(key, isChecked)
            }
        }

        container.addView(textView)
        container.addView(switch)
        layout.addView(container)
    }

    /**
     * 开关状态变化
     */
    private fun onSwitchChanged(key: String, isChecked: Boolean) {
        when (settingsType) {
            "forest" -> Config.updateForestConfig(key, isChecked)
            "farm" -> Config.updateFarmConfig(key, isChecked)
        }

        Toast.makeText(
            this,
            "配置已保存: $key = $isChecked",
            Toast.LENGTH_SHORT
        ).show()
    }
}
