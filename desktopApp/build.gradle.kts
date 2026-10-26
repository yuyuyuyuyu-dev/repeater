import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

detekt {
    buildUponDefaultConfig = true
}

ktlint {
    version =
        libs.versions.ktlint.cli
            .get()
    filter {
        val buildPath =
            layout.buildDirectory
                .get()
                .asFile.path + File.separator
        exclude { it.file.path.startsWith(buildPath) }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "dev.yuyuyuyuyu.repeater.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.yuyuyuyuyu.repeater"
            packageVersion = "1.0.0"
        }
    }
}
