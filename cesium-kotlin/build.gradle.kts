plugins {
    id("com.github.turansky.kfc.library")
    `cesium-declarations`
}

dependencies {
    implementation(npm("cesium", property("cesium.version") as String))
}
