package com.example.musicapp1.data

import retrofit2.http.GET
import retrofit2.http.Query

data class DeezerResponse(
    val data: List<DeezerTrack>
)

data class DeezerTrack(
    val id: Long,
    val title: String,
    val artist: DeezerArtist,
    val album: DeezerAlbum,
    val preview: String,
    val duration: Int
)

data class DeezerArtist(
    val name: String
)

data class DeezerAlbum(
    val cover_medium: String
)

interface MusicApiService {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): DeezerResponse
}
