import UIKit
import Flutter
import KMShared

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    private var colorFactory = ColorFactory()
    private var sceneViewFactory = SceneViewFactory()

    override func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

      setupColorListener()
      registerSceneView()
      registerSetColorChannel()

      return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }

    private func registerSceneView() {
        registrar(forPlugin: "BIMMultiPlatform")?.register(sceneViewFactory, withId: "SceneView")
        GeneratedPluginRegistrant.register(with: self)
    }

    private func registerSetColorChannel() {
        guard let controller = window?.rootViewController as? FlutterViewController else {
            return
        }
        let colorsChannel = FlutterMethodChannel(name: "bimmultiplatform/colors",
                                                  binaryMessenger: controller.binaryMessenger)
        colorsChannel.setMethodCallHandler {(call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            if call.method == "setSelectionColor"{
                self.saveColor(from: call.arguments)
            }
        }
    }

    private func setupColorListener() {
        colorFactory.colorListener = setBackgroundColor
    }

    private func saveColor(from arguments: Any?) {
        guard let arguments = arguments as? [String: Any] else {
            return
        }
        if let color = arguments["color"] as? String {
            colorFactory.saveColor(newColor: color)
        }
    }

    private func setBackgroundColor(color: String) {
        sceneViewFactory.sceneView.backgroundColor = color
    }
}

