import org.gradle.api.tasks.scala.ScalaCompile
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.InstrumentCodeTask

plugins {
    scala
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.wenjunhuang.codeepiphany"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

sourceSets {
    main {
        // Java and Scala reference each other and must be compiled together by Zinc.
        java.setSrcDirs(emptyList<String>())
        scala.setSrcDirs(listOf("src/main/scala", "src/main/java", "src/generated/java"))
        resources.srcDir(layout.buildDirectory.dir("webviewResources"))
    }
}

configurations.configureEach {
    exclude(group = "org.slf4j")
    exclude(group = "org.typelevel", module = "log4cats-slf4j_3")
    exclude(group = "org.jetbrains.kotlin")
}

val jooqCodegen = configurations.create("jooqCodegen")

dependencies {
    intellijPlatform {
        val localPath = providers.gradleProperty("localPlatformPath")
        if (localPath.isPresent) local(localPath.get())
        else intellijIdea(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.intellij.java")
        bundledModule("com.intellij.modules.jcef")
        testFramework(TestFrameworkType.Platform)
    }
    implementation("org.scala-lang:scala3-library_3:3.7.4")
    implementation("org.typelevel:cats-effect_3:3.6.3")
    implementation("org.typelevel:cats-core_3:2.13.0")
    implementation("org.typelevel:cats-mtl_3:1.6.0")
    implementation("io.circe:circe-core_3:0.14.15")
    implementation("io.circe:circe-generic_3:0.14.15")
    implementation("io.circe:circe-parser_3:0.14.15")
    implementation("io.circe:circe-optics_3:0.15.1")
    implementation("co.fs2:fs2-core_3:3.12.2")
    implementation("dev.optics:monocle-core_3:3.3.0")
    implementation("dev.optics:monocle-macro_3:3.3.0")
    implementation("org.typelevel:log4cats-core_3:2.7.1")
    implementation("org.typelevel:case-insensitive_3:1.5.0")
    implementation("org.http4s:http4s-client_3:0.23.33")
    implementation("org.http4s:http4s-dsl_3:0.23.33")
    implementation("org.http4s:http4s-circe_3:0.23.33")
    implementation("com.squareup.okhttp3:okhttp-jvm:5.3.2")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.vladsch.flexmark:flexmark:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-ext-attributes:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-data:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-ast:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-misc:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-builder:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-sequence:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-collection:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-dependency:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-format:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-html:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-options:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util-visitor:0.64.8")
    implementation("com.softwaremill.common:id-generator_3:1.4.0")
    implementation("io.monix:newtypes-core_3:0.3.0")
    implementation("io.monix:newtypes-circe-v0-14_3:0.3.0")
    implementation("com.github.cb372:cats-retry_3:4.0.0")
    implementation("com.github.weisj:jsvg:2.0.0")
    implementation("org.apache.commons:commons-text:1.15.0")
    implementation("org.jooq:jooq:3.19.18")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("org.xerial:sqlite-jdbc:3.51.1.0")
    implementation("org.flywaydb:flyway-core:11.9.1")
    implementation("com.zaxxer:HikariCP:7.0.2")
    testImplementation("org.scalatest:scalatest_3:3.2.19")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
    testImplementation("org.typelevel:cats-effect-testing-scalatest_3:1.7.0")
    // Gradle runs ScalaTest suites through JUnit, alongside IntelliJ fixture tests.
    testImplementation("org.scalatestplus:junit-4-13_3:3.2.19.0")
    add(jooqCodegen.name, "org.jooq:jooq-codegen:3.19.18")
    add(jooqCodegen.name, "org.xerial:sqlite-jdbc:3.51.1.0")
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()
        description = "<p>Personal fork of LeetCode Epiphany for LeetCode and LeetCodeCN.</p>"
        changeNotes = "<p>LeetCode-only fork with JCEF compatibility and a Gradle build.</p>"
        ideaVersion { sinceBuild = providers.gradleProperty("platformSinceBuild") }
    }
    pluginVerification { ides { current() } }
}

val npmInstall = tasks.register<Exec>("npmInstall") {
    workingDir("webview")
    commandLine("npm", "ci")
    inputs.files("webview/package.json", "webview/package-lock.json")
    outputs.dir("webview/node_modules")
}

val buildWebview = tasks.register<Exec>("buildWebview") {
    dependsOn(npmInstall)
    workingDir("webview")
    commandLine("npm", "run", "build")
    inputs.files(fileTree("webview") { exclude("node_modules/**") })
    outputs.dir(layout.buildDirectory.dir("webviewResources"))
}

val checkWebview = tasks.register<Exec>("checkWebview") {
    dependsOn(npmInstall)
    workingDir("webview")
    commandLine("npm", "run", "typecheck")
    inputs.files(fileTree("webview") { exclude("node_modules/**") })
}

tasks.check { dependsOn(checkWebview) }
tasks.processResources {
    dependsOn(buildWebview)
    includeEmptyDirs = false
    from("LICENSE") { into("META-INF") }
}

tasks.withType<ScalaCompile>().configureEach {
    scalaCompileOptions.additionalParameters = listOf(
        "-Wunused:imports", "-language:implicitConversions", "-Xkind-projector:underscores",
        "-source:3.7", "-feature", "-Xmax-inlines:100", "-explain-cyclic"
    )
    scalaCompileOptions.forkOptions.memoryMaximumSize = "2g"
}

tasks.named<InstrumentCodeTask>("instrumentCode") {
    formsDirs.from(fileTree("src/main/java") { include("**/*.form") })
    sourceDirs.from("src/main/java", "src/main/scala")
}

tasks.test {
    useJUnit()
    systemProperty("java.awt.headless", "true")
    maxHeapSize = "2g"
}

val prepareCodegenDatabase = tasks.register<Exec>("prepareCodegenDatabase") {
    inputs.file("gradle/schema.sql")
    outputs.file(layout.buildDirectory.file("codegen/schema.sqlite"))
    doFirst { layout.buildDirectory.dir("codegen").get().asFile.mkdirs() }
    commandLine("python3", "-c", """
        import sqlite3
        from pathlib import Path
        path = Path('build/codegen/schema.sqlite')
        path.unlink(missing_ok=True)
        with sqlite3.connect(path) as db:
            db.executescript(Path('gradle/schema.sql').read_text())
    """.trimIndent())
}

tasks.register<JavaExec>("generateJooq") {
    group = "code generation"
    description = "Regenerate the retained database models from the design schema."
    dependsOn(prepareCodegenDatabase)
    classpath = jooqCodegen
    mainClass = "org.jooq.codegen.GenerationTool"
    args("gradle/jooq.xml")
}
