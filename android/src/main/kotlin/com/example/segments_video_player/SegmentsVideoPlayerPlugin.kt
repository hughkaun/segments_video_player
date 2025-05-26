package com.example.segments_video_player

import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.platform.PlatformViewRegistry
import com.example.segments_video_player.SegmentsVideoPlayerView
import com.example.segments_video_player.SegmentsVideoPlayerViewFactory


class SegmentsVideoPlayerPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private lateinit var playerView: SegmentsVideoPlayerView
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        playerView = SegmentsVideoPlayerView(context)

        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "segments_video_player")
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "segments_video_player_events")

        methodChannel.setMethodCallHandler(this)
        eventChannel.setStreamHandler(this)

        val registry: PlatformViewRegistry = flutterPluginBinding.platformViewRegistry
        registry.registerViewFactory("segments_video_player_view", SegmentsVideoPlayerViewFactory(context))
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "initialize" -> initialize(call, result)
            "play" -> play(result)
            "pause" -> pause(result)
            "dispose" -> dispose(result)
            else -> result.notImplemented()
        }
    }

    private fun initialize(call: MethodCall, result: Result) {
        val segments = call.argument<List<Map<String, Any>>>("segments")
        if (segments != null) {
            try {
                playerView.initialize(segments)
                result.success(null)
            } catch (e: IllegalArgumentException) {
                result.error("Invalid segments", e.message, null)
            }
        } else {
            result.error("Invalid arguments", "Segments cannot be null", null)
        }
    }

    private fun play(result: Result) {
        try {
            playerView.play()
            result.success(null)
        } catch (e: IllegalStateException) {
            result.error("Player is not initialized", e.message, null)
        }
    }

    private fun pause(result: Result) {
        try {
            playerView.pause()
            result.success(null)
        } catch (e: IllegalStateException) {
            result.error("Player is not initialized", e.message, null)
        }
    }

    private fun dispose(result: Result) {
        playerView.dispose()
        result.success(null)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        playerView.setEventSink(events)
    }

    override fun onCancel(arguments: Any?) {
        playerView.setEventSink(null)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }
}
