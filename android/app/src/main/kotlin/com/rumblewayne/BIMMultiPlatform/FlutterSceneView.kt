import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.rumblewayne.BIMMultiPlatform.FlutterSceneViewModel
import com.rumblewayne.BIMMultiPlatform.R
import com.rumblewayne.bimtestandroid.DragTransformableNode
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlutterSceneView(context: Context, private val viewModel: FlutterSceneViewModel): PlatformView {
    private val context = context
    private var sceneView = SceneView(context)

    override fun onFlutterViewAttached(flutterView: View) {
        setBIMScene()
        sceneView.resume()
        listenToBackgroundColor()
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
        sceneView.scene?.addChild(dragTransformableNode)

        dragTransformableNode.select()
        sceneView.scene
                ?.addOnPeekTouchListener { hitTestResult: HitTestResult?, motionEvent: MotionEvent? ->
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

    private fun listenToBackgroundColor() {
        viewModel.colorFlow.onEach {
            val newColor = Color.parseColor(it)
            sceneView.setBackgroundColor(newColor)
        }.launchIn(viewModel.viewModelScope)
    }

    override fun getView(): View {
        return sceneView
    }

    override fun dispose() {
        sceneView.destroy()
    }
}