package com.example.display_sdk_flutter

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.pavolibrary.utils.LogUtils
import com.serialport.api.SerialPortFinder
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class DisplaySdkFlutterPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel : MethodChannel
  private var setDisplayType : String = "setDisplayType"
  private var permissionAccess : String = "permissionAccess";
  private var serialPortFinder = "serialPortFinder";
  private var displayConnectSdk = "displayConnectSdk";


  private var displayType : String = "PD108";
  private var serialBaudrate : Int? = 9600
  private var serialPort : String? = "USB"
  private var serialFlag : Int? = 0;
  private lateinit var context : Context;
  private lateinit var activity : Activity

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "display_sdk_flutter")
    context = flutterPluginBinding.applicationContext
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == setDisplayType){
      setDisplayType(call, result)
    }else if (call.method == permissionAccess){
      permissionAccess()
    }else if (call.method == serialPortFinder){
      serialPortFinder(call, result)
    } else if(call.method == displayConnectSdk) {
      sdkConnect(call, result)
    }
    else {
      result.notImplemented()
    }
  }

  fun setDisplayType(call: MethodCall, result: Result) {
    var type: String? = call.argument<String>("displayType")
    if(type != null) {
      displayType = type;
      result.success(true)
    }else{
      result.success(false)
    }
  }

  fun permissionAccess() {
    LogUtils.isDeBug = true
    LogUtils.isWrite = true
    if (LogUtils.isWrite) {
      verifyPermissions(activity) //申请存储权限
    }
  }

  fun verifyPermissions(activity: Activity?) {
    XXPermissions.with(activity) // 适配 Android 11 分区存储这样写
      .permission(Permission.MANAGE_EXTERNAL_STORAGE)
      .interceptor(PermissionInterceptor())
      .request { permissions, all ->
        if (all) {
          Toast.makeText(
            context,
            "permission successfull",
            Toast.LENGTH_SHORT
          ).show()
        }
      }
  }

  fun serialPortFinder(call: MethodCall, result: Result) {
    var mSerialPortFinder = SerialPortFinder()
    val entryValues: Array<String> = mSerialPortFinder.getAllDevicesPath()
    val allDevices: MutableList<String> = ArrayList()
    allDevices.add("USB")
    for (i in entryValues.indices) {
      allDevices.add(entryValues[i])
    }
    result.success(allDevices)
  }

  fun sdkConnect(call: MethodCall, result: Result) {
    serialPort = call.argument<String>("serialPort")
    serialBaudrate = call.argument<Int>("serialBaudrate")
    var pd108 = PD108(context,serialPort!!,serialBaudrate!!, serialFlag!!)
    pd108.connect(result)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {

  }
}
