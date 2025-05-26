package com.example.segmentsvideoplayer;

import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.platform.PlatformViewRegistry;

import java.util.List;
import java.util.Map;

public class SegmentsVideoPlayerPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private SegmentsVideoPlayerView playerView;
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        playerView = new SegmentsVideoPlayerView(context);

        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "segments_video_player");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "segments_video_player_events");

        methodChannel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);

        PlatformViewRegistry registry = flutterPluginBinding.getPlatformViewRegistry();
        registry.registerViewFactory("segments_video_player_view", new SegmentsVideoPlayerViewFactory(context));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "initialize":
                initialize(call, result);
                break;
            case "play":
                play(result);
                break;
            case "pause":
                pause(result);
                break;
            case "dispose":
                dispose(result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void initialize(MethodCall call, Result result) {
        List<Map<String, Object>> segments = call.argument("segments");
        try {
            playerView.initialize(segments);
            result.success(null);
        } catch (IllegalArgumentException e) {
            result.error("Invalid segments", e.getMessage(), null);
        }
    }

    private void play(Result result) {
        try {
            playerView.play();
            result.success(null);
        } catch (IllegalStateException e) {
            result.error("Player is not initialized", e.getMessage(), null);
        }
    }

    private void pause(Result result) {
        try {
            playerView.pause();
            result.success(null);
        } catch (IllegalStateException e) {
            result.error("Player is not initialized", e.getMessage(), null);
        }
    }

    private void dispose(Result result) {
        playerView.dispose();
        result.success(null);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        playerView.setEventSink(events);
    }

    @Override
    public void onCancel(Object arguments) {
        playerView.setEventSink(null);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
    }
}
