import 'package:flutter/services.dart';

class DisplaySdkFlutter {
  final methodChannel = const MethodChannel('display_sdk_flutter');
  final String _setDisplayType = "setDisplayType";
  final String _permissionAccess = "permissionAccess";
  final String _serialPortFinder = "serialPortFinder";
  final String _displayConnectSdk = "displayConnectSdk";

  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  Future<bool?> permissionAccess() async {
    final version = await methodChannel.invokeMethod<bool?>(_permissionAccess);
    return version;
  }

  Future<bool?> setDisplayType({required String displayType}) async {
    final version = await methodChannel
        .invokeMethod<bool?>(_setDisplayType, {"displayType": displayType});
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

  Future<bool?> displayConnectSdk(
      {required String serialPort, required int serialBaudrate}) async {
    final version = await methodChannel.invokeMethod<bool>(_displayConnectSdk,
        {"serialPort": serialPort, "serialBaudrate": serialBaudrate});
    return version;
  }
}
