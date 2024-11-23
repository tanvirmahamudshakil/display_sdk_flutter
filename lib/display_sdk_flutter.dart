import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class DisplaySdkFlutter {
  final methodChannel = const MethodChannel('display_sdk_flutter');
  final String _setDisplayType = "setDisplayType";
  final String _permissionAccess = "permissionAccess";
  final String _serialPortFinder = "serialPortFinder";
  final String _displayConnectSdk = "displayConnectSdk";
  final String _connectionCheck = "connectionCheck";
  final String _clearLine = "clearLine";
  final String _distroy = "distroy";
  final String _clearScreen = "clearScreen";
  final String _ledInit = "ledInit";
  final String _disconnect = "disconnect";
  final String _displayText = "displayText";
  final String _letStatusLight = "letStatusLight";


  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  Future<bool?> permissionAccess() async {
    final version = await methodChannel.invokeMethod<bool?>(_permissionAccess);
    return version;
  }

  Future<bool?> setDisplayType({required String displayType}) async {
    final version = await methodChannel.invokeMethod<bool?>(_setDisplayType, {"displayType": displayType});
    return version;
  }

  Future<List<String>> serialPortFinder() async {
    final version = await methodChannel.invokeMethod<List>(_serialPortFinder);
    List<String> list = version!
        .where((element) => element != null) // Filter out null values
        .map((e) => e.toString()) // Convert each element to string
        .toList();
    return list;
  }

  Future<bool?> displayConnectSdk({required String serialPort, required int serialBaudrate}) async {
    final version = await methodChannel.invokeMethod<bool>(_displayConnectSdk, {"serialPort": serialPort, "serialBaudrate": serialBaudrate});
    return version;
  }

  Future<bool?> connectionCheck() async {
    final version = await methodChannel.invokeMethod<bool>(_connectionCheck);
    return version;
  }

  Future<bool?> distroySdk() async {
    final version = await methodChannel.invokeMethod<bool>(_distroy);
    return version;
  }

  Future<bool?> clearLine() async {
    final version = await methodChannel.invokeMethod<bool>(_clearLine);
    return version;
  }

  Future<bool?> clearScreen() async {
    final version = await methodChannel.invokeMethod<bool>(_clearScreen);
    return version;
  }

  Future<bool?> ledInit() async {
    final version = await methodChannel.invokeMethod<bool>(_ledInit);
    return version;
  }

  Future<bool?> disconnect() async {
    final version = await methodChannel.invokeMethod<bool>(_disconnect);
    return version;
  }

  Future<bool?> displayText({required String text}) async {
    final version = await methodChannel.invokeMethod<bool>(_displayText, {"text": text});
    return version;
  }

  Future<bool?> ledStatusLight({required int status}) async {
    final version = await methodChannel.invokeMethod<bool>(_letStatusLight, {"status": status});
    return version;
  }

  void permissionForDisplaySdk() async {
    var manageExternalStorage = await Permission.manageExternalStorage.status;
    var storage = await Permission.storage.status;
    if (manageExternalStorage.isDenied) {
      await Permission.manageExternalStorage.request();
    }

    if (storage.isDenied) {
      await Permission.storage.request();
    }
  }

}
