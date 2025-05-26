import 'dart:async';
import 'package:flutter/services.dart';
import 'package:segments_video_player/segments_video_player_platform_interface.dart';
import 'package:segments_video_player/video_segment.dart';

/// A class that interacts with the native platform using method channels and event channels
class MethodChannelSegmentsVideoPlayer extends SegmentsVideoPlayerPlatform {
  static const MethodChannel _channel = MethodChannel('segments_video_player');
  static const EventChannel _eventChannel =
      EventChannel('segments_video_player_events');

  final StreamController<Duration> _positionStreamController =
      StreamController<Duration>.broadcast();

  MethodChannelSegmentsVideoPlayer() {
    // Listen to the event channel for position updates
    _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      final Map<dynamic, dynamic> map = event;
      if (map['event'] == 'position') {
        final position = Duration(milliseconds: map['position']);
        _positionStreamController.add(position);
      }
    });
  }

  /// Initializes the video player with the given segments, width, and height.
  ///
  /// [segments] is a list of [VideoSegment] objects defining the video segments.
  /// [width] and [height] define the dimensions of the video.
  @override
  Future<void> initialize(
      List<VideoSegment> segments, double width, double height) async {
    await _channel.invokeMethod('initialize', {
      'segments': segments.map((segment) => segment.toMap()).toList(),
      'width': width,
      'height': height,
    });
  }

  /// Starts playback of the video.
  @override
  Future<void> play() async {
    await _channel.invokeMethod('play');
  }

  /// Pauses playback of the video.
  @override
  Future<void> pause() async {
    await _channel.invokeMethod('pause');
  }

  /// Stops playback and resets the player to the beginning.
  @override
  Future<void> stop() async {
    await _channel.invokeMethod('stop');
  }

  /// Seeks to the specified position in the video.
  ///
  /// [position] specifies the position to seek to.
  @override
  Future<void> seekTo(Duration position) async {
    await _channel
        .invokeMethod('seekTo', {'position': position.inMilliseconds});
  }

  /// Disposes of the video player resources.
  @override
  Future<void> dispose() async {
    await _channel.invokeMethod('dispose');
    _positionStreamController.close();
  }

  /// Returns a stream of the current playback position.
  ///
  /// The stream emits [Duration] objects representing the current position.
  @override
  Stream<Duration> getPositionStream() {
    return _positionStreamController.stream;
  }
}
