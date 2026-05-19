package com.example.musicapp1.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicapp1.R
import com.example.musicapp1.model.Song
import java.util.Locale

@Composable
fun PlayerScreen(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.bg),
            error = painterResource(id = R.drawable.bg)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.98f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Text(text = "Now Playing", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(10.dp),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                AsyncImage(
                    model = song.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.intro_pic),
                    error = painterResource(id = R.drawable.intro_pic)
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = song.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = song.artist, color = Color.Gray, fontSize = 18.sp, maxLines = 1)
                }
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) Color(0xFF1DB954) else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Slider(
                value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                onValueChange = { onSeek((it * duration).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatTime(currentPosition), color = Color.Gray, fontSize = 12.sp)
                Text(text = formatTime(duration), color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Shuffle, contentDescription = "Shuffle", tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = onPrevClick) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(45.dp))
                }
                
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(80.dp).clickable { onTogglePlay() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }

                IconButton(onClick = onNextClick) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(45.dp))
                }
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Repeat, contentDescription = "Repeat", tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
            }
            
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
