plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

// Maven Central 发布配置 - Vanniktech 插件会自动读取 gradle.properties 中的配置
mavenPublishing {
    // 发布到 Maven Central（自动检测 SNAPSHOT 和正式版本）
    publishToMavenCentral()

    // 显式启用签名
    signAllPublications()
}

android {
    namespace = "com.yzq.application"

    buildFeatures {
        // 根据项目需要添加构建特性
        // viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom.stable))
    implementation(libs.androidx.appcompat.stable)
    implementation(libs.androidx.core.ktx.stable)
}