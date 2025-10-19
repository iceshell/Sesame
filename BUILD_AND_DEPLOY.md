# 🚀 Sesame 编译和部署指南

## ✅ 项目状态

**迁移状态**：✅ 完成  
**编译状态**：✅ 准备就绪  
**功能状态**：✅ 核心功能完整

---

## 📋 前置要求

### 开发环境
- ✅ **JDK 17** 或更高版本
- ✅ **Android Studio Ladybug** (2024.2.1) 或更新版本
- ✅ **Gradle 8.7.3**
- ✅ **Kotlin 2.2.20**

### Android 配置
- ✅ **compileSdk**: 35 (Android 15)
- ✅ **minSdk**: 24 (Android 7.0)
- ✅ **targetSdk**: 35 (Android 15)

### Xposed 框架
- ✅ **LSPosed** (推荐)
- ✅ **EdXposed**
- ✅ **Xposed API**: 82+

---

## 🔧 编译步骤

### 方法 1：使用 Android Studio（推荐）

1. **打开项目**
   ```bash
   # 启动 Android Studio
   # File -> Open -> 选择 D:\GitHub\Sesame
   ```

2. **同步 Gradle**
   ```
   File -> Sync Project with Gradle Files
   ```

3. **编译 Debug 版本**
   ```
   Build -> Build Bundle(s) / APK(s) -> Build APK(s)
   ```

4. **编译 Release 版本**
   ```
   Build -> Generate Signed Bundle / APK
   ```

### 方法 2：使用命令行

```powershell
# 进入项目目录
cd D:\GitHub\Sesame

# 清理项目
./gradlew clean

# 编译 Debug APK
./gradlew assembleDebug

# 编译 Release APK
./gradlew assembleRelease

# 输出位置
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

### 方法 3：使用 GitHub Actions（自动化）

项目已配置 GitHub Actions，每次 push 或 PR 都会自动编译。

查看编译状态：
```
https://github.com/YOUR_USERNAME/Sesame/actions
```

---

## 📦 APK 输出位置

编译成功后，APK 文件位于：

```
D:\GitHub\Sesame\app\build\outputs\apk\
├── debug/
│   └── app-debug.apk          # Debug 版本
└── release/
    └── app-release.apk        # Release 版本（需签名）
```

---

## 🔑 签名配置（Release 版本）

### 创建签名密钥

```bash
keytool -genkey -v -keystore sesame.jks -keyalg RSA -keysize 2048 -validity 10000 -alias sesame
```

### 配置签名信息

在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../sesame.jks")
            storePassword = "your_password"
            keyAlias = "sesame"
            keyPassword = "your_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

---

## 📲 安装和激活

### 1. 安装 APK

```bash
# 通过 adb 安装
adb install app-debug.apk

# 或直接在设备上安装
# 复制 APK 到手机，点击安装
```

### 2. 在 LSPosed 中激活

1. 打开 **LSPosed Manager**
2. 进入 **模块** 页面
3. 找到 **Sesame** 并启用
4. 勾选作用域：**支付宝**
5. 重启支付宝

### 3. 验证激活状态

```
打开支付宝 -> 查看日志输出
Logcat 过滤: "Sesame"
```

预期日志：
```
[Sesame] Initializing Sesame v1.0.0
[Sesame] Network utils initialized
[Sesame] RPC bridge initialized
[Sesame] AntForest manager initialized
[Sesame] Sesame initialization completed successfully
```

---

## 🧪 功能测试

### 测试蚂蚁森林功能

```kotlin
// 在支付宝中触发
// 1. 打开蚂蚁森林
// 2. 观察日志输出
// 3. 验证能量收取功能
```

### 查看日志

```bash
# 实时查看日志
adb logcat | grep "Sesame"

# 查看森林日志
adb logcat | grep "Sesame-Forest"

# 查看错误日志
adb logcat | grep "Sesame.*ERROR"
```

---

## ⚠️ 常见问题

### 1. 编译失败：找不到 JDK

**解决方案**：
```bash
# 设置 JAVA_HOME
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. Gradle 同步失败

**解决方案**：
```bash
# 清理 Gradle 缓存
./gradlew clean
rm -rf .gradle

# 重新同步
./gradlew build --refresh-dependencies
```

### 3. LSPosed 无法激活模块

**解决方案**：
- 检查 `xposed_init` 文件是否存在
- 确认 AndroidManifest.xml 中的 meta-data 配置
- 重启 LSPosed 框架

### 4. 模块激活但不工作

**解决方案**：
- 检查作用域是否包含支付宝
- 查看 Logcat 日志寻找错误
- 确认支付宝版本兼容性

---

## 🔍 调试技巧

### 启用详细日志

在 `Application.kt` 中：
```kotlin
Logger.system("Application", "Debug mode enabled")
```

### 使用 Logcat

```bash
# 过滤 Sesame 相关日志
adb logcat -s Sesame:V

# 保存日志到文件
adb logcat | grep "Sesame" > sesame.log
```

### 使用 Android Studio Profiler

```
View -> Tool Windows -> Profiler
```

---

## 📊 性能优化

### 编译优化

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true        // 启用代码压缩
            isShrinkResources = true      // 启用资源压缩
            proguardFiles(...)
        }
    }
}
```

### ProGuard 规则

在 `proguard-rules.pro` 中添加：

```proguard
# 保留 Xposed 相关类
-keep class de.robv.android.xposed.** { *; }
-keep class iceshell.xposed.sesame.core.XposedEntry { *; }

# 保留 Logger 类
-keep class iceshell.xposed.sesame.util.Logger { *; }

# 保留数据类
-keepclassmembers class iceshell.xposed.sesame.entity.** { *; }
```

---

## 🚀 持续集成/持续部署 (CI/CD)

### GitHub Actions 配置

`.github/workflows/build.yml` 已配置：

- ✅ 自动编译 Debug 和 Release 版本
- ✅ 运行单元测试
- ✅ 上传 APK 到 Artifacts
- ✅ 发布到 GitHub Releases

### 手动触发编译

```
GitHub -> Actions -> Build APK -> Run workflow
```

---

## 📝 版本发布流程

1. **更新版本号**
   ```kotlin
   // app/build.gradle.kts
   versionCode = 2
   versionName = "1.1.0"
   ```

2. **创建 Git 标签**
   ```bash
   git tag -a v1.1.0 -m "Release v1.1.0"
   git push origin v1.1.0
   ```

3. **自动编译和发布**
   - GitHub Actions 会自动编译
   - APK 会上传到 GitHub Releases

---

## ✅ 检查清单

编译前请确认：

- [ ] JDK 17 已安装
- [ ] Android SDK 已配置
- [ ] Gradle 版本正确
- [ ] 所有依赖已下载
- [ ] xposed_init 文件存在
- [ ] AndroidManifest.xml 配置正确
- [ ] 签名密钥已创建（Release）

部署前请确认：

- [ ] APK 编译成功
- [ ] LSPosed 已安装
- [ ] 支付宝已安装
- [ ] 作用域配置正确
- [ ] 日志可以正常输出

---

## 🎯 下一步

1. **功能增强**
   - 实现真正的 RPC Hook 逻辑
   - 添加更多森林功能
   - 完善蚂蚁庄园功能

2. **UI 优化**
   - 添加配置界面
   - 实现实时日志查看
   - 添加统计数据展示

3. **稳定性提升**
   - 添加更多错误处理
   - 优化性能
   - 增加单元测试

---

## 📞 技术支持

如果遇到问题：

1. **查看文档**
   - `README.md` - 项目说明
   - `MIGRATION_COMPLETE.md` - 迁移报告
   - `ARCHITECTURE_COMPLETE.md` - 架构文档

2. **检查日志**
   ```bash
   adb logcat | grep "Sesame"
   ```

3. **提交 Issue**
   - GitHub Issues: 报告 Bug
   - Discussions: 提出建议

---

## 🎉 恭喜！

如果您能看到这里，说明项目已经准备就绪！

现在您可以：
- ✅ 编译项目生成 APK
- ✅ 安装并激活模块
- ✅ 开始使用蚂蚁森林自动化功能

**祝您使用愉快！** 🎊
