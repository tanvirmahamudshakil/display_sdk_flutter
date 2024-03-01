package com.example.display_sdk_flutter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.pavolibrary.commands.DeviceAPI
import com.pavolibrary.commands.LedAPI
import io.flutter.plugin.common.MethodChannel
import java.util.Timer
import java.util.TimerTask


class PD108(mcontext : Context,serialPort : String, serialBaudrate: Int, serialFlag: Int) {
    private var context : Context;
    private var serialPort = "USB" //定义串口号
    private var serialBaudrate = 9600 //定义波特率
    private var serialFlag = 0
    private var runnable: Runnable? = null
    private var timerKeppTesting: Timer? = null
    private var isTesting = false
    private val testDisplayCount = 1
    private var testStatusCount = 1
    private var usbReceiver: UsbReceiver? = null
    private var mLed8: LedAPI? = null
    init {
        context = mcontext
        this.serialPort = serialPort
        this.serialBaudrate = serialBaudrate
        this.serialFlag = serialFlag
        mLed8 = LedAPI(context)
        onStart()
    }

    fun connectedCheck(result: MethodChannel.Result) {
        if(mLed8 != null && mLed8!!.isConnect){
            result.success(true)
        }else{
            result.success(false)
        }
    }

    fun onStart() {
        val intentFilter: IntentFilter
        usbReceiver = UsbReceiver(context)
        intentFilter = IntentFilter()
        intentFilter.addAction(DeviceAPI.ACTION_USB_DISCONNECTED)
        context.registerReceiver(usbReceiver, intentFilter)
    }
    fun onStop() {
        context.unregisterReceiver(usbReceiver)
    }
    fun onDestroy() {
        if (mLed8 != null) {
            mLed8!!.disconnect()
            mLed8 = null
        }
    }

     fun connect(result: MethodChannel.Result) {
        mLed8?.disconnect()
        runnable = Runnable {
            if (DeviceAPI.SUCCESS == mLed8!!.connect(serialPort, serialBaudrate, serialFlag)) {
                result.success(true)
            } else {
                result.success(false)
            }
        }
        Thread(runnable).start()
    }

    fun displayText(text: String) {
        if (mLed8 != null && mLed8!!.isConnect && text.isNotEmpty()) {
            mLed8!!.LED_Display(text, "ASCII")
        }
    }
    fun cleanLine() {
        if (mLed8 != null && mLed8!!.isConnect) {
            mLed8!!.LED_ClearLine()
        }
    }

    fun cleanScreen() {
        if (mLed8 != null && mLed8!!.isConnect) {
            mLed8!!.LED_ClearScreen()
        }
    }

    fun ledInit() {
        if (mLed8 != null && mLed8!!.isConnect) {
            mLed8!!.LED_Init()
        }
    }
    fun disConnect() {
        if (mLed8 != null && mLed8!!.isConnect) {
            mLed8!!.disconnect()
        }
        mLed8 = null
    }

    fun testingEnable(value : Boolean) {
        if(value) {
            if (mLed8 != null && mLed8!!.isConnect) {
                cancelKeepTesting()
                keepTesting()
            }
        }else{
            cancelKeepTesting()
        }
    }


    fun keepTesting() {
        try {
            if (timerKeppTesting == null) {
                timerKeppTesting = Timer()
                timerKeppTesting!!.schedule(object : TimerTask() {
                    var Testflag = false
                    override fun run() {
                        if (Testflag) return
                        Testflag = true
                        isTesting = true
                        if (mLed8 != null && mLed8!!.isConnect) {
                            if (testStatusCount == 0) testStatusCount = 4
                            mLed8!!.LED_SetStatusLight(testStatusCount)
                            when (testStatusCount) {
                                4 -> mLed8!!.LED_Display("8.8.", "ASCII")
                                3 -> mLed8!!.LED_Display("8.8.8.8.", "ASCII")
                                2 -> mLed8!!.LED_Display("8.8.8.8.8.8.", "ASCII")
                                1 -> mLed8!!.LED_Display("8.8.8.8.8.8.8.8.", "ASCII")
                            }
                            testStatusCount--
                        }
                        Testflag = false
                    }
                }, 0, 1000)
            }
        } catch (e: Exception) {
            isTesting = false
        }
    }

    private fun cancelKeepTesting() {
        if (timerKeppTesting != null) {
            timerKeppTesting!!.cancel()
            timerKeppTesting = null
            testStatusCount = 4
            isTesting = false
        }
    }

    private class UsbReceiver(context: Context) : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            if (arg1.action == DeviceAPI.ACTION_USB_DISCONNECTED) {
                ToastUtils.showToast(arg0, "USB disconnected")

            }
        }
    }

}