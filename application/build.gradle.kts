plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

// Maven Central 发布配置 - Vanniktech 插件会自动读取 gradle.properties 中的配置
mavenPublishing {
    // 发布到 Maven Central（自动检测 SNAPSHOT 和正式版本）
    publishToMavenCentral()

    // 只有非 SNAPSHOT 版本才需要签名
    // SNAPSHOT 版本发布到 snapshots 仓库，不需要 GPG 签名
    val versionName = project.findProperty("VERSION_NAME")?.toString() ?: ""
    val isSnapshot = versionName.endsWith("SNAPSHOT", ignoreCase = true)

    if (!isSnapshot) {
        signAllPublications()
    }
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