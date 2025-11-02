import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.07"

project {

    buildType(MigrateASimpleJavaProjectFromJenkinsToTeamcity_Build)
}

object MigrateASimpleJavaProjectFromJenkinsToTeamcity_Build : BuildType({
    id("Build")
    name = "Build"

    params {
        param("env.APP_NAME", "demo-spring-boot-app")
        param("BUILD_VERSION", "1.0.0")
        password("env.DB_PASSWORD", "credentialsJSON:cadc2dff-1b45-49e4-8bb7-31d7f1fd9cd1")
        select("BUILD_ENVIRONMENT", "",
                options = listOf("development", "staging", "production"))
        checkbox("RUN_TESTS", "true",
                  checked = "true", unchecked = "false")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Initialization"
            id = "simpleRunner"
            scriptContent = """
                echo "========== Build Initialization =========="
                echo "Environment: ${'$'}{BUILD_ENVIRONMENT}"
                echo "Version: ${'$'}{BUILD_VERSION}"
                
                # Print Java version
                echo "Java Version:"
                java -version
            """.trimIndent()
        }
        gradle {
            name = "Gradle Build"
            id = "gradle_runner"
            tasks = "clean build -x test"
        }
        gradle {
            name = "Gradle Unit Tests"
            id = "gradle_runner_unit_test"

            conditions {
                equals("RUN_TESTS", "true")
            }
            tasks = "test"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
