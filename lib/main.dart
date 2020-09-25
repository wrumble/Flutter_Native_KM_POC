import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'dart:io' show Platform;

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('bimmultiplatform/colors');
  Color currentSelectionColor = Color(0xFFFFFF);
  List<String> colorList = ["Red", "Green", "Blue"];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: getPlatformSceneView(),
      bottomNavigationBar: Row(children: <Widget>[
        colorButton(colorList[0]),
        colorButton(colorList[1]),
        colorButton(colorList[2])
      ]),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  Widget colorButton(String color) {
    return Expanded(
        child: RaisedButton(
        child: Text(color),
        onPressed: () => setSelectionColor(color),
        )
    );
  }

  Widget getPlatformSceneView() {
    final String viewType = "SceneView";
    if (Platform.isAndroid) {
      return AndroidView(viewType: viewType);
    } else if (Platform.isIOS) {
      return UiKitView(viewType: viewType);
    } else {
      return Text("Platform not supported");
    }
  }

  Future<void> setSelectionColor(String color) async {
    Color selectionColor;
    String methodName = "setSelectionColor";
    try {
      final String result = await platform.invokeMethod(methodName, {"color": color});
      print("Color set to $result");
    } on PlatformException catch (error) {
      print("Error setting color through $methodName MethodChannel: $error ");
      selectionColor = currentSelectionColor;
    }

    setState(() {
      currentSelectionColor = selectionColor;
    });
  }
}