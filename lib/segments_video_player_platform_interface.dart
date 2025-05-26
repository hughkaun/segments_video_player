import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'segments_video_player_method_channel.dart';
import 'video_segment.dart';

abstract class SegmentsVideoPlayerPlatform extends PlatformInterface {
  /// Constructs a SegmentsVideoPlayerPlatform.
  SegmentsVideoPlayerPlatform() : super(token: _token);

  static final Object _token = Object();

  static SegmentsVideoPlayerPlatform _instance =
      MethodChannelSegmentsVideoPlayer();

  /// The default instance of [SegmentsVideoPlayerPlatform] to use.
  ///
  /// Defaults to [MethodChannelSegmentsVideoPlayer].
  static SegmentsVideoPlayerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SegmentsVideoPlayerPlatform] when
  /// they register themselves.
  static set instance(SegmentsVideoPlayerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Initializes the video player with the given segments, width, and height.
  ///
  /// [segments] is a list of [VideoSegment] objects defining the video segments.
  /// [width] and [height] define the dimensions of the video.
  Future<void> initialize(
      List<VideoSegment> segments, double width, double height) {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  /// Starts playback of the video.
  Future<void> play() {
    throw UnimplementedError('play() has not been implemented.');
  }

  /// Pauses playback of the video.
  Future<void> pause() {
    throw UnimplementedError('pause() has not been implemented.');
  }

  /// Stops playback and resets the player to the beginning.
  Future<void> stop() {
    throw UnimplementedError('stop() has not been implemented.');
  }

  /// Seeks to the specified position in the video.
  ///
  /// [position] specifies the position to seek to.
  Future<void> seekTo(Duration position) {
    throw UnimplementedError('seekTo() has not been implemented.');
  }

  /// Disposes of the video player resources.
  Future<void> dispose() {
    throw UnimplementedError('dispose() has not been implemented.');
  }

  /// Returns a stream of the current playback position.
  ///
  /// The stream emits [Duration] objects representing the current position.
  Stream<Duration> getPositionStream() {
    throw UnimplementedError('getPositionStream() has not been implemented.');
  }
}
