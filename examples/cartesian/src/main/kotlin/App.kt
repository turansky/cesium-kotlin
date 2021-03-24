import cesium.Cartesian3
import cesium.SceneMode
import cesium.Viewer
import kotlinx.browser.document

fun main() {
    val container = document.getElementById("root")!!

    val viewer = Viewer(container) {
        sceneMode = SceneMode.SCENE3D
    }

    viewer.camera.move(
        direction = Cartesian3(x = 1.0, y = 2.0, z = 3.0),
        amount = 100.0
    )
}
