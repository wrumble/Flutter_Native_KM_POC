//
//  FlutterSceneViewModel.swift
//  Runner
//
//  Created by Wayne Rumble on 05/10/2020.
//

import Foundation
import KMShared
import RxSwift

protocol FlutterSceneViewModelType {
    var colorBehaviourSubject: BehaviorSubject<String> { get }
}

class FlutterSceneViewModel: FlutterSceneViewModelType {
    let colorBehaviourSubject = BehaviorSubject(value: "#FFFFFF")
    private let database: Database

    required init(database: Database) {
        self.database = database

        listenToBackgroundColorFlow()
    }

    private func listenToBackgroundColorFlow() {
        database.backgroundColorFlow.watch { color in
            guard let colorHex = color?.hex else {
                return
            }
            self.colorBehaviourSubject.onNext(colorHex)
        }
    }
}
