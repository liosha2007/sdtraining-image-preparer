import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.4.1"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "com.x256n.sdtrainingimagepreparer.desktop"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repo.repsy.io/mvn/chrynan/public")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(kotlin("stdlib"))
//    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    implementation("com.chrynan.navigation:navigation-core:0.4.0")
    implementation("com.chrynan.navigation:navigation-compose:0.4.0")

    implementation("io.insert-koin:koin-core:3.3.3")
//    implementation("io.insert-koin:koin-core-jvm:3.3.3")

    implementation("com.google.code.findbugs:jsr305:3.0.2")

    // log4j2
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
//        javaParameters = true
        // -opt-in=kotlin.RequiresOptIn
        // -Xopt-in=kotlin.RequiresOptIn
//        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

compose.desktop {
    application {
        mainClass = "com.x256n.sdtrainingimagepreparer.desktop.MainKt"
        jvmArgs += listOf(
            "-XX:ErrorFile=hs_err.log.txt",
//            "-XX:-HeapDumpOnOutOfMemoryError",
//            "-XX:HeapDumpPath=.log/dump.hprof",
        )
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "sdtraining-image-preparer"
            packageVersion = project.version.toString()
            windows {
                dirChooser = true
                menuGroup = packageName
                shortcut = true
                menu = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}
