plugins {
    id("com.github.turansky.kfc.library")
    `cesium-declarations`
}

dependencies {
    implementation(npmv("cesium"))
}
