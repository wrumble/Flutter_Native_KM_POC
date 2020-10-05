import 'package:flutter/material.dart';

import 'BIMHomePage.dart';
import 'BIMHomePageViewModel.dart';

class BIMMultiPlatform extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: BIMHomePage(viewModel: BIMHomePageViewModel()),
    );
  }
}