import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.rumblewayne.BIMMultiPlatform.R
import com.rumblewayne.bimmultiplatform.db.cache.Database
import com.rumblewayne.bimtestandroid.DragTransformableNode
import io.flutter.plugin.platform.PlatformView

class FlutterSceneView(context: Context, private val database: Database): PlatformView {
    private val context = context
    private val sceneView = SceneView(context)

    init {
        setBIMScene()
        listenToBackgroundColorFlow()
    }

    private fun setBIMScene() {
        ModelRenderable.builder()
                .setSource(context, R.raw.bimtest)
                .build()
                .thenAccept {
                    addNodeToScene(it)
                }
                .exceptionally {
                    val toast = Toast.makeText(context, "Unable to load BIM renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
    }

    private fun addNodeToScene(model: ModelRenderable) {
        val transformationSystem = makeTransformationSystem()
        val dragTransformableNode = DragTransformableNode(3f, transformationSystem)
        dragTransformableNode.renderable = model
        sceneView.scene.addChild(dragTransformableNode)

        dragTransformableNode.select()
        sceneView.scene
                .addOnPeekTouchListener { hitTestResult: HitTestResult?, motionEvent: MotionEvent? ->
                    transformationSystem.onTouch(
                            hitTestResult,
                            motionEvent
                    )
                }
    }

    private fun makeTransformationSystem(): TransformationSystem {
        val footprintSelectionVisualizer = FootprintSelectionVisualizer()
        return TransformationSystem(context.resources.displayMetrics, footprintSelectionVisualizer)
    }

    private fun listenToBackgroundColorFlow() {
        database.backgroundColorFlow.watch { setBackgroundColor(it.hex) }
    }

    override fun getView(): View {
        sceneView.resume()
        return sceneView
    }

    private fun setBackgroundColor(color: String) {
        val newColor = Color.parseColor(color)
        sceneView.setBackgroundColor(newColor)
    }

    override fun dispose() {
        sceneView.destroy()
    }
}