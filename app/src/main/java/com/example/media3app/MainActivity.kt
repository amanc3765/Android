package com.example.media3app

import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.ListenableFuture


class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone && !controllerFuture.isCancelled) controllerFuture.get() else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()
        initializeBrowser()
        initializeController()
    }

    override fun onStop() {
        releaseController()
        releaseBrowser()
        super.onStop()
    }

    private fun initializeBrowser() {
        val sessionToken = SessionToken(this, ComponentName(this, MediaLibraryService::class.java))
        browserFuture = MediaBrowser.Builder(this, sessionToken).buildAsync()
        browserFuture.addListener(
            {
                if (this.browser != null) browseMediaLibraryRoot()
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun initializeController() {
        val sessionToken = SessionToken(this, ComponentName(this, MediaLibraryService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                if (this.controller != null) playerView.player = this.controller
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun releaseBrowser() {
        if (this::browserFuture.isInitialized) {
            MediaBrowser.releaseFuture(browserFuture)
        }
    }

    private fun releaseController() {
        playerView.player = null
        if (this::controllerFuture.isInitialized) {
            MediaController.releaseFuture(controllerFuture)
        }
    }

    private fun browseMediaLibraryRoot() {
        val currentBrowser = this.browser ?: run {
            return
        }
        val libraryRootFuture: ListenableFuture<LibraryResult<MediaItem>> =
            currentBrowser.getLibraryRoot(null)
        libraryRootFuture.addListener(
            {
                val result: LibraryResult<MediaItem>? = libraryRootFuture.get()
                val rootItem: MediaItem = result?.value!!

                val treeMediaItem: MediaItem = MediaItemTree.getMediaItem(rootItem.mediaId)!!
                if (treeMediaItem.mediaMetadata.isPlayable == true && treeMediaItem.localConfiguration?.uri != null) {
                    playMediaItem(treeMediaItem)
                }
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun playMediaItem(mediaItem: MediaItem) {
        val currentController = this.controller ?: return
        currentController.setMediaItem(mediaItem)
        currentController.prepare()
        currentController.play()
    }
}