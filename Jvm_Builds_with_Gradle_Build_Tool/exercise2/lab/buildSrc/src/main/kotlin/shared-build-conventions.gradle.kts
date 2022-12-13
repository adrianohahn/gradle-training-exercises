import java.nio.file.Path
import java.nio.file.Paths

plugins {
    java
    id("org.barfuin.gradle.taskinfo")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

val extraSrc = sourceSets.create("extra")

dependencies {
    implementation("joda-time:joda-time:2.11.1")
    implementation("com.google.guava:guava:31.1-jre")
    "extraImplementation"("joda-time:joda-time:2.11.1")
}

tasks.register("sourceSetsInfo") {
    doLast {
        val projectPath = layout.projectDirectory.asFile.toPath()
        val gradleHomePath: Path = gradle.gradleUserHomeDir.toPath()
        val cachePath: Path = Paths.get(gradleHomePath.toString(), "caches/modules-2/files-2.1/")


        sourceSets.forEach {
            val sourceSet = it
            println()
            println("[" + sourceSet.name + "]")

            println("   srcDirs:")
            sourceSet.allSource.srcDirs.forEach {
                println("      " + projectPath.relativize(it.toPath()))
            }

            println("   outputs:")
            sourceSet.output.classesDirs.files.forEach {
                println("      " + projectPath.relativize(it.toPath()))
            }
            println("   impl dependency configuration:")
            println("      " + sourceSet.implementationConfigurationName)

            println("   compile task:")
            println("      " + sourceSet.compileJavaTaskName)

            println("   compile classpath:")
            sourceSet.compileClasspath.files.forEach {
                if (it.toString().contains(".gradle/")) {
                    println("      " + cachePath.relativize(it.toPath()))
                } else {
                    println("      " + projectPath.relativize(it.toPath()))
                }
            }
        }
    }
}

tasks.named<Jar>("jar") {
    if (project.name.equals("kotlin")) {
        manifest.attributes["Main-Class"] = "com.gradle.lab.AppKt"
    } else {
        manifest.attributes["Main-Class"] = "com.gradle.lab.App"
    }
}

tasks.register<Copy>("generateMlCode") {
    from(layout.projectDirectory.dir("../mlCodeGenTemplate"))
    into(layout.buildDirectory.dir("generated/sources/mlCode"))
}

tasks.register<JavaExec>("runExtra") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
    classpath = extraSrc.output + extraSrc.runtimeClasspath
    mainClass.set("Extra")
}

tasks.named<JavaCompile>("compileExtraJava") {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

sourceSets {
    main {
        java {
            srcDir(tasks.named("generateMlCode"))
        }
    }
}
