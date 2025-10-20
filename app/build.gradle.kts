// 芝麻开花节节高
plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "iceshell.xposed.sesame"
    compileSdk = 35

    defaultConfig {
        applicationId = "iceshell.xposed.sesame"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Kotlin标准库
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    
    // Android核心库
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    
    // Xposed API
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("de.robv.android.xposed:api:82:sources")
    
    // JSON处理
    implementation("org.json:json:20240303")
    
    // 协程支持
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    
    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
