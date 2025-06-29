package com.example.media3app

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.core.net.toUri

object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var isInitialized = false
    private const val MEDIA_ID = "[my_media_item]"

    private data class MediaItemNode(val mediaItem: MediaItem)

    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        val sourceUri =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4".toUri()
        treeNodes[MEDIA_ID] = MediaItemNode(
            buildMediaItem(
                title = "my_title",
                mediaId = MEDIA_ID,
                isPlayable = true,
                isBrowsable = false,
                mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                sourceUri = sourceUri,
            )
        )
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        isBrowsable: Boolean,
        mediaType: @MediaMetadata.MediaType Int,
        subtitleConfigurations: List<SubtitleConfiguration> = mutableListOf(),
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder().setAlbumTitle(album).setTitle(title).setArtist(artist)
                .setGenre(genre).setIsBrowsable(isBrowsable).setIsPlayable(isPlayable)
                .setArtworkUri(imageUri).setMediaType(mediaType).build()

        return MediaItem.Builder().setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations).setMediaMetadata(metadata)
            .setUri(sourceUri).build()
    }


    fun getRootItem(): MediaItem {
        if (!isInitialized) {
            initialize()
        }
        return treeNodes[MEDIA_ID]!!.mediaItem
    }

    fun getMediaItem(id: String): MediaItem? {
        return treeNodes[id]?.mediaItem
    }

}