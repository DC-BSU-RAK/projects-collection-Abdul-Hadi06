package com.example.musicapp1.presentation

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp1.R
import com.example.musicapp1.model.Song
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MusicViewModel = viewModel(),
    onPlayerClick: () -> Unit,
    onFavoriteHeaderClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val songs = viewModel.songs.value
    val currentSong = viewModel.currentSong.value
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var selectedSongForMenu by remember { mutableStateOf<Song?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                
                // App Logo at the top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.intro_pic),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF1DB954), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Good evening",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = onFavoriteHeaderClick) { 
                            Icon(Icons.Default.Favorite, "Favorites", tint = Color.White) 
                        }
                        IconButton(onClick = onSettingsClick) { 
                            Icon(Icons.Default.Settings, "Settings", tint = Color.White) 
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (it.isNotEmpty()) viewModel.searchSongs(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for songs, artists...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1DB954),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))
                QuickGrid(songs.take(6), viewModel)
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Recommended for you", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(songs.shuffled().take(10)) { song ->
                        RecommendedItem(song) { viewModel.onSongSelected(song) }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Explore All Tracks", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(songs) { song ->
                SongRowItem(song, 
                    onClick = { viewModel.onSongSelected(song) },
                    onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onMoreClick = { 
                        selectedSongForMenu = song
                        showSheet = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            item { Spacer(modifier = Modifier.height(110.dp)) }
        }

        if (currentSong != null) {
            SpotifyBottomBar(
                song = currentSong,
                isPlaying = viewModel.isPlaying.value,
                progress = if (viewModel.duration.value > 0) viewModel.currentPosition.value.toFloat() / viewModel.duration.value.toFloat() else 0f,
                onTogglePlay = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clickable { onPlayerClick() }
            )
        }

        if (showSheet && selectedSongForMenu != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF282828),
                contentColor = Color.White
            ) {
                SongMenuContent(
                    song = selectedSongForMenu!!,
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showSheet = false
                        }
                    },
                    onToggleFavorite = { viewModel.toggleFavorite(selectedSongForMenu!!) }
                )
            }
        }
    }
}

@Composable
fun SongMenuContent(song: Song, onClose: () -> Unit, onToggleFavorite: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = song.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = song.artist, color = Color.Gray, fontSize = 14.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        MenuOption(Icons.Default.Favorite, if (song.isFavorite) "Remove from Liked Songs" else "Like", if (song.isFavorite) Color(0xFF1DB954) else Color.White) {
            onToggleFavorite()
            onClose()
        }
        MenuOption(Icons.Default.Add, "Add to Playlist") { Toast.makeText(context, "Added to Playlist", Toast.LENGTH_SHORT).show(); onClose() }
        MenuOption(Icons.Default.Share, "Share Song") { Toast.makeText(context, "Link Copied!", Toast.LENGTH_SHORT).show(); onClose() }
        MenuOption(Icons.Default.Person, "View Artist") { Toast.makeText(context, "Artist Profile", Toast.LENGTH_SHORT).show(); onClose() }
    }
}

@Composable
fun MenuOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, color: Color = Color.White, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun QuickGrid(songs: List<Song>, viewModel: MusicViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        songs.chunked(2).forEach { rowSongs ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowSongs.forEach { song ->
                    Surface(
                        modifier = Modifier.weight(1f).height(56.dp).clickable { viewModel.onSongSelected(song) },
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = song.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.intro_pic),
                                error = painterResource(id = R.drawable.intro_pic)
                            )
                            Text(song.title, Modifier.padding(horizontal = 8.dp), Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedItem(song: Song, onClick: () -> Unit) {
    Column(modifier = Modifier.width(160.dp).clickable { onClick() }) {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(160.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.intro_pic),
            error = painterResource(id = R.drawable.intro_pic)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(text = "Artist • ${song.artist}", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
fun SongRowItem(song: Song, onClick: () -> Unit, onFavoriteClick: () -> Unit, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(54.dp).clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.intro_pic),
            error = painterResource(id = R.drawable.intro_pic)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = if (song.isFavorite) Color(0xFF1DB954) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(song.artist, color = Color.Gray, fontSize = 14.sp, maxLines = 1)
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (song.isFavorite) Color(0xFF1DB954) else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
        }
    }
}

@Composable
fun SpotifyBottomBar(
    song: Song,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF282828),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = song.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.intro_pic),
                    error = painterResource(id = R.drawable.intro_pic)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(song.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.dp).padding(horizontal = 8.dp),
                color = Color.White,
                trackColor = Color.Gray.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
fun FavoritesScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit,
    onSongClick: () -> Unit
) {
    val favoriteSongs = viewModel.songs.value.filter { it.isFavorite }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Text("Liked Songs", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        if (favoriteSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No liked songs yet", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favoriteSongs) { song ->
                    SongRowItem(
                        song = song,
                        onClick = { 
                            viewModel.onSongSelected(song)
                            onSongClick()
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(song) },
                        onMoreClick = { Toast.makeText(context, "Options for ${song.title}", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Text("Settings", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingItem("Audio Quality", "Very High")
            SettingItem("Data Saver", "Off")
            SettingItem("Storage", "Clear Cache")
            SettingItem("Version", "1.0.4-Spotify-Clone")
        }
    }
}

@Composable
fun SettingItem(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title, color = Color.White, fontSize = 16.sp)
        Text(subtitle, color = Color.Gray, fontSize = 14.sp)
    }
}
