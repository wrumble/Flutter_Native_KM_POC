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
    private let database: Database

    init(database: Database) {
        self.database = database
    }

    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return FlutterSceneView(frame, viewId: viewId, args: args, database: database)
    }
}
