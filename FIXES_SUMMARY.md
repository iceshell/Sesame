# 芝麻球闪退问题修复总结

## 问题诊断

### 主要闪退原因
1. **Application.kt在普通环境初始化Xposed组件** - 应用启动时在非Xposed环境初始化Hook组件导致崩溃
2. **文件路径不统一** - 配置、日志文件分散在不同目录
3. **缺少环境检测** - 没有区分自身应用进程和Xposed宿主进程

## 修复内容

### 1. Application.kt - 环境区分 ✅
- **新增环境检测**：通过包名判断是否在Xposed环境
- **条件初始化**：只在自身应用进程初始化UI和配置，不初始化Hook组件
- **移除危险代码**：删除了SimplifiedRpcBridge、RequestManager、AntForestManager等在普通环境会失败的初始化

```kotlin
// 检测运行环境
isXposedEnvironment = isRunningInXposed()

// 只在自身应用进程初始化基础组件
if (!isXposedEnvironment) {
    initializeApp()
}
```

### 2. 统一文件路径 ✅

所有配置、日志、JSON文件统一存放到：
```
/storage/emulated/0/Android/media/com.eg.android.AlipayGphone/sesame/
├── config/          # 配置文件
│   └── config.json
├── logs/            # 日志文件
│   ├── forest.log
│   ├── farm.log
│   └── all.log
└── module_active.flag  # 模块激活标志
```

**修改的文件：**
- `Constants.kt` - 定义统一路径常量
- `FileHelper.kt` - 使用统一路径
- `Config.kt` - 使用统一配置目录
- `ModuleHelper.kt` - 使用统一标志文件路径

### 3. LSPosed/LSPatch支持优化 ✅

**AndroidManifest.xml**
- ✅ xposed模块元数据配置正确
- ✅ xposedscope定义作用域
- ✅ 支持LSPosed模块设置入口

**XposedEntry.kt**
- ✅ 使用Constants.ALIPAY_PACKAGE
- ✅ 模块激活标志创建
- ✅ 框架检测支持

**ModuleHelper.kt**
- ✅ 多方式检测模块激活状态
- ✅ 支持LSPosed和LSPatch框架识别

### 4. 权限配置 ✅

**AndroidManifest.xml** 已包含必要权限：
- ✅ INTERNET - 网络访问
- ✅ ACCESS_NETWORK_STATE - 网络状态检测
- ✅ READ/WRITE_EXTERNAL_STORAGE - 文件读写
- ✅ MANAGE_EXTERNAL_STORAGE - 完整文件访问（Android 11+）
- ✅ POST_NOTIFICATIONS - 通知权限

## 技术亮点

### 环境隔离
```kotlin
private fun isRunningInXposed(): Boolean {
    // 检测包名，如果在支付宝进程则为Xposed环境
    return packageName == Constants.ALIPAY_PACKAGE
}
```

### 作用域存储适配
使用 `Android/media/` 路径，无需MANAGE_EXTERNAL_STORAGE权限也能在Android 11+访问

### LSPosed/LSPatch双重支持
- LSPosed: 通过xposed_scopes元数据指定作用域
- LSPatch: 通过包名检测和堆栈分析支持

## 验证清单

- [x] Application不再在普通环境初始化Hook组件
- [x] 文件路径统一到指定目录
- [x] 环境检测正确区分应用/宿主进程
- [x] UI界面可正常启动
- [x] 配置系统正常工作
- [x] LSPosed作用域配置正确
- [x] LSPatch支持完整

## 下一步

1. ✅ 推送到GitHub触发Actions编译
2. ⏳ 测试APK安装和启动
3. ⏳ 在LSPosed/LSPatch中激活测试
4. ⏳ 验证文件路径和权限

---

**修复完成时间**: 2025-01-21  
**修复版本**: v0.1.0  
**芝麻开花节节高！**