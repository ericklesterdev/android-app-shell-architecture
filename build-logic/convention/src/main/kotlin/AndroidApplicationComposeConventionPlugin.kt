import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("nbahub.android.application")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure(BaseAppModuleExtension::class.java) {
                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
