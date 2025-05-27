package com.example.segments_video_player

import android.content.Context
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class SegmentsVideoPlayerViewFactory(private val playerView: SegmentsVideoPlayerView) : PlatformViewFactory(null) {

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return playerView
    }
}
