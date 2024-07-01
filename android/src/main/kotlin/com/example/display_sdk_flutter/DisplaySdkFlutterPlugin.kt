package com.example.display_sdk_flutter

//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.felhr.usbserial.UsbSerialDevice
import com.pavolibrary.utils.LogUtils
import com.serialport.api.SerialPortFinder
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class DisplaySdkFlutterPlugin: FlutterPlugin, MethodCallHandler, ActivityAware,
  EventChannel.StreamHandler {

  private val TAG: String = "UsbSerialPortAdapter"

//  private val m_Context: Context? = null
  private var m_Manager: UsbManager? = null
  private var m_InterfaceId = 0
  private var m_Messenger: BinaryMessenger? = null
  private var m_EventSink: EventSink? = null

  private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
  val ACTION_USB_ATTACHED: String = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
  val ACTION_USB_DETACHED: String = "android.hardware.usb.action.USB_DEVICE_DETACHED"

  private lateinit var channel : MethodChannel
  private var setDisplayType : String = "setDisplayType"
  private var permissionAccess : String = "permissionAccess";
  private var serialPortFinder = "serialPortFinder";
  private var displayConnectSdk = "displayConnectSdk";
  private var connectionCheck = "connectionCheck";
  private var distroysdk = "distroy";
  private var clearLine = "clearLine";
  private var clearScreen = "clearScreen";
  private var ledInit = "ledInit";
  private var disconnect = "disconnect";
  private var displayText = "displayText";
  private var letStatusLight = "letStatusLight";


  private var displayType : String = "PD108";
  private var serialBaudrate : Int? = 9600
  private var serialPort : String? = "USB"
  private var serialFlag : Int? = 0;
  private  var pd108 : PD108? = null;
  private var context : Context? = null;
  private lateinit var activity : Activity

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    register(flutterPluginBinding.binaryMessenger, flutterPluginBinding.applicationContext)
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "display_sdk_flutter")
    context = flutterPluginBinding.applicationContext
    //pd108 = PD108(context,serialPort!!,serialBaudrate!!, serialFlag!!)
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == setDisplayType){
      setDisplayType(call, result)
    } else if (call.method == permissionAccess){
      permissionAccess()
    } else if (call.method == serialPortFinder){
      serialPortFinder(call, result)
    } else if(call.method == displayConnectSdk) {
      sdkConnect(call, result)
    } else if(call.method == connectionCheck){
      connectionCheck(call, result)
    } else if(call.method == distroysdk){
      distroySdk(call, result)
    } else if (call.method == clearLine){
      clearLine(call, result)
    } else if (call.method == clearScreen){
      clearScreen(call, result)
    } else if (call.method == ledInit){
      ledInit(call, result)
    } else if (call.method == disconnect){
      disconnected(call, result)
    } else if (call.method == displayText){
      displayText(call, result)
    } else if (call.method == letStatusLight) {
      ledSetStatusLight(call, result)
    }else if (call.method == "create") {
      val type = call.argument<String>("type")
      val vid = call.argument<Int>("vid")
      val pid = call.argument<Int>("pid")
      val deviceId = call.argument<Int>("deviceId")
      val interfaceId = call.argument<Int>("interface")
      if (type != null && vid != null && pid != null && deviceId != null && interfaceId != null) {
        createTyped(type, vid, pid, deviceId, interfaceId, result)
      }
    }else if (call.method == "listDevices") {
      listDevices(result)
    } else {
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

  fun ledSetStatusLight(call: MethodCall, result: Result) {
    var type: Int? = call.argument<Int>("status")
    if (type != null) {
      pd108?.ledSetStatusLight(type)
      result.success(true);
    }else{
      result.success(false);
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
//    XXPermissions.with(activity) // 适配 Android 11 分区存储这样写
//      .permission(Permission.MANAGE_EXTERNAL_STORAGE)
//      .interceptor(PermissionInterceptor())
//      .request { permissions, all ->
//        if (all) {
//          Toast.makeText(
//            context,
//            "permission successfull",
//            Toast.LENGTH_SHORT
//          ).show()
//        }
//      }
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
    pd108 = context?.let { PD108(it,serialPort!!,serialBaudrate!!, serialFlag!!) }
    pd108?.connect(result)
  }

  fun connectionCheck(call: MethodCall, result: Result) {

    if(pd108 != null) {
      pd108?.connectedCheck(result)
    }else{
      result.success(false);
    }

  }

  fun distroySdk(call: MethodCall, result: Result) {
    pd108?.onDestroy(result);
  }

  fun clearLine(call: MethodCall, result: Result) {
    pd108?.cleanLine(result)
  }

  fun clearScreen(call: MethodCall, result: Result) {
    pd108?.cleanScreen(result)
  }

  fun ledInit(call: MethodCall, result: Result) {
    pd108?.ledInit(result)
  }

  fun disconnected(call: MethodCall, result: Result) {
    pd108?.disConnect(result)
  }

  fun displayText(call: MethodCall, result: Result) {
    var textdata : String? = call.argument<String>("text")
    pd108?.displayText(textdata!!, result)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    unregister()
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




// USB Serial

  private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    private fun getUsbDeviceFromIntent(intent: Intent): UsbDevice? {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return intent.getParcelableExtra(
          UsbManager.EXTRA_DEVICE,
          UsbDevice::class.java
        )
      } else {
        // Create local variable to keep scope of deprecation suppression smallest
        @Suppress("deprecation") val ret = intent.getParcelableExtra<UsbDevice>(
          UsbManager.EXTRA_DEVICE
        )
        return ret
      }
    }

    override fun onReceive(context: Context, intent: Intent) {
      val action = intent.action ?: return
      if (action == ACTION_USB_ATTACHED) {
        Log.d(TAG, "ACTION_USB_ATTACHED")
        if (m_EventSink != null) {
          val device = getUsbDeviceFromIntent(intent)
          if (device != null) {
            val msg = serializeDevice(device)
            msg["event"] = ACTION_USB_ATTACHED
            m_EventSink?.success(msg)
          } else {
            Log.e(TAG, "ACTION_USB_ATTACHED but no EXTRA_DEVICE")
          }
        }
      } else if (action == ACTION_USB_DETACHED) {
        Log.d(TAG, "ACTION_USB_DETACHED")
        if (m_EventSink != null) {
          val device = getUsbDeviceFromIntent(intent)
          if (device != null) {
            val msg = serializeDevice(device)
            msg["event"] = ACTION_USB_DETACHED
            m_EventSink?.success(msg)
          } else {
            Log.e(TAG, "ACTION_USB_DETACHED but no EXTRA_DEVICE")
          }
        }
      }
    }
  }

//  fun UsbSerialPlugin() {
//    m_Messenger = null
//    context = null
//    m_Manager = null
//    m_InterfaceId = 0
//  }


  private interface AcquirePermissionCallback {
    fun onSuccess(device: UsbDevice?)
    fun onFailed(device: UsbDevice?)
  }

  @SuppressLint("PrivateApi")
  private fun acquirePermissions(device: UsbDevice, cb: AcquirePermissionCallback) {
    class BRC2(private val m_Device: UsbDevice, private val m_CB: AcquirePermissionCallback) :
      BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (ACTION_USB_PERMISSION == action) {
          Log.e(TAG, "BroadcastReceiver intent arrived, entering sync...")
          context.unregisterReceiver(this)
          synchronized(this) {
            Log.e(TAG, "BroadcastReceiver in sync")
            /* UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE); */
            if (intent.getBooleanExtra(
                UsbManager.EXTRA_PERMISSION_GRANTED,
                false
              )
            ) {
              // createPort(m_DriverIndex, m_PortIndex, m_Result, false);
              m_CB.onSuccess(m_Device)
            } else {
              Log.d(TAG, "permission denied for device ")
              m_CB.onFailed(m_Device)
            }
          }
        }
      }
    }

    val cw: Context? = context //m_Registrar.context();

    val usbReceiver = BRC2(device, cb)

    var flags = 0

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      flags = PendingIntent.FLAG_MUTABLE
    }

    val intent: Intent = Intent(ACTION_USB_PERMISSION)

    var activityThread: Class<*>? = null
    try {
      activityThread = Class.forName("android.app.ActivityThread")
      val method = activityThread.getDeclaredMethod("currentPackageName")
      val appPackageName = method.invoke(activityThread) as String
      intent.setPackage(appPackageName)
    } catch (e: Exception) {
      // Not too important to throw anything
    }

    val permissionIntent = PendingIntent.getBroadcast(cw, 0, intent, flags)

    val filter: IntentFilter = IntentFilter(ACTION_USB_PERMISSION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      cw?.registerReceiver(usbReceiver, filter, null, null, Context.RECEIVER_NOT_EXPORTED)
    } else {
      cw?.registerReceiver(usbReceiver, filter)
    }
    m_Manager!!.requestPermission(device, permissionIntent)
  }

  private fun openDevice(
    type: String,
    device: UsbDevice,
    iface: Int,
    result: Result,
    allowAcquirePermission: Boolean
  ) {
    val cb: AcquirePermissionCallback = object : AcquirePermissionCallback {
      override fun onSuccess(device: UsbDevice?) {
        if (device != null) {
          openDevice(type, device, iface, result, false)
        }
      }

      override fun onFailed(device: UsbDevice?) {
        result.error(TAG, "Failed to acquire permissions.", null)
      }
    }

    try {
      val connection = m_Manager!!.openDevice(device)

      if (connection == null && allowAcquirePermission) {
        acquirePermissions(device, cb)
        return
      }

      val serialDeviceDevice: UsbSerialDevice?
      if (type == "") {
        serialDeviceDevice = UsbSerialDevice.createUsbSerialDevice(device, connection, iface)
      } else {
        serialDeviceDevice = UsbSerialDevice.createUsbSerialDevice(device, connection, iface)
      }

      if (serialDeviceDevice != null) {
        val interfaceId = m_InterfaceId++
        val adapter: UsbSerialPortAdapter =
          UsbSerialPortAdapter(m_Messenger!!, interfaceId, connection, serialDeviceDevice)
        result.success(adapter.getMethodChannelName())
        Log.d(TAG, "success.")
        return
      }
      result.error(TAG, "Not an Serial device.", null)
    } catch (e: SecurityException) {
      if (allowAcquirePermission) {
        acquirePermissions(device, cb)
      } else {
        result.error(TAG, "Failed to acquire USB permission.", null)
      }
    } catch (e: Exception) {
      result.error(TAG, "Failed to acquire USB device.", null)
    }
  }

  private fun createTyped(
    type: String,
    vid: Int,
    pid: Int,
    deviceId: Int,
    iface: Int,
    result: Result
  ) {
    val devices: Map<String, UsbDevice> = m_Manager!!.deviceList
    for (device in devices.values) {
      if (deviceId == device.deviceId || (device.vendorId == vid && device.productId == pid)) {
        openDevice(type, device, iface, result, true)
        return
      }
    }

    result.error(TAG, "No such device", null)
  }

  private fun serializeDevice(device: UsbDevice): HashMap<String, Any?> {
    val dev = HashMap<String, Any?>()
    dev["deviceName"] = device.deviceName
    dev["vid"] = device.vendorId
    dev["pid"] = device.productId
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      dev["manufacturerName"] = device.manufacturerName
      dev["productName"] = device.productName
      dev["interfaceCount"] = device.interfaceCount
      /* if the app targets SDK >= android.os.Build.VERSION_CODES.Q and the app does not have permission to read from the device. */
      try {
        dev["serialNumber"] = device.serialNumber
      } catch (e: SecurityException) {
        Log.e(TAG, e.toString())
      }
    }
    dev["deviceId"] = device.deviceId
    return dev
  }

  private fun listDevices(result: Result) {
    val devices: Map<String, UsbDevice>? = m_Manager!!.deviceList
    if (devices == null) {
      result.error(TAG, "Could not get USB device list.", null)
      return
    }
    val transferDevices: MutableList<HashMap<String, Any?>> = java.util.ArrayList()

    for (device in devices.values) {
      transferDevices.add(serializeDevice(device))
    }
    result.success(transferDevices)
  }


  override fun onListen(o: Any?, eventSink: EventSink) {
    m_EventSink = eventSink
  }

  override fun onCancel(o: Any?) {
    m_EventSink = null
  }


  private var m_EventChannel: EventChannel? = null
  private fun register(messenger: BinaryMessenger, context: Context) {
    m_Messenger = messenger
    this.context = context
    m_Manager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
    m_InterfaceId = 100
    m_EventChannel = EventChannel(messenger, "usb_serial/usb_events")
    m_EventChannel!!.setStreamHandler(this)

    val filter = IntentFilter()
    filter.addAction(ACTION_USB_DETACHED)
    filter.addAction(ACTION_USB_ATTACHED)
    context.registerReceiver(usbReceiver, filter)
  }

  private fun unregister() {
    context?.unregisterReceiver(usbReceiver)
    m_EventChannel!!.setStreamHandler(null)
    m_Manager = null
    context = null
    m_Messenger = null
  }


}
