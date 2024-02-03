plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
}

android {
    namespace = "com.yzq.application"
}

dependencies {
    implementation(libs.androidx.appcompat)
}