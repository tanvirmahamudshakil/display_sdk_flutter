import 'package:flutter_test/flutter_test.dart';
import 'package:display_sdk_flutter/display_sdk_flutter.dart';
import 'package:display_sdk_flutter/display_sdk_flutter_platform_interface.dart';
import 'package:display_sdk_flutter/display_sdk_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockDisplaySdkFlutterPlatform
    with MockPlatformInterfaceMixin
    implements DisplaySdkFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final DisplaySdkFlutterPlatform initialPlatform = DisplaySdkFlutterPlatform.instance;

  test('$MethodChannelDisplaySdkFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelDisplaySdkFlutter>());
  });

  test('getPlatformVersion', () async {
    DisplaySdkFlutter displaySdkFlutterPlugin = DisplaySdkFlutter();
    MockDisplaySdkFlutterPlatform fakePlatform = MockDisplaySdkFlutterPlatform();
    DisplaySdkFlutterPlatform.instance = fakePlatform;

    expect(await displaySdkFlutterPlugin.getPlatformVersion(), '42');
  });
}
