package com.example.segmentsvideoplayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.common.EventChannel

class SegmentsVideoPlayerView(private val context: Context) : PlatformView {
    private val playerView: PlayerView = PlayerView(context)
    private var player: SimpleExoPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var eventSink: EventChannel.EventSink? = null

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(context).build().apply {
            volume = 0f // Set initial volume to 0
        }
        playerView.player = player
        player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    mainHandler.post(updateProgressAction)
                }
            }
        })
    }

    /**
     * Initialize the player with a list of video segments.
     * @param segments List of segments, each segment is a map with keys "path", "start", and "end".
     */
    fun initialize(segments: List<Map<String, Any>>?) {
        if (segments == null || segments.isEmpty()) {
            throw IllegalArgumentException("Segments list is empty or null")
        }

        val dataSourceFactory = DefaultDataSourceFactory(
            context, Util.getUserAgent(context, "segments_video_player"))
        val concatenatedSource = ConcatenatingMediaSource()

        for (segment in segments) {
            val path = segment["path"] as String
            val startSeconds = (segment["start"] as Number).toDouble()
            val endSeconds = (segment["end"] as Number).toDouble()

            val startMs = (startSeconds * 1000).toLong()
            val endMs = (endSeconds * 1000).toLong()

            val mediaSource = ClippingMediaSource(
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(path))),
                startMs, endMs)
            concatenatedSource.addMediaSource(mediaSource)
        }

        player?.setMediaSource(concatenatedSource)
        player?.prepare()
    }

    fun play() {
        player?.play() ?: throw IllegalStateException("Player is not initialized")
    }

    fun pause() {
        player?.pause() ?: throw IllegalStateException("Player is not initialized")
    }

    fun dispose() {
        player?.release()
        player = null
    }

    fun setEventSink(eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
    }

    private val updateProgressAction = object : Runnable {
        override fun run() {
            player?.let {
                eventSink?.success(it.currentPosition)
                mainHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun getView(): View {
        return playerView
    }
}
