package com.example.segmentsvideoplayer

import android.content.Context
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class SegmentsVideoPlayerViewFactory(private val context: Context) : PlatformViewFactory(null) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return SegmentsVideoPlayerView(context)
    }
}

