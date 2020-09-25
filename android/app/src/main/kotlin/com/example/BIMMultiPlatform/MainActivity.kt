package com.example.BIMMultiPlatform

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.bimtestandroid.DragTransformableNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        registerSceneView(flutterEngine)
        registerSetColorChannel(flutterEngine)
    }

    private fun registerSceneView(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        SceneViewPlugin.registerWith(flutterEngine)
    }

    private fun registerSetColorChannel(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "bimmultiplatform/colors").setMethodCallHandler { call, _ ->
            if (call.method == "setSelectionColor") {
                setColor(call.arguments)
            }
        }
    }

    private fun setColor(arguments: Any?) {
        if (arguments is Map<*, *>) {
            val color = arguments["color"]
            Log.d("BIMMultiPlatform Android", "It mother fucking works we got the color: $color")
        }
    }
}

object SceneViewPlugin{
    fun registerWith(engine: FlutterEngine) {
            engine
                .platformViewsController.registry
                .registerViewFactory("SceneView", SceneViewFactory(engine.dartExecutor.binaryMessenger))
    }
}

class SceneViewFactory(private val messenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context, id: Int, o: Any?): PlatformView {
        return FlutterSceneView(context)
    }
}

class FlutterSceneView(context: Context): PlatformView {
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

    override fun dispose() {
        sceneView.destroy()
    }
}