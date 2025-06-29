package com.example.media3app

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession.ControllerInfo

open class MediaLibraryService : MediaLibraryService() {
    // Class fields ###############################################################################

    // The MediaLibrarySession connected to the Player
    private lateinit var mediaLibrarySession: MediaLibrarySession

    // Overridden methods #########################################################################

    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        mediaLibrarySession.release()
        mediaLibrarySession.player.release()
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    // Class methods #######$$#####################################################################

    protected open fun createMediaLibrarySessionCallback(): MediaLibrarySession.Callback {
        return MediaLibrarySessionCallback(this)
    }

    private fun initializeSessionAndPlayer() {
        // Create a Player instance
        val player = ExoPlayer.Builder(this).build()
        // Create a MediaLibrarySession connected to the Player
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, createMediaLibrarySessionCallback()).build()
    }
}