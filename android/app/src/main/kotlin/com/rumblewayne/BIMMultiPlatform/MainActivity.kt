package com.rumblewayne.BIMMultiPlatform

import android.util.Log
import androidx.annotation.NonNull
import com.rumblewayne.bimmultiplatform.db.cache.Database
import com.rumblewayne.bimmultiplatform.db.cache.DatabaseDriverFactory
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {
    private val databaseDriverFactory = DatabaseDriverFactory(context)
    private val database = Database(databaseDriverFactory)
    private lateinit var sceneViewFactory: FlutterSceneViewFactory

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        registerSceneView(flutterEngine)
        registerSetColorChannel(flutterEngine)
    }

    private fun registerSceneView(flutterEngine: FlutterEngine) {
        sceneViewFactory = FlutterSceneViewFactory(flutterEngine.dartExecutor.binaryMessenger, database)

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

    private fun saveColor(arguments: Any?) {
        if (arguments is Map<*, *>) {
            val color = arguments["color"]
            if (color is String) {
                database.cacheBackgroundColor(color)
            } else {
                Log.d("BIMMultiPlatform Android", "Color passed is not a string: $color")
            }
        }
    }
}