package com.example.BIMMultiPlatform

import io.flutter.embedding.engine.FlutterEngine

import android.view.View
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant

import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import com.example.bimtestandroid.DragTransformableNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Scene
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import io.flutter.Log

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        SceneViewPlugin.registerWith(flutterEngine)
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
        Log.d("FlutterSceneView", "WTF IN innit")
        setBIMScene()
    }

    private fun setBIMScene() {
        Log.d("FlutterSceneView", "WTF IN setBIMScene")
        Log.d("FlutterSceneView context", "$context")
        val bimtest = R.raw.bimtest
        Log.d("FlutterSceneView bimtest", "$bimtest")

        ModelRenderable.builder()
                .setSource(context, R.raw.bimtest)

                .build()
                .thenAccept {
                    Log.d("FlutterSceneView", "WTF IN thenAccept")
                    addNodeToScene(it)
                }
                .exceptionally {
                    Log.d("FlutterSceneView", "WTF IN exceptionally")
                    val toast = Toast.makeText(context, "Unable to load BIM renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
    }

    private fun addNodeToScene(model: ModelRenderable) {
        Log.d("FlutterSceneView", "WTF IN addNodeToScene")
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
        Log.d("FlutterSceneView", "WTF IN makeTransformationSystem")
        val footprintSelectionVisualizer = FootprintSelectionVisualizer()
        return TransformationSystem(context.resources.displayMetrics, footprintSelectionVisualizer)
    }

    override fun getView(): View {
        print("WTF IN getView")
        sceneView.resume()
        return sceneView
    }

    override fun dispose() {
        print("WTF IN dispose")
        sceneView.pause()
        sceneView.destroy()
    }
}