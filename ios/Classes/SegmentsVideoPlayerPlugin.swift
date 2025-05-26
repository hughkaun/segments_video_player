import Flutter
import UIKit

public class SegmentsVideoPlayerPlugin: NSObject, FlutterPlugin {
  private var eventSink: FlutterEventSink?
  private var segmentsVideoPlayerView: SegmentsVideoPlayerView?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let methodChannel = FlutterMethodChannel(
      name: "segments_video_player", binaryMessenger: registrar.messenger())
    let eventChannel = FlutterEventChannel(
      name: "segments_video_player_events", binaryMessenger: registrar.messenger())

    let instance = SegmentsVideoPlayerPlugin()
    registrar.addMethodCallDelegate(instance, channel: methodChannel)
    eventChannel.setStreamHandler(instance)

    registrar.register(SegmentsVideoPlayerViewFactory(), withId: "segments_video_player_view")
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "initialize":
      if let args = call.arguments as? [String: Any],
        let segments = args["segments"] as? [[String: Any]],
        let width = args["width"] as? Double,
        let height = args["height"] as? Double
      {
        segmentsVideoPlayerView?.initialize(with: segments, width: width, height: height)
      }
      result(nil)
    case "play":
      segmentsVideoPlayerView?.play()
      result(nil)
    case "pause":
      segmentsVideoPlayerView?.pause()
      result(nil)
    case "stop":
      segmentsVideoPlayerView?.stop()
      result(nil)
    case "seekTo":
      if let args = call.arguments as? [String: Any],
        let position = args["position"] as? Double
      {
        segmentsVideoPlayerView?.seekTo(
          position: CMTime(seconds: position, preferredTimescale: 600))
      }
      result(nil)
    case "dispose":
      segmentsVideoPlayerView?.dispose()
      result(nil)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}

extension SegmentsVideoPlayerPlugin: FlutterStreamHandler {
  public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink)
    -> FlutterError?
  {
    self.eventSink = events
    NotificationCenter.default.addObserver(
      forName: .playerPositionChanged, object: nil, queue: .main
    ) { notification in
      if let position = notification.userInfo?["position"] as? Double {
        events(["event": "position", "position": position * 1000])
      }
    }
    return nil
  }

  public func onCancel(withArguments arguments: Any?) -> FlutterError? {
    NotificationCenter.default.removeObserver(self, name: .playerPositionChanged, object: nil)
    self.eventSink = nil
    return nil
  }
}
