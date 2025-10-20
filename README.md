# 芝麻球 - Sesame Xposed 模块

> 芝麻开花节节高

[![Android CI](https://github.com/iceshell/Sesame/actions/workflows/android.yml/badge.svg)](https://github.com/iceshell/Sesame/actions/workflows/android.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-9.1-green.svg)](https://gradle.org)

## 项目简介

芝麻球（Sesame）是一个基于 Xposed 框架的 Android 模块，用于自动化管理支付宝的蚂蚁森林和蚂蚁庄园功能。

**芝麻开花节节高** - 让你的蚂蚁森林茁壮成长！

## 功能特性

- ✅ **蚂蚁森林自动收能量**：自动收取好友能量球
- ✅ **蚂蚁庄园自动喂鸡**：自动喂养小鸡和收集鸡蛋
- ✅ **任务自动完成**：自动完成森林任务
- ✅ **好友能量排行**：查询好友能量排行榜

## 技术栈

- **语言**: Kotlin 2.2.20
- **构建工具**: Gradle 9.1
- **最低 Android 版本**: Android 7.0 (API 24)
- **目标 Android 版本**: Android 15 (API 35)
- **框架**: Xposed Framework

## 构建说明

### 前置要求

- JDK 17+
- Android SDK
- Gradle 9.1+

### 本地构建

```bash
# 克隆项目
git clone https://github.com/iceshell/Sesame.git
cd Sesame

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

### GitHub Actions 自动构建

本项目已配置 GitHub Actions 自动构建，每次推送代码到 main 或 master 分支时会自动触发构建。

构建产物会自动上传到 Actions 的 Artifacts 中，保留 30 天。

## 项目结构

```
Sesame/
 app/                    # 主应用模块
    src/
       main/
           java/      # Kotlin 源代码
           res/       # Android 资源文件
           AndroidManifest.xml
    build.gradle.kts   # 应用级构建配置
    proguard-rules.pro # ProGuard 混淆规则
 antforest/             # 蚂蚁森林模块
 antfarm/               # 蚂蚁庄园模块
 network/               # 网络请求模块
 gradle/                # Gradle 配置
    libs.versions.toml # 依赖版本管理
    wrapper/           # Gradle Wrapper
 .github/               # GitHub Actions 配置
    workflows/
        android.yml    # CI/CD 工作流
 build.gradle.kts       # 项目级构建配置
 settings.gradle.kts    # 项目设置
 gradle.properties      # Gradle 属性配置
 README.md              # 🌱 Sesame - 芝麻粒

[![Build APK](https://github.com/iceshell/Sesame/actions/workflows/build.yml/badge.svg)](https://github.com/iceshell/Sesame/actions/workflows/build.yml)
[![GitHub release](https://img.shields.io/github/v/release/iceshell/Sesame)](https://github.com/iceshell/Sesame/releases)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)

基于 Xposed 框架的支付宝蚂蚁森林和蚂蚁庄园自动化模块（从 Sesame-TK3 完整迁移）

## ✨ 核心特性

### 🌳 蚂蚁森林
- ✅ 自动收取能量（自己+好友）
- ✅ 好友排行榜查询
- ✅ 能量雨自动化
- ✅ 森林任务自动完成
- ✅ 道具自动使用
- ✅ 智能错误处理（1009退避机制）

### 🏗️ 技术架构
- **语言**：100% Kotlin
- **架构**：Clean Architecture + Feature Module
- **异步**：Kotlin Coroutines
- **日志**：智能去重系统
- **RPC**：完整的40+ API方法

## 🚀 快速开始

### 下载APK

**方法1：从Releases下载**
```
https://github.com/iceshell/Sesame/releases
```

**方法2：从GitHub Actions下载**
```
https://github.com/iceshell/Sesame/actions
```

### 安装和激活

1. 安装 APK 到手机
2. 在 **LSPosed Manager** 中激活模块
3. 勾选作用域：**支付宝**
4. 重启支付宝

### 验证激活

```bash
adb logcat | findstr "Sesame"
```

预期输出：
```
[Sesame] Initializing Sesame v1.0.0
[Sesame] Network utils initialized
[Sesame] RPC bridge initialized
[Sesame] AntForest manager initialized
[Sesame] Sesame initialization completed successfully
```

## 🔧 本地编译

### 环境要求
- JDK 17+
- Android Studio Ladybug (2024.2.1+)
- Gradle 8.7+

### 编译命令

```powershell
# Windows
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease

# Linux/macOS
./gradlew assembleDebug
./gradlew assembleRelease

# 输出位置
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

## 📚 文档

| 文档 | 说明 |
|------|------|
| [MIGRATION_COMPLETE.md](MIGRATION_COMPLETE.md) | 详细的迁移报告（强烈推荐） |
| [BUILD_AND_DEPLOY.md](BUILD_AND_DEPLOY.md) | 完整的编译部署指南 |
| [QUICK_START.md](QUICK_START.md) | 快速开始指南 |

## 🎯 项目结构

```
app/src/main/java/iceshell/xposed/sesame/
├── core/                      # 核心模块
│   ├── Application.kt         # 应用入口
│   ├── Constants.kt           # 常量定义
│   └── XposedEntry.kt         # Xposed入口
├── hook/                      # Hook层
│   ├── RequestManager.kt      # 请求管理
│   ├── RpcErrorHandler.kt     # 错误处理
│   └── rpc/                   # RPC桥接
├── feature/                   # 功能模块
│   ├── antforest/             # 蚂蚁森林
│   │   ├── AntForestManager.kt
│   │   └── AntForestRpcCall.kt (40+ API)
│   └── antfarm/               # 蚂蚁庄园
├── util/                      # 工具类
│   ├── Logger.kt              # 智能日志
│   ├── CoroutineUtils.kt      # 协程工具
│   └── NetworkUtils.kt        # 网络工具
└── entity/                    # 实体类
    └── RpcEntity.kt
```

## 💡 核心亮点

### 1. 智能日志系统
```kotlin
Logger.forest("收取了 120g 能量")
Logger.error("API", "请求失败")
// 自动错误去重，避免刷屏
```

### 2. 完整的森林API
```kotlin
AntForestRpcCall.queryHomePage()
AntForestRpcCall.collectEnergy(bizType, userId, bubbleId)
AntForestRpcCall.vitalitySign()
// 40+ API方法
```

### 3. 智能错误处理
- ✅ 1009错误自动暂停10分钟
- ✅ 动态并发控制
- ✅ 接口成功率统计
- ✅ 请求去重机制

## 🛠️ 技术栈

| 技术 | 版本 |
|------|------|
| Kotlin | 2.2.20 |
| Android Gradle Plugin | 8.7.3 |
| compileSdk | 35 (Android 15) |
| minSdk | 24 (Android 7.0) |
| Xposed API | 82+ |
| Coroutines | 1.10.1 |

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

### 开发流程
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

- 项目主页：https://github.com/iceshell/Sesame
- Issue 反馈：https://github.com/iceshell/Sesame/issues
