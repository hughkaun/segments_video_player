package com.example.segmentsvideoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.common.EventChannel;

public class SegmentsVideoPlayerView implements PlatformView {
    private final PlayerView playerView;
    private SimpleExoPlayer player;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private EventChannel.EventSink eventSink;

    public SegmentsVideoPlayerView(Context context) {
        this.context = context;
        playerView = new PlayerView(context);
        initializePlayer();
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(context).build();
        player.setVolume(0f);
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    mainHandler.post(updateProgressAction);
                }
            }
        });
    }

    /**
     * Initialize the player with a list of video segments.
     * @param segments List of segments, each segment is a map with keys "path", "start", and "end".
     */
    public void initialize(List<Map<String, Object>> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("Segments list is empty or null");
        }

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "segments_video_player"));

        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();
        for (Map<String, Object> segment : segments) {
            String path = (String) segment.get("path");
            double startSeconds = ((Number) segment.get("start")).doubleValue();
            double endSeconds = ((Number) segment.get("end")).doubleValue();

            long startMs = (long) (startSeconds * 1000);
            long endMs = (long) (endSeconds * 1000);

            MediaSource mediaSource = new ClippingMediaSource(
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(Uri.parse(path))),
                    startMs, endMs);
            concatenatedSource.addMediaSource(mediaSource);
        }

        player.setMediaSource(concatenatedSource);
        player.prepare();
    }

    public void play() {
        if (player != null) {
            player.play();
        } else {
            throw new IllegalStateException("Player is not initialized");
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        } else {
            throw new IllegalStateException("Player is not initialized");
        }
    }

    public void dispose() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void setEventSink(EventChannel.EventSink eventSink) {
        this.eventSink = eventSink;
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            if (player != null && eventSink != null) {
                long position = player.getCurrentPosition();
                eventSink.success(position);
                mainHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View getView() {
        return playerView;
    }
}
