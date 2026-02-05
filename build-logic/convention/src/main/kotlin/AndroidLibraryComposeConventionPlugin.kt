import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("nbahub.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure(LibraryExtension::class.java) {
                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
