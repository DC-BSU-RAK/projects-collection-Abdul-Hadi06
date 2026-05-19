package com.example.musicapp1.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicapp1.ui.theme.MusicApp1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicApp1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicApp()
                }
            }
        }
    }
}

@Composable
fun MusicApp() {
    val navController = rememberNavController()
    val musicViewModel: MusicViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = musicViewModel,
                onPlayerClick = { navController.navigate("player") },
                onFavoriteHeaderClick = { navController.navigate("favorites") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                viewModel = musicViewModel,
                onBackClick = { navController.popBackStack() },
                onSongClick = { navController.navigate("player") }
            )
        }
        composable("settings") {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("player") {
            musicViewModel.currentSong.value?.let { song ->
                PlayerScreen(
                    song = song,
                    isPlaying = musicViewModel.isPlaying.value,
                    currentPosition = musicViewModel.currentPosition.value,
                    duration = musicViewModel.duration.value,
                    onTogglePlay = { musicViewModel.togglePlayPause() },
                    onSeek = { musicViewModel.seekTo(it) },
                    onBackClick = { navController.popBackStack() },
                    onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                    onNextClick = { musicViewModel.skipNext() },
                    onPrevClick = { musicViewModel.skipPrevious() }
                )
            }
        }
    }
}
