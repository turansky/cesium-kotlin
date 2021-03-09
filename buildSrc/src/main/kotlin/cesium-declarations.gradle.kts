tasks {
    named<Delete>("clean") {
        delete("src")
    }

    val generateDeclarations by registering {
        doLast {
            // TODO: implement
        }
    }

    named("compileKotlinJs") {
        dependsOn(generateDeclarations)
    }
}
