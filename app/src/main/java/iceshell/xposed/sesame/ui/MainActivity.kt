// 芝麻开花节节高
package iceshell.xposed.sesame.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iceshell.xposed.sesame.R
import iceshell.xposed.sesame.config.Config
import iceshell.xposed.sesame.util.FileHelper
import iceshell.xposed.sesame.util.ModuleHelper

/**
 * 主界面Activity
 * 
 * 功能:
 * - 显示模块激活状态
 * - 提供功能配置入口
 * - 查看日志
 * - 访问GitHub
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnForestLog: Button
    private lateinit var btnFarmLog: Button
    private lateinit var btnAllLog: Button
    private lateinit var btnSettings: Button
    private lateinit var btnGithub: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        updateModuleStatus()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        btnForestLog = findViewById(R.id.btn_forest_log)
        btnFarmLog = findViewById(R.id.btn_farm_log)
        btnAllLog = findViewById(R.id.btn_all_log)
        btnSettings = findViewById(R.id.btn_settings)
        btnGithub = findViewById(R.id.btn_github)
    }

    private fun setupListeners() {
        // 森林日志
        btnForestLog.setOnClickListener {
            viewLog("forest")
        }

        // 庄园日志
        btnFarmLog.setOnClickListener {
            viewLog("farm")
        }

        // 所有日志
        btnAllLog.setOnClickListener {
            viewLog("all")
        }

        // 设置按钮 - 打开设置选择
        btnSettings.setOnClickListener {
            showSettingsDialog()
        }

        // 访问GitHub
        btnGithub.setOnClickListener {
            openGithub()
        }

        // 长按状态显示详细信息
        tvStatus.setOnLongClickListener {
            showModuleInfo()
            true
        }
    }
    
    /**
     * 显示设置选择对话框
     */
    private fun showSettingsDialog() {
        val options = arrayOf("森林配置", "庄园配置")
        AlertDialog.Builder(this)
            .setTitle("请选择配置")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openSettings("forest")
                    1 -> openSettings("farm")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 更新模块激活状态
     */
    private fun updateModuleStatus() {
        val isActive = ModuleHelper.isModuleActive()
        
        if (isActive) {
            tvStatus.text = "✅ 模块已激活"
            tvStatus.setTextColor(getColor(R.color.active_text))
        } else {
            tvStatus.text = "❌ 模块未激活"
            tvStatus.setTextColor(getColor(R.color.not_active_text))
        }
    }

    /**
     * 打开设置界面
     */
    private fun openSettings(type: String) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    /**
     * 查看日志
     */
    private fun viewLog(type: String) {
        val logFile = when (type) {
            "forest" -> FileHelper.getForestLogFile()
            "farm" -> FileHelper.getFarmLogFile()
            else -> FileHelper.getAllLogFile()
        }

        if (logFile.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(logFile), "text/plain")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "未找到可打开日志的应用", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "日志文件不存在", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 打开GitHub
     */
    private fun openGithub() {
        val url = "https://github.com/iceshell/Sesame"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "未找到可用的浏览器", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 显示模块详细信息
     */
    private fun showModuleInfo() {
        val info = buildString {
            appendLine("芝麻球 - 蚂蚁森林和蚂蚁庄园自动化模块")
            appendLine()
            appendLine("模块状态: ${if (ModuleHelper.isModuleActive()) "已激活" else "未激活"}")
            appendLine("版本: ${getVersionName()}")
            appendLine()
            appendLine("支持框架:")
            appendLine("- LSPosed ✅")
            appendLine("- LSPatch ✅")
            appendLine()
            appendLine("配置文件: ${Config.getConfigFile().absolutePath}")
            appendLine()
            appendLine("芝麻开花节节高！")
        }

        AlertDialog.Builder(this)
            .setTitle("模块信息")
            .setMessage(info)
            .setPositiveButton("确定", null)
            .show()
    }

    /**
     * 获取版本名称
     */
    private fun getVersionName(): String {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "未知"
        } catch (e: Exception) {
            "未知"
        }
    }

    override fun onResume() {
        super.onResume()
        updateModuleStatus()
    }
}
