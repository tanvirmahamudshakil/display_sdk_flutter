//package com.example.display_sdk_flutter.LedApi
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.hardware.usb.UsbDevice
//import android.hardware.usb.UsbDeviceConnection
//import android.hardware.usb.UsbManager
//import android.util.Log
//import com.felhr.usbserial.UsbSerialDevice
//import com.pavolibrary.io.SerialAPI
//import io.flutter.plugin.common.MethodChannel.Result
//import java.io.File
//
//class Printer80Led(context: Context, serialPort : String, serialBaudrate: Int, serialFlag: Int) {
//    private var serialPort = "USB" //定义串口号
//    private var serialBaudrate = 9600 //定义波特率
//    private var serialFlag = 0
//    private var connected : Boolean = false;
//    private var mContext : Context = context;
//    private lateinit var usbManager: UsbManager
////    private var usbDevice: UsbDevice? = null
////    private var usbConnection: UsbDeviceConnection? = null
////    var port: UsbSerialDevice? = null
//    var serialport: SerialAPI? = null
//
//    init {
//        this.serialPort = serialPort
//        this.serialBaudrate = serialBaudrate
//        this.serialFlag = serialFlag
////        usbManager = mContext.getSystemService(Context.USB_SERVICE) as UsbManager
//        serialport = SerialAPI(File(serialPort), serialBaudrate, serialFlag)
//    }
//
//
//    @SuppressLint("SuspiciousIndentation")
//    fun connect(result: Result) {
//        if(serialport?.isOpen == true) {
//            disConnect()
//        }
//
//
//
//        if(serialport?.isOpen == false) {
//          var d =  serialport?.openDevice();
//            if(d == 0) {
//                result.success(true)
//            }else{
//                result.success(false)
//            }
//
//        }else{
//            result.success(true)
//        }
//
//
////        val deviceList = usbManager.deviceList
////        usbDevice = deviceList.values.find { it.deviceName == serialport.filePath}
////        deviceList.forEach {
////            Log.e("serial Port Data", "connect: ${it.key} ---- ${File(serialPort).path}", )
////        }
////        if(usbDevice != null) {
////            usbConnection =  usbManager.openDevice(usbDevice)
////            if(usbConnection != null) {
////                 port = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbConnection);
////                if (port != null) {
////                    port?.open();
////                    port?.setBaudRate(serialBaudrate);
////                    port?.setDataBits(UsbSerialInterface.DATA_BITS_8);
////                    port?.setStopBits(UsbSerialInterface.STOP_BITS_1);
////                    port?.setParity(serialFlag);
////                    connected = true
////                    result.success(connected)
////                }else{
////                    connected = false;
////                    result.success(connected)
////                }
////            }else{
////                connected = false;
////                result.success(connected)
////            }
////        }else{
////            connected = false;
////            result.success(connected)
////        }
//
//    }
//
//    fun isConnected() : Boolean {
//        return connected;
//    }
//
//    fun disConnect() {
//        serialport?.closeDevice()
////        if(port != null) {
////            port?.close()
////            port = null;
////        }
////       if(usbConnection != null) {
////           usbConnection?.close()
////           connected = false
////           usbConnection = null;
////       }
//
//    }
//
//    fun sendTex(lightType: Int, text: String, result: Result) {
//        Log.e("send text", "connect: ${text} ---")
//        if(serialport != null) {
//
//            val guestDisplay = GuestDisplay(serialport)
//            guestDisplay.sendDisplayInstruction(lightType, text)
//            result.success(true);
//        }else{
//            Log.e("send Not text", "connect: ${text} ---")
//            result.success(false);
//        }
//    }
//
//}
//
//class GuestDisplay(private val serialPort: SerialAPI?) {
//    fun sendDisplayInstruction(lightType: Int, firstRow: String) {
//        if (serialPort == null) return
//
//        var lightCommand = ""
//        when (lightType) {
//            1 -> lightCommand = "\u001B\u0073\u0032" // ESC s 2 total
//            2 -> lightCommand = "\u001B\u0073\u0033" // ESC s 3 price
//            3 -> lightCommand = "\u001B\u0073\u0034" // ESC s 4 change
//        }
//        val message = lightCommand + "\u001B\u0041" + firstRow.trim { it <= ' ' } + "\r"
////        serialPort.write(message.toByteArray())
//        val bytes = message.toByteArray()
//        serialPort.writeBuffer(bytes, 0, bytes.size, 100)
//
//    }
//}