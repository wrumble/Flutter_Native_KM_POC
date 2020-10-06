package com.rumblewayne.BIMMultiPlatform

import FlutterSceneView
import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class FlutterSceneViewFactory(private val messenger: BinaryMessenger, private val viewModel: FlutterSceneViewModel): PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, id: Int, o: Any?): PlatformView {
        return FlutterSceneView(context, viewModel)
    }
}