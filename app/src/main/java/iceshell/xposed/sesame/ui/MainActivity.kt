// 芝麻开花节节高
package iceshell.xposed.sesame.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import iceshell.xposed.sesame.BuildConfig
import iceshell.xposed.sesame.R
import iceshell.xposed.sesame.config.Config
import iceshell.xposed.sesame.util.FileHelper
import iceshell.xposed.sesame.util.ModuleHelper
import java.io.File

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化Compose视图
        initComposeView()
    }

    private fun initComposeView() {
        val composeView = findViewById<ComposeView>(R.id.device_info)
        composeView?.setContent {
            MaterialTheme {
                DeviceInfoCard()
            }
        }
    }
    
    @Composable
    private fun DeviceInfoCard() {
        val isActive = ModuleHelper.isModuleActive()
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isActive) "✅ 模块已激活" else "❌ 模块未激活",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "版本: ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "芝麻开花节节高！",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
    
    /**
     * 处理onClick事件（供XML布局使用）
     */
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_forest_log -> viewLog("forest")
            R.id.btn_farm_log -> viewLog("farm")
            R.id.btn_view_all_log_file -> viewLog("all")
            R.id.btn_view_error_log_file -> viewLog("error")
            R.id.btn_settings -> showSettingsDialog()
            R.id.btn_github -> openGithub()
            R.id.one_word -> showModuleInfo()
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
            "error" -> FileHelper.getRuntimeLogFile()
            else -> FileHelper.getAllLogFile()
        }

        if (logFile.exists() && logFile.length() > 0) {
            try {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        logFile
                    )
                } else {
                    Uri.fromFile(logFile)
                }
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "text/plain")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(intent, "选择查看方式"))
                } else {
                    Toast.makeText(this, "未找到可打开日志的应用", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "打开日志失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "日志文件不存在或为空", Toast.LENGTH_SHORT).show()
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
}
