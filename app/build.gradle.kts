@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.xeonyu.application)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yzq.application.demo"

    defaultConfig {
        applicationId = "com.yzq.application.demo"
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.xeonyu.logger)

//    implementation(project(":application"))
//    implementation(libs.xeonyu.application)

    implementation("com.xeonyu:application:1.0.0")
}