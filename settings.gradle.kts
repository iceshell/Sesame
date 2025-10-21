// 芝麻开花节节高
rootProject.name = "Sesame"

include(":app")

// settings.gradle.kts
pluginManagement {
    repositories {
        maven(uri("https://maven.aliyun.com/repository/gradle-plugin")) // 插件优先
        maven(uri("https://maven.aliyun.com/repository/public"))        // 公共仓库
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(uri("https://maven.aliyun.com/repository/public"))        // 优先
        mavenCentral()
        google()                                                        // Android 依赖必需
    }
}