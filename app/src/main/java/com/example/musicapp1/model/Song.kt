package com.example.musicapp1.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val songUrl: String,
    val duration: String,
    val isFavorite: Boolean = false
)
