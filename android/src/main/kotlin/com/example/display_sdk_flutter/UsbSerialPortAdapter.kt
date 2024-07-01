package com.example.display_sdk_flutter

import android.hardware.usb.UsbDeviceConnection
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface.UsbReadCallback
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class UsbSerialPortAdapter(messenger : BinaryMessenger, interfaceId : Int, connection : UsbDeviceConnection, serialDevice : UsbSerialDevice) : MethodCallHandler, EventChannel.StreamHandler {
    private val TAG: String = UsbSerialPortAdapter::class.java.simpleName

    private var m_InterfaceId = 0
    private var m_Connection: UsbDeviceConnection? = null
    private var m_SerialDevice: UsbSerialDevice? = null
    private var m_Messenger: BinaryMessenger? = null
    private var m_MethodChannelName: String? = null
    private var m_EventSink: EventSink? = null
    private var m_handler: Handler? = null

    init {
        m_Messenger = messenger
        m_InterfaceId = interfaceId
        m_Connection = connection
        m_SerialDevice = serialDevice
        m_MethodChannelName = "usb_serial/UsbSerialPortAdapter/$interfaceId"
        m_handler = Handler(Looper.getMainLooper())
        val channel = MethodChannel(m_Messenger!!, m_MethodChannelName!!)
        channel.setMethodCallHandler(this)
        val eventChannel = EventChannel(m_Messenger, "$m_MethodChannelName/stream")
        eventChannel.setStreamHandler(this)
    }

    fun getMethodChannelName(): String? {
        return m_MethodChannelName
    }

    private fun setPortParameters(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int) {
        m_SerialDevice!!.setBaudRate(baudRate)
        m_SerialDevice!!.setDataBits(dataBits)
        m_SerialDevice!!.setStopBits(stopBits)
        m_SerialDevice!!.setParity(parity)
    }

    private fun setFlowControl(flowControl: Int) {
        m_SerialDevice!!.setFlowControl(flowControl)
    }

    private val mCallback =
        UsbReadCallback { arg0 ->
            if (m_EventSink != null) {
                m_handler!!.post {
                    if (m_EventSink != null) {
                        m_EventSink!!.success(arg0)
                    }
                }
            }
        }

    private fun open(): Boolean {
        if (m_SerialDevice!!.open()) {
            m_SerialDevice!!.read(mCallback)
            return true
        } else {
            return false
        }
    }

    private fun close(): Boolean {
        m_SerialDevice!!.close()
        return true
    }

    private fun write(data: ByteArray?) {
        m_SerialDevice!!.write(data)
    }

    // return true if the object is to be kept, false if it is to be destroyed.
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "close" -> result.success(close())
            "open" -> result.success(open())
            "write" -> {
                write(call.argument<Any>("data") as ByteArray?)
                result.success(true)
            }

            "setPortParameters" -> {
                setPortParameters(
                    call.argument<Any>("baudRate") as Int, call.argument<Any>("dataBits") as Int,
                    call.argument<Any>("stopBits") as Int, call.argument<Any>("parity") as Int
                )
                result.success(null)
            }

            "setFlowControl" -> {
                setFlowControl(call.argument<Any>("flowControl") as Int)
                result.success(null)
            }

            "setDTR" -> {
                val v = call.argument<Boolean>("value")!!
                m_SerialDevice!!.setDTR(v)
                if (v == true) {
                    Log.e(TAG, "set DTR to true")
                } else {
                    Log.e(TAG, "set DTR to false")
                }
                result.success(null)
            }

            "setRTS" -> {
                val v = call.argument<Boolean>("value")!!
                m_SerialDevice!!.setRTS(v)
                result.success(null)
            }

            else -> result.notImplemented()
        }
    }

    override fun onListen(o: Any?, eventSink: EventSink?) {
        m_EventSink = eventSink
    }

    override fun onCancel(o: Any?) {
        m_EventSink = null
    }


}