import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.lang.Long.parseLong
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.rumblewayne.BIMMultiPlatform.R
import com.rumblewayne.bimtestandroid.DragTransformableNode
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class SceneViewFactory(private val messenger: BinaryMessenger): PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    lateinit var sceneView: FlutterSceneView

    override fun create(context: Context, id: Int, o: Any?): PlatformView {
        sceneView = FlutterSceneView(context)
        return sceneView
    }
}

class FlutterSceneView(context: Context): PlatformView {
    var backGroundColor: String = "000000"
        set(value) {
            setBackgroundColor(value)
        }
    private val context = context
    private val sceneView = SceneView(context)

    init {
        setBIMScene()
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