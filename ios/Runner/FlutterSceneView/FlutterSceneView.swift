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
import RxSwift

public class FlutterSceneView: NSObject, FlutterPlatformView {
    private var viewModel: FlutterSceneViewModelType
    private var disposeBag = DisposeBag()
    private var sceneView = SCNView()

    init(_ frame: CGRect, viewId: Int64, args: Any?, viewModel: FlutterSceneViewModelType) {
        self.viewModel = viewModel

        super.init()

        self.sceneView.frame = frame

        setupScene()
        listenToBackgroundColor()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
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

    public func view() -> UIView {
        return sceneView
    }


    private func listenToBackgroundColor() {
        viewModel.colorBehaviourSubject
            .asObservable()
            .subscribe { colorHex in
                self.sceneView.backgroundColor = UIColor(hexString: colorHex)
            }.disposed(by: disposeBag)
    }
}
