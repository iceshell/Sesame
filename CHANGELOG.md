# 更新日志 / Changelog

## [0.1.0] - 2025-01-XX

### 🎉 重大更新

#### 构建系统升级
- ✅ **Android Gradle Plugin**: 8.7.3 → **8.13.0**（最新稳定版）
- ✅ **Gradle**: 9.1.0（最新稳定版）
- ✅ **Kotlin**: 2.2.20（最新版本）
- ✅ **compileSdk/targetSdk**: 35（Android 15）

#### 依赖更新
- ✅ 添加 **Jetpack Compose** 支持
  - compose-bom: 2024.12.01
  - Material3 UI 组件
  - Compose Compiler Plugin
- ✅ 添加 **Kotlin Compose Plugin** 2.2.20
- ✅ 更新核心依赖至最新稳定版

### 🐛 Bug 修复

#### 闪退问题修复
1. **FileProvider 配置**
   - 添加 FileProvider 声明到 AndroidManifest.xml
   - 创建 `file_paths.xml` 配置文件
   - 修复 Android 7.0+ FileUriExposedException

2. **MainActivity 重构**
   - 添加 `onClick(View)` 方法处理 XML onClick 事件
   - 使用 Compose 实现设备信息卡片
   - 修复日志查看功能，支持 FileProvider
   - 添加 Intent chooser 提升用户体验

3. **资源修复**
   - 修复 `active_text` 颜色值（#DCDCDC → #00C853，更清晰可见）
   - 确保所有布局资源正确引用

### 🎨 UI 改进
- ✅ 使用 Jetpack Compose 实现现代化 UI 组件
- ✅ 模块状态卡片使用 Material3 设计
- ✅ 优化颜色方案，提升可读性

### 🔧 配置优化
- ✅ 启用 Compose 构建特性
- ✅ 保持 Java 17 兼容性
- ✅ 优化 Gradle 构建配置
- ✅ 支持 GitHub Actions 自动构建

### 📦 构建输出
- ✅ 本地构建成功：`app-release.apk`
- ✅ GitHub Actions 自动化构建配置完善
- ✅ 支持自动发布 Release

---

## 技术细节

### 环境要求
- **Android Studio**: 2025.1.4.8 或更高
- **SDK**: Android SDK 35
- **JDK**: 17 或更高
- **Gradle**: 9.1.0

### 已验证功能
- ✅ APK 成功编译
- ✅ FileProvider 正常工作
- ✅ UI 界面正常显示
- ✅ 日志查看功能正常

### 待验证功能
- ⏳ Xposed 模块激活状态检测
- ⏳ 蚂蚁森林收能量功能
- ⏳ 蚂蚁庄园喂养功能
- ⏳ 配置保存和加载

---

**芝麻开花节节高！** 🌱
