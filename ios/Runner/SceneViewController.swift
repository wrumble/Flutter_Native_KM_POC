//
//  SceneViewController.swift
//  Runner
//
//  Created by Wayne Rumble on 23/09/2020.
//

import Foundation
import UIKit
import SceneKit
import Flutter
import KMShared

class SceneViewFactory: NSObject, FlutterPlatformViewFactory {
    public var sceneView: SceneView?
    private let database: Database

    init(database: Database) {
        self.database = database
    }

    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        sceneView = SceneView(frame, viewId: viewId, args: args, database: database)
        return sceneView!
    }
}

public class SceneView: NSObject, FlutterPlatformView {
    private var viewId: Int64!
    private var database: Database!
    private var sceneView = SCNView()

    init(_ frame: CGRect, viewId: Int64, args: Any?, database: Database) {
        super.init()

        self.viewId = viewId
        self.database = database
        self.sceneView.frame = frame

        setupScene()
        listenToBackgroundColorFlow()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func view() -> UIView {
        return sceneView
    }

    private func setBackground(to color: BGColor?) {
        guard let color = color else {
            return
        }
        sceneView.backgroundColor = UIColor(hexString: color.hex)
    }

    private func setupScene() {
        guard let scene = SCNScene(named: "BIMAssets.scnassets/BIMTest.dae") else {
            return
        }

        let cameraNode = SCNNode()
        cameraNode.camera = SCNCamera()
        cameraNode.position = SCNVector3(x: 20, y: 0, z: 75)

        let lightNode = SCNNode()
        lightNode.light = SCNLight()
        lightNode.light?.type = .omni
        lightNode.position = SCNVector3(x: 0, y: 0, z: 75)

        let ambientLightNode = SCNNode()
        ambientLightNode.light = SCNLight()
        ambientLightNode.light?.type = .ambient
        ambientLightNode.light?.color = UIColor.lightGray

        scene.rootNode.addChildNode(cameraNode)
        scene.rootNode.addChildNode(lightNode)
        scene.rootNode.addChildNode(ambientLightNode)

        sceneView.allowsCameraControl = true
        sceneView.backgroundColor = .darkGray

        if #available(iOS 11.0, *) {
            sceneView.cameraControlConfiguration.allowsTranslation = false
        }
        sceneView.scene = scene
    }

    private func listenToBackgroundColorFlow() {
        database.backgroundColorFlow.watch(block: setBackground)
    }
}
