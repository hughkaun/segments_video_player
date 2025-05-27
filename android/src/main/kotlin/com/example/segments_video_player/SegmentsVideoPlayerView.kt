package com.example.segments_video_player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.ui.PlayerView
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.common.EventChannel
import java.io.File

class SegmentsVideoPlayerView(private val context: Context) : PlatformView {
    private val playerView: PlayerView = PlayerView(context)
    private var player: ExoPlayer? = null
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var eventSink: EventChannel.EventSink? = null

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0f
            playerView.player = this
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY && playWhenReady) {
                        mainHandler.post(updateProgressAction)
                    }
                }
            })
        }
    }

    @OptIn(UnstableApi::class)
    fun initialize(segments: List<Map<String, Any>>) {
        if (segments.isEmpty()) {
            throw IllegalArgumentException("Segments list is empty or null")
        }

        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSources = mutableListOf<MediaSource>()

        for (segment in segments) {
            val path = segment["path"] as? String ?: throw IllegalArgumentException("Path is missing")
            val startSeconds = (segment["start"] as? Number)?.toDouble() ?: throw IllegalArgumentException("Start time is missing")
            val endSeconds = (segment["end"] as? Number)?.toDouble() ?: throw IllegalArgumentException("End time is missing")

            val startMs = (1000 * 1000).toLong()
            val endMs = (5000 * 1000).toLong()

            val uri = Uri.fromFile(File(path))
            val mediaSource = ClippingMediaSource(
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri)),
                startMs, endMs
            )
            mediaSources.add(mediaSource)
        }

        val concatenatedSource = ConcatenatingMediaSource(*mediaSources.toTypedArray())
        player?.setMediaSource(concatenatedSource)
        player?.prepare()
        player?.play()
    }

    fun play() {
        player?.play() ?: throw IllegalStateException("Player is not initialized")
    }

    fun pause() {
        player?.pause() ?: throw IllegalStateException("Player is not initialized")
    }

    override fun dispose() {
        player?.release()
        player = null
    }

    fun setEventSink(eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
    }

    private val updateProgressAction: Runnable = object : Runnable {
        override fun run() {
            player?.let {
                val position: Long = it.currentPosition
                eventSink?.success(position)
                mainHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun getView(): View {
        return playerView
    }
}
