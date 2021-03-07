package com.os.operando.advertisingid

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import kotlin.concurrent.thread

class AdvertisingIdPlugin() : FlutterPlugin, MethodCallHandler, ActivityAware {
    companion object {
        var activity: Activity? = null
        var context: Context? = null
        private var methodChannel: MethodChannel? = null
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        methodChannel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "advertising_id")
        methodChannel?.setMethodCallHandler(AdvertisingIdPlugin())
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel?.setMethodCallHandler(null)
        methodChannel = null
    }

    
    // note: this may be called multiple times on app startup
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        attachActivity(binding)
    }

    override fun onDetachedFromActivity() {
        detachActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        attachActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        detachActivity()
    }

    private fun attachActivity(binding: ActivityPluginBinding) {
        if (activity != null) detachActivity()
        activity = binding.activity
    }

    private fun detachActivity() {
        activity = null
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getAdvertisingId" -> thread {
                try {
                    val id = AdvertisingIdClient.getAdvertisingIdInfo(context).id
                    activity?.runOnUiThread {
                        result.success(id)
                    }
                } catch (e: Exception) {
                    activity?.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            "isLimitAdTrackingEnabled" -> thread {
                try {
                    val isLimitAdTrackingEnabled = AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled
                    activity?.runOnUiThread {
                        result.success(isLimitAdTrackingEnabled)
                    }
                } catch (e: Exception) {
                    activity?.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            else -> result.notImplemented()
        }
    }
}
