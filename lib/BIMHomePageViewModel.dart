import 'package:flutter/services.dart';

abstract class BIMHomePageViewModelType {
  MethodChannel get platform;
  List<String> get colorList;

  Future<void> setSelectionColor(String color);
}

class BIMHomePageViewModel implements BIMHomePageViewModelType {
  final MethodChannel platform = MethodChannel('bimmultiplatform/colors');
  final List<String> colorList = ["Red", "Green", "Blue"];


  Future<void> setSelectionColor(String color) async {
    String methodName = "setSelectionColor";
    try {
      await platform.invokeMethod(methodName, {"color": color});
    } on PlatformException catch (error) {
      print("Error setting color through $methodName MethodChannel: $error ");
    }
  }
}