import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionCatalogUpdate)
}

val ktlintVersion =
    libs.versions.ktlint.cli
        .get()

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        // Kotlin Multiplatform source sets live outside of the src/main and src/test
        // directories detekt looks at by default
        source.setFrom("src")
    }

    extensions.configure<KtlintExtension> {
        version = ktlintVersion
        filter {
            val buildPath =
                layout.buildDirectory
                    .get()
                    .asFile.path + File.separator
            exclude { it.file.path.startsWith(buildPath) }
        }
    }
}
