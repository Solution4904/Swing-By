pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            // Kakao Map
            url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")

            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
    }
}

rootProject.name = "Swing_By"
include(":app")
