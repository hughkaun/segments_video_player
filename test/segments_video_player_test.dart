// import 'package:flutter_test/flutter_test.dart';
// import 'package:segments_video_player/segments_video_player.dart';
// import 'package:segments_video_player/segments_video_player_platform_interface.dart';
// import 'package:segments_video_player/segments_video_player_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockSegmentsVideoPlayerPlatform
//     with MockPlatformInterfaceMixin
//     implements SegmentsVideoPlayerPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final SegmentsVideoPlayerPlatform initialPlatform = SegmentsVideoPlayerPlatform.instance;

//   test('$MethodChannelSegmentsVideoPlayer is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelSegmentsVideoPlayer>());
//   });

//   test('getPlatformVersion', () async {
//     SegmentsVideoPlayer segmentsVideoPlayerPlugin = SegmentsVideoPlayer();
//     MockSegmentsVideoPlayerPlatform fakePlatform = MockSegmentsVideoPlayerPlatform();
//     SegmentsVideoPlayerPlatform.instance = fakePlatform;

//     expect(await segmentsVideoPlayerPlugin.getPlatformVersion(), '42');
//   });
// }
