import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.3.1"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "com.x256n.sdtrainimagepreparer.desktop"
version = "0.1.0"

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

    implementation("com.darkrockstudios:mpfilepicker:1.0.0")

    implementation("io.insert-koin:koin-core:3.3.3")
//    implementation("io.insert-koin:koin-core-jvm:3.3.3")

    implementation("ch.qos.logback:logback-classic:1.4.6")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "com.x256n.sdtrainimagepreparer.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sdtrain-image-preparer"
            packageVersion = "1.0.0"

//            windows {
//                shortcut = true
//                iconFile.set(project.file("icon.ico"))
//            }
        }
    }
}
