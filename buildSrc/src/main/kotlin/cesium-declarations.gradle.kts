import com.github.turansky.cesium.generateKotlinDeclarations

tasks {
    named<Delete>("clean") {
        delete("src")
    }

    val generateDeclarations by registering {
        dependsOn(":kotlinNpmInstall")

        doLast {
            val sourceDir = file("src/main/kotlin")
                .also { delete(it) }

            val definitionsFile = rootProject.buildDir
                .resolve("js/node_modules/cesium")
                .resolve("/Source/Cesium.d.ts")

            generateKotlinDeclarations(
                definitionsFile = definitionsFile,
                sourceDir = sourceDir
            )
        }
    }

    named("compileKotlinJs") {
        dependsOn(generateDeclarations)
    }
}
