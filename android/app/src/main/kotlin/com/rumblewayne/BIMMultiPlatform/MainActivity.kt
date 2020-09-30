package com.rumblewayne.BIMMultiPlatform

import ColorFactory
import SceneViewFactory
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

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