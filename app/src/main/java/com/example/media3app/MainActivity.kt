package com.example.media3app

import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.ListenableFuture


class MainActivity : AppCompatActivity() {
    // Class fields ###############################################################################

    // A future that represents the connection to the MediaLibraryService.
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>

    // The MediaBrowser instance.
    private val browser: MediaBrowser?
        // A custom getter function that returns the MediaBrowser instance if the future is done and not cancelled.
        // Otherwise, return null.
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    // PlayerView object to display the video
    private lateinit var playerView: PlayerView

    // ExoPlayer object to play the video
    private lateinit var player: ExoPlayer

    // Fields to store the state of the player
    private var playWhenReady = true // Start playing the video as soon as it's ready
    private var itemIndexToPlay = 0 // Start playing the first item in the list
    private var playbackPosition = 0L // Start from the beginning of the video

    // Overridden methods #########################################################################

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()
        initializeBrowser()
    }

    override fun onStop() {
        releaseBrowser()
        super.onStop()
    }

    // Class methods #######$$#####################################################################

    // Initialize the MediaBrowser instance to connect to the MediaLibraryService.
    private fun initializeBrowser() {
        // Create a MediaBrowser instance to connect to the MediaLibraryService.
        val componentName = ComponentName(this, MediaLibraryService::class.java)
        val sessionToken = SessionToken(this, componentName)
        browserFuture = MediaBrowser.Builder(
            this, sessionToken
        ).buildAsync()

        // Add a listener to the browserFuture. This listener will be called when the future is done.
        browserFuture.addListener({ browseMediaLibrary() }, ContextCompat.getMainExecutor(this))
    }

    // Release the MediaBrowser instance to disconnect from the MediaLibraryService.
    private fun releaseBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }

    // Browse the media library on the MediaLibraryService.
    private fun browseMediaLibrary() {
        val browser = this.browser ?: return
        val mediaLibraryRootFuture = browser.getLibraryRoot(/* params= */ null)
        mediaLibraryRootFuture.addListener(
            {
                val result: LibraryResult<MediaItem> = mediaLibraryRootFuture.get()!!
                val mediaLibraryRoot: MediaItem = result.value!!
                playMedia(mediaLibraryRoot)
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun playMedia(mediaItem: MediaItem) {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItemFromLibrary = MediaItemTree.getMediaItem(mediaItem.mediaId)!!
            exoPlayer.setMediaItems(
                listOf(mediaItemFromLibrary), itemIndexToPlay, playbackPosition
            )
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.prepare()
        }
    }

}

