import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

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

    // Markdown export renders a user-customizable Groovy template through
    // groovy.text.SimpleTemplateEngine, which lives in Groovy's groovy-templates
    // module. Through 2026.1 the IDE bundled a full Groovy in the platform lib/
    // (a groovy-all-style groovy.jar), so the engine resolved implicitly with no
    // declared dependency. 2026.2 slimmed the bundled Groovy down to the core
    // (groovy.lang stays, the whole groovy.text package is gone), which makes
    // ExportUtil.processToMarkdownString fail with NoClassDefFoundError.
    //
    // Bundle our own complete Groovy instead of depending on the IDE's Groovy
    // plugin: the Groovy plugin only ships compiler/runtime helper jars and has
    // never carried the groovy.text classes, and a <depends> on it would also
    // stop the whole plugin from loading whenever the user disables it. Pulling
    // groovy core + groovy-templates from the same release keeps groovy.lang and
    // groovy.text version-consistent within the plugin classloader.
    implementation("org.apache.groovy:groovy:4.0.24")
    implementation("org.apache.groovy:groovy-templates:4.0.24")

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
    // sqlite-jdbc pinned to 3.39.2.0 — last release that uses java.util.logging.
    // 3.39.3.0 (Sept 2022) switched to SLF4J, and at runtime inside IntelliJ the
    // plugin classloader sees a different org.slf4j.ILoggerFactory class than
    // the one StaticLoggerBinder was loaded against → LinkageError during
    // org.sqlite.JDBC.<clinit>. Same mechanism as the dbcp2/commons-logging
    // downgrade above. 3.39.2.0 still has Apple Silicon native binaries and
    // modern sqlite engine (3.39.2), just without SLF4J.
    implementation("org.xerial:sqlite-jdbc:3.39.2.0")

    // Swing drag-and-drop handlers use javax.activation.{DataHandler, ActivationDataFlavor}
    // (4 TransferHandler classes). Jakarta migration deferred to L1.
    implementation("javax.activation:javax.activation-api:1.2.0")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

intellijPlatform {
    // Re-run the Marketplace compatibility check locally with `./gradlew verifyPlugin`.
    // Targets the 2026.2 line (the report ran against IU-262.8377.35, an EAP that has
    // since rolled forward to 262.8665.81 — EAP snapshots are not retained long-term).
    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaUltimate, "262.8665.81")
        }
    }
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

    // Bake the Gradle pluginVersion into a resource so DatabaseBackupService can
    // read the plugin's own version at runtime WITHOUT the platform plugin-registry
    // APIs (PluginManager.findEnabledPlugin / getPluginByClass etc. all became
    // @ApiStatus.Internal in the 2026.2 line). Scoped to the single file so other
    // resources (plugin.xml, message bundles) are never template-expanded.
    processResources {
        filesMatching("notebook-build.properties") {
            expand(mapOf("version" to pluginVersion))
        }
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
