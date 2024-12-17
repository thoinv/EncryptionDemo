package com.example.encryptiondemo

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

object DebugLog {
    private const val TAG = "DebugLog"
    var DEBUG: Boolean = true
     var callback: ((String?) -> Unit)? = null

    fun logd(obj: Any?) {
        if (obj == null || !DEBUG) return
        val message = obj.toString()
        val fullClassName = Thread.currentThread().stackTrace[3].className
        var className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"))
        }
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber

        Log.d(TAG, "at ($className.java:$lineNumber) [$methodName]$message")
        sendCallback(message)
    }

    private fun sendCallback(message: String?) {
        callback?.invoke(message)
    }

    fun logn(obj: Any?) {
        if (obj == null || !DEBUG) return
        val message = obj.toString()
        val fullClassName = Thread.currentThread().stackTrace[3].className
        var className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"))
        }
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber

        Log.i(TAG, "at ($className.java:$lineNumber) [$methodName]$message")
        sendCallback(message)
    }

    fun loge(obj: Any?) {
        if (obj == null || !DEBUG) return
        val message = obj.toString()
        val fullClassName = Thread.currentThread().stackTrace[3].className
        var className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"))
        }
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber

        Log.e(TAG, "at ($className.java:$lineNumber) [$methodName]$message")
        sendCallback(message)
    }

    fun loge(e: Exception?) {
        if (e == null || !DEBUG) return
        val errors = StringWriter()
        e.printStackTrace(PrintWriter(errors))

        val message = errors.toString()

        val fullClassName = Thread.currentThread().stackTrace[3].className
        var className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"))
        }
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber

        Log.e(TAG, "at ($className.java:$lineNumber) [$methodName]$message")
        sendCallback(e.message)
    }

    fun logi(obj: Any?) {
        if (obj == null || !DEBUG) return
        val message = obj.toString()
        val fullClassName = Thread.currentThread().stackTrace[3].className
        var className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"))
        }
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber

        Log.i(TAG, "at ($className.java:$lineNumber) [$methodName]$message")
        sendCallback(message)
    }
}
