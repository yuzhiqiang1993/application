plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
}

android {
    namespace = "com.yzq.application"
}

dependencies {
    implementation(platform(libs.kotlin.bom.stable))
    implementation(libs.androidx.appcompat.stable)
}