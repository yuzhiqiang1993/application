plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
}

android {
    namespace = "com.yzq.application"
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(libs.xeonyu.logger)
    implementation(libs.androidx.annotation)

}