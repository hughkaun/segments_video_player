package com.example.segmentsvideoplayer;

import android.content.Context;

import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class SegmentsVideoPlayerViewFactory extends PlatformViewFactory {
    private final Context context;

    public SegmentsVideoPlayerViewFactory(Context context) {
        super(null);
        this.context = context;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new SegmentsVideoPlayerView(context);
    }
}
