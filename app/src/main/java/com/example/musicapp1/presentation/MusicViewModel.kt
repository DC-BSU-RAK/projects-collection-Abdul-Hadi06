package com.example.musicapp1.presentation

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp1.model.Song
import com.example.musicapp1.data.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import java.util.Locale

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val exoPlayer = ExoPlayer.Builder(application).build()

    private val _songs = mutableStateOf<List<Song>>(emptyList())
    val songs: State<List<Song>> = _songs

    private val _currentSong = mutableStateOf<Song?>(null)
    val currentSong: State<Song?> = _currentSong

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableLongStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableLongStateOf(0L)
    val duration: State<Long> = _duration

    init {
        loadInitialSongs()
        setupPlayerListener()
        startProgressUpdate()
    }

    private fun loadInitialSongs() {
        viewModelScope.launch {
            try {
                // Fetch real songs from API
                val response = RetrofitInstance.api.searchTracks("top hits")
                val apiSongs = response.data.map { track ->
                    Song(
                        id = track.id.toInt(),
                        title = track.title,
                        artist = track.artist.name,
                        imageUrl = track.album.cover_medium,
                        songUrl = track.preview,
                        duration = formatDuration(track.duration)
                    )
                }
                
                // Add specific Urdu songs
                val specificSongs = listOf(
                    Song(101, "Tum", "Murtaza", "https://images.unsplash.com/photo-1459749411177-042180ceea72?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "4:15"),
                    Song(102, "Hum", "Murtaza", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:45"),
                    Song(103, "Bhool", "Murtaza", "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "4:02"),
                    Song(104, "Deedar", "Third Hour", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "3:30"),
                    Song(105, "Faraibi", "Third Hour", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "3:55"),
                    Song(106, "Seaside", "Third Hour", "https://images.unsplash.com/photo-1514525253344-f21f8573d98a?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "3:20")
                )
                
                _songs.value = specificSongs + apiSongs
            } catch (e: Exception) {
                // Fallback to mock if API fails
                loadMockSongs()
            }
        }
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds)
    }

    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchTracks(query)
                _songs.value = response.data.map { track ->
                    Song(
                        id = track.id.toInt(),
                        title = track.title,
                        artist = track.artist.name,
                        imageUrl = track.album.cover_medium,
                        songUrl = track.preview,
                        duration = formatDuration(track.duration)
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadMockSongs() {
        val specificSongs = listOf(
            Song(101, "Tum", "Murtaza", "https://images.unsplash.com/photo-1459749411177-042180ceea72?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "4:15"),
            Song(102, "Hum", "Murtaza", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:45"),
            Song(103, "Bhool", "Murtaza", "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "4:02"),
            Song(104, "Deedar", "Third Hour", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "3:30"),
            Song(105, "Faraibi", "Third Hour", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "3:55"),
            Song(106, "Seaside", "Third Hour", "https://images.unsplash.com/photo-1514525253344-f21f8573d98a?w=500", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "3:20")
        )

        val titles = listOf("Midnight City", "Starboy", "Blinding Lights", "Flowers", "As It Was", "Cruel Summer", "Save Your Tears", "Stay", "Levitating", "Peaches", "Heat Waves", "Cold Heart", "Bad Habits", "Shivers", "Enemy", "Believer", "Thunder", "Natural", "Bones", "Demons")
        val artists = listOf("The Weeknd", "Miley Cyrus", "Harry Styles", "Taylor Swift", "Justin Bieber", "Dua Lipa", "Imagine Dragons", "Ed Sheeran", "Glass Animals", "M83")
        
        val songList = specificSongs.toMutableList()
        for (i in 1..50) {
            val title = titles[i % titles.size] + " " + (i / titles.size + 1)
            val artist = artists[i % artists.size]
            val imageUrl = "https://picsum.photos/id/${i + 20}/500/500"
            val songUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${(i % 10) + 7}.mp3"
            val duration = "${(2..4).random()}:${(10..59).random()}"
            
            songList.add(Song(i, title, artist, imageUrl, songUrl, duration))
        }
        _songs.value = songList
    }

    fun toggleFavorite(song: Song) {
        val updatedList = _songs.value.map {
            if (it.id == song.id) it.copy(isFavorite = !it.isFavorite) else it
        }
        _songs.value = updatedList
        if (_currentSong.value?.id == song.id) {
            _currentSong.value = _currentSong.value?.copy(isFavorite = !song.isFavorite)
        }
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    _duration.longValue = exoPlayer.duration.coerceAtLeast(0L)
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    _duration.longValue = exoPlayer.duration
                }
            }
        })
    }

    private fun startProgressUpdate() {
        viewModelScope.launch {
            while (isActive) {
                if (_isPlaying.value) {
                    _currentPosition.longValue = exoPlayer.currentPosition
                }
                delay(1000)
            }
        }
    }

    fun onSongSelected(song: Song) {
        _currentSong.value = song
        val mediaItem = MediaItem.fromUri(song.songUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun skipNext() {
        val currentIndex = _songs.value.indexOfFirst { it.id == _currentSong.value?.id }
        if (currentIndex != -1 && currentIndex < _songs.value.size - 1) {
            onSongSelected(_songs.value[currentIndex + 1])
        } else if (_songs.value.isNotEmpty()) {
            onSongSelected(_songs.value[0])
        }
    }

    fun skipPrevious() {
        val currentIndex = _songs.value.indexOfFirst { it.id == _currentSong.value?.id }
        if (currentIndex > 0) {
            onSongSelected(_songs.value[currentIndex - 1])
        } else if (_songs.value.isNotEmpty()) {
            onSongSelected(_songs.value.last())
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _currentPosition.longValue = position
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
