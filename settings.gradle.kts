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
