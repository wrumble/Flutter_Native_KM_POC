//
//  SceneViewFactory.swift
//  Runner
//
//  Created by Wayne Rumble on 02/10/2020.
//

import Foundation
import Flutter
import KMShared

class FlutterSceneViewFactory: NSObject, FlutterPlatformViewFactory {
    private let viewModel: FlutterSceneViewModelType

    init(viewModel: FlutterSceneViewModelType) {
        self.viewModel = viewModel
    }

    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return FlutterSceneView(frame, viewId: viewId, args: args, viewModel: viewModel)
    }
}
