import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:segments_video_player/video_segment.dart';
import 'dart:io' show Platform;

class SegmentsVideoPlayerController {
  static const MethodChannel _channel = MethodChannel('segments_video_player');
  static const EventChannel _eventChannel =
      EventChannel('segments_video_player_events');

  final StreamController<Duration> _positionStreamController =
      StreamController<Duration>.broadcast();
  Stream<Duration> get positionStream => _positionStreamController.stream;

  SegmentsVideoPlayerController() {
    _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      final Map<dynamic, dynamic> map = event;
      if (map['event'] == 'position') {
        final position = Duration(milliseconds: map['position']);
        _positionStreamController.add(position);
      }
    });
  }

  Future<void> initialize(
      List<VideoSegment> segments, double width, double height) async {
    await _channel.invokeMethod('initialize', {
      'segments': segments.map((segment) => segment.toMap()).toList(),
      'width': width,
      'height': height,
    });
  }

  Future<void> play() async {
    await _channel.invokeMethod('play');
  }

  Future<void> pause() async {
    await _channel.invokeMethod('pause');
  }

  Future<void> stop() async {
    await _channel.invokeMethod('stop');
  }

  Future<void> seekTo(Duration position) async {
    await _channel
        .invokeMethod('seekTo', {'position': position.inMilliseconds});
  }

  Future<void> dispose() async {
    await _channel.invokeMethod('dispose');
    _positionStreamController.close();
  }
}

class SegmentsVideoPlayer extends StatefulWidget {
  final SegmentsVideoPlayerController controller;

  SegmentsVideoPlayer({required this.controller});

  @override
  _SegmentsVideoPlayerState createState() => _SegmentsVideoPlayerState();
}

class _SegmentsVideoPlayerState extends State<SegmentsVideoPlayer> {
  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return Container(
        child: AndroidView(
          viewType: 'segments_video_player_view',
          onPlatformViewCreated: (int id) {
            // Additional initialization if needed
          },
        ),
      );
    } else if (Platform.isIOS) {
      return Container(
        child: UiKitView(
          viewType: 'segments_video_player_view',
          onPlatformViewCreated: (int id) {
            // Additional initialization if needed
          },
        ),
      );
    } else {
      return Container(
        child: Center(
          child: Text('This platform is not supported'),
        ),
      );
    }
  }
}
