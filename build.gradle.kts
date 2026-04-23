plugins {
    id("java")
    // Pinned to 2.11.0 (matches JavaFX Tools reference). The plugin will log a
    // "outdated" message recommending 2.14.0, but 2.14.0 requires Gradle 9.0+
    // which is a larger upgrade we defer to L1.
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

val platformType: String by project
val platformVersion: String by project
val pluginGroup: String by project
val pluginVersion: String by project
val javaVersion: String by project

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(platformType, platformVersion)

        bundledPlugins(
            "com.intellij.java"
        )
    }

    // SQLite + connection pool.
    //
    // dbcp2 pinned to 2.9.0 (not the latest 2.12.0) because starting with 2.10.0
    // the transitive commons-logging jumped from 1.2 to 1.3.x, whose new
    // Slf4jLogFactory auto-bridges to SLF4J. Inside the IntelliJ runtime that
    // triggers a LinkageError: the platform and plugin classloaders see two
    // different org.slf4j.ILoggerFactory Class objects when BasicDataSource's
    // <clinit> asks for a Log. 2.9.0 still uses commons-logging 1.2 which logs
    // via java.util.logging — no SLF4J bridge, no conflict. API (setMaxTotal,
    // setDriverClassName, etc.) is unchanged from our usage in
    // DatabaseBasicService.
    implementation("org.apache.commons:commons-dbcp2:2.9.0")
    implementation("commons-dbutils:commons-dbutils:1.8.1")
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")

    // Swing drag-and-drop handlers use javax.activation.{DataHandler, ActivationDataFlavor}
    // (4 TransferHandler classes). Jakarta migration deferred to L1.
    implementation("javax.activation:javax.activation-api:1.2.0")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
    }

    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set("233")
    }

    buildSearchableOptions {
        enabled = false
    }

    runIde {
        // macOS Metal renderer occasionally freezes the UI thread (jstack/kill -3 hang).
        jvmArgs("-Dsun.java2d.metal=false")
        // Preserve old jdk-internal export needed for some Swing internals.
        jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
    }
}
