import 'package:display_sdk_flutter/enumvalue.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:display_sdk_flutter/display_sdk_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _displaySdkFlutterPlugin = DisplaySdkFlutter();
  List<String> serialPortList = [];

  List<String> typelist = [
    DisplayType.PD108.name,
    DisplayType.PD220.name,
    DisplayType.PD280.name,
    DisplayType.PD350.name,
    DisplayType.PD500.name,
    DisplayType.PD700.name
  ];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    serialPortList = (await _displaySdkFlutterPlugin.serialPortFinder());
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [permissionAccess(), listDiplayType(), serialPortFinder(), displaySdkConnect()],
        ),
      ),
    );
  }

  Widget displaySdkConnect() {
    return TextButton(
        onPressed: () {
          _displaySdkFlutterPlugin.displayConnectSdk(
              serialBaudrate: 9600, serialPort: "USB");
        },
        child: Text("Display Sdk Connected"));
  }

  Widget permissionAccess() {
    return TextButton(
        onPressed: () {
          _displaySdkFlutterPlugin.permissionAccess();
        },
        child: Text("Permission"));
  }

  Widget serialPortFinder() {
    return Column(
      children: [
        Container(
          width: 300,
          child: DropdownButtonFormField(
            hint: Text("Serial Port"),
            decoration:
                InputDecoration(border: OutlineInputBorder(), isDense: true),
            items: List.generate(serialPortList.length, (index) {
              var data = serialPortList[index];
              return DropdownMenuItem(
                child: Text(data),
                value: index,
              );
            }),
            onChanged: (value) async {},
          ),
        )
      ],
    );
  }

  Widget listDiplayType() {
    return Column(
      children: [
        Container(
          width: 300,
          child: DropdownButtonFormField(
            hint: Text("Display Type"),
            decoration:
                InputDecoration(border: OutlineInputBorder(), isDense: true),
            items: List.generate(typelist.length, (index) {
              var data = typelist[index];
              return DropdownMenuItem(
                child: Text(data),
                value: index,
              );
            }),
            onChanged: (value) async {
              var data = await _displaySdkFlutterPlugin.setDisplayType(
                  displayType: typelist[value!]);
              print("sdjsdhvb ${data}");
            },
          ),
        )
      ],
    );
  }
}
