package com.example.media3app

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession.ControllerInfo

open class MediaLibraryService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null
    private var player: Player? = null

    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        releaseSessionAndPlayer()
        super.onDestroy()
    }

    protected open fun createMediaLibrarySessionCallback(): MediaLibrarySession.Callback {
        return MediaLibrarySessionCallback(this)
    }

    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(
            this, player!!, createMediaLibrarySessionCallback()
        ).build()
    }

    private fun releaseSessionAndPlayer() {
        mediaLibrarySession?.release()
        player?.release()
    }

}