import 'package:flutter/material.dart';
import 'dart:io' show Platform;

import 'BIMHomePageViewModel.dart';

class BIMHomePage extends StatefulWidget {
  BIMHomePage({Key key, this.viewModel}) : super(key: key);

  final BIMHomePageViewModelType viewModel;

  Widget getPlatformSceneView() {
    final String viewType = "SceneView";
    if (Platform.isAndroid) {
      return AndroidView(viewType: viewType);
    } else if (Platform.isIOS) {
      return UiKitView(viewType: viewType);
    } else {
      return Center(child: Text("Platform view not supported"));
    }
  }

  @override
  BIMHomePageState createState() => BIMHomePageState(viewModel: viewModel, platformSceneView: getPlatformSceneView());
}

class BIMHomePageState extends State<BIMHomePage> {
  BIMHomePageState({this.viewModel, this.platformSceneView}) : super();

  final BIMHomePageViewModelType viewModel;

  final Widget platformSceneView;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: platformSceneView,
        bottomNavigationBar: colorButtonRow()
    );
  }

  Widget colorButtonRow() {
    var colorButtons = viewModel.colorList.map((color) => colorButton(color)).toList();
    return Row(children: colorButtons);
  }

  Widget colorButton(String color) {
    return Expanded(
        child: RaisedButton(
          child: Text(color),
          onPressed: () => viewModel.setSelectionColor(color),
        )
    );
  }
}