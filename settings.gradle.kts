pluginManagement {
    repositories {
//        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()

        // SNAPSHOT 仓库（用于依赖其他项目的 SNAPSHOT 版本）
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            content {
                includeVersionByRegex(".*", ".*", ".*-SNAPSHOT")
            }
        }
    }
//    versionCatalogs {
//        create("libs") {
//            from("com.xeonyu:version-catalog:0.1.1")
//        }
//    }
}

rootProject.name = "application"
include(":app")
include(":application")
