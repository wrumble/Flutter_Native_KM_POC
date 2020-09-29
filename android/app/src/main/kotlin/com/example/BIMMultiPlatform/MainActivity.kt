package com.example.BIMMultiPlatform

import ColorFactory
import android.content.Context
import android.graphics.Color
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
import java.lang.Long.parseLong

class MainActivity: FlutterActivity() {

    private lateinit var sceneViewFactory: SceneViewFactory
    private val colorFactory = ColorFactory()

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        setupColorListener()
        registerSceneView(flutterEngine)
        registerSetColorChannel(flutterEngine)
    }

    private fun registerSceneView(flutterEngine: FlutterEngine) {
        sceneViewFactory = SceneViewFactory(flutterEngine.dartExecutor.binaryMessenger)

        GeneratedPluginRegistrant.registerWith(flutterEngine)
        flutterEngine.platformViewsController.registry.registerViewFactory("SceneView", sceneViewFactory)
    }

    private fun registerSetColorChannel(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "bimmultiplatform/colors").setMethodCallHandler { call, _ ->
            if (call.method == "setSelectionColor") {
                saveColor(call.arguments)
            }
        }
    }

    private fun setupColorListener() {
        colorFactory.colorListener = {
            setBackgroundColor(it)
        }
    }

    private fun saveColor(arguments: Any?) {
        if (arguments is Map<*, *>) {
            val color = arguments["color"]
            if (color is String) {
                colorFactory.saveColor(color)
            } else {
                Log.d("BIMMultiPlatform Android", "Color passed is not a string: $color")
            }
        }
    }

    private fun setBackgroundColor(color: String) {
        sceneViewFactory.sceneView.backGroundColor = color
    }
}

class SceneViewFactory(private val messenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    lateinit var sceneView: FlutterSceneView

    override fun create(context: Context, id: Int, o: Any?): PlatformView {
        sceneView = FlutterSceneView(context)
        return sceneView
    }
}

class FlutterSceneView(context: Context): PlatformView {
    var backGroundColor: String = "FFFFFF"
        set(value) {
            Log.d("BIMMultiPlatform Android", "Setting backGroundColor: $value")
            val newColor = Color.parseColor(value)
            Log.d("BIMMultiPlatform Android", "Setting parsedColor: $newColor")
            sceneView.setBackgroundColor(newColor)
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
        val color = parseLong(backGroundColor, 16).toInt()
        sceneView.setBackgroundColor(color)
        sceneView.resume()
        return sceneView
    }

    override fun dispose() {
        sceneView.destroy()
    }
}