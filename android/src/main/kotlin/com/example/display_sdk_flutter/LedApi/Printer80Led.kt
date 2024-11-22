package com.example.display_sdk_flutter.LedApi

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface

class Printer80Led(context: Context) {

    private var connected : Boolean = false;
    private var mContext : Context = context;
    private lateinit var usbManager: UsbManager
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    var port: UsbSerialDevice? = null

    init {
        usbManager = mContext.getSystemService(Context.USB_SERVICE) as UsbManager
    }


    fun connect(serialPort : String, baudRate : Int, flowControl : Int) {
        val deviceList = usbManager.deviceList
        usbDevice = deviceList.values.find { it.deviceName == serialPort}
        if(usbDevice != null) {
            usbConnection =  usbManager.openDevice(usbDevice)
            if(usbConnection != null) {
                 port = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbConnection);
                if (port != null) {
                    port?.open();
                    port?.setBaudRate(baudRate);
                    port?.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    port?.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    port?.setParity(flowControl);
                    connected = true
                }else{
                    connected = false;
                }
            }else{
                connected = false;
            }
        }else{
            connected = false;
        }

    }

    fun isConnected() : Boolean {
        return connected;
    }

    fun disConnect() {
        if(port != null) {
            port?.close()
            port = null;
        }
       if(usbConnection != null) {
           usbConnection?.close()
           connected = false
           usbConnection = null;
       }
    }

    fun sendTex(lightType: Int, text: String) {
        if(port != null) {
            val guestDisplay = GuestDisplay(port)
            guestDisplay.sendDisplayInstruction(lightType, text)
        }
    }

}

class GuestDisplay(private val serialPort: UsbSerialDevice?) {
    fun sendDisplayInstruction(lightType: Int, firstRow: String) {
        if (serialPort == null) return

        var lightCommand = ""
        when (lightType) {
            1 -> lightCommand = "\u001B\u0073\u0032" // ESC s 2 total
            2 -> lightCommand = "\u001B\u0073\u0033" // ESC s 3 price
            3 -> lightCommand = "\u001B\u0073\u0034" // ESC s 4 change
        }
        val message = lightCommand + "\u001B\u0041" + firstRow.trim { it <= ' ' } + "\r"
        serialPort.write(message.toByteArray())
    }
}