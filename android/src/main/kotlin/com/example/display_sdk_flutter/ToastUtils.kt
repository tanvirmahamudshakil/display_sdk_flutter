package com.example.display_sdk_flutter

import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var toast: Toast? = null

    fun showToast(context: Context, text: String?) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(text) //如果不为空，则直接改变当前toast的文本
        }
        toast!!.show()
    }
}