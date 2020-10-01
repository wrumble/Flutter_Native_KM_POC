import UIKit
import Flutter
import KMShared

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    private let database = Database(databaseDriverFactory: DatabaseDriverFactory())

    private var sceneViewFactory: SceneViewFactory?

    override func application(_ application: UIApplication,
                              didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        sceneViewFactory = SceneViewFactory(fetchBackgroundColor: fetchBackgroundColor)
        setupColorListener()
        registerSceneView()
        registerSetColorChannel()

        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }

    private func registerSceneView() {
        guard let sceneViewFactory = sceneViewFactory else {
            return
        }
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
        database.colorListener = setBackgroundColor
    }

    private func saveColor(from arguments: Any?) {
        guard let arguments = arguments as? [String: Any] else {
            return
        }
        if let color = arguments["color"] as? String {
            database.cacheBackgroundColor(color: color)
        }
    }

    private func setBackgroundColor(color: String) {
        guard let sceneViewFactory = sceneViewFactory else {
            return
        }
        sceneViewFactory.sceneView.backgroundColor = color
    }

    private func fetchBackgroundColor() -> String? {
        if let savedColor = database.fetchColor() {
            return savedColor.hex
        } else {
            return nil
        }
    }
}

