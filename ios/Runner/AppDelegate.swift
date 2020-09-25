import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {

    registerSceneView()
    registerSetColorChannel()

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

    private func registerSceneView() {
        let viewFactory = SceneViewFactory()
        registrar(forPlugin: "BIMMultiPlatform")?.register(viewFactory, withId: "SceneView")
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
                self.setColor(from: call.arguments)
            }
        }
    }

    private func setColor(from arguments: Any?) {
        guard let arguments = arguments as? [String: Any] else {
            return
        }
        if let color = arguments["color"] as? String {
            print("It mother fucking works we got the color: \(color)")
        }
    }
}

