import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            val configFile = layout.buildDirectory.file("detekt/detekt.yml").get().asFile
            configFile.parentFile.mkdirs()
            DetektConventionPlugin::class.java.classLoader
                .getResourceAsStream("detekt/detekt.yml")
                ?.use { configFile.outputStream().use(it::copyTo) }
                ?: error("detekt.yml not found in convention plugin resources")

            extensions.configure(DetektExtension::class.java) {
                buildUponDefaultConfig = true
                parallel = true
                config.setFrom(files(configFile))
            }
        }
    }
}
