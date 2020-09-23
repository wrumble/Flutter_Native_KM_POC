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

class SceneViewFactory: NSObject, FlutterPlatformViewFactory {
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return SceneView(frame, viewId: viewId, args: args)
    }
}

public class SceneView: NSObject, FlutterPlatformView {
    private var viewId: Int64!
    private var sceneView = SCNView()

    init(_ frame: CGRect, viewId: Int64, args: Any?) {
        super.init()

        self.viewId = viewId
        self.sceneView.frame = frame

        setupScene()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func view() -> UIView {
        return sceneView
    }

    private func setupScene() {
        //From .dae or .obj just change extension
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
}