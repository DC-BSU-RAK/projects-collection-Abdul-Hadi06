package com.example.fitmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitmatch.ui.theme.FitMatchTheme
import com.example.fitmatch.ui.theme.NeonBlue
import com.example.fitmatch.ui.theme.DeepBlack
import com.example.fitmatch.ui.theme.DarkGray
import com.example.fitmatch.ui.theme.BeigeAccent
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitMatchTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedWeather by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("") }
    var selectedOccasion by remember { mutableStateOf("") }
    
    var resultOutfit by remember { mutableStateOf("") }
    var resultScore by remember { mutableIntStateOf(0) }
    var resultQuote by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }

    val weatherOptions = listOf("Sunny ☀️", "Rainy 🌧️", "Cold ❄️", "Hot 🔥")
    val moodOptions = listOf("Chill 😌", "Confident 😎", "Lazy 😴", "Happy 😊")
    val styleOptions = listOf("Casual 👕", "Streetwear 🧢", "Formal 🤵", "Sporty 🏃")
    val occasionOptions = listOf("College 🎒", "Party 🎉", "Date ❤️", "Gym 💪")

    val fashionQuotes = listOf(
        "Style is a way to say who you are.",
        "Fashion fades, style remains.",
        "Dress like you already succeeded.",
        "Confidence is your best outfit.",
        "Life is too short to wear boring clothes."
    )

    val backgroundBrush = when {
        selectedWeather.contains("Sunny") -> Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFF57C00)))
        selectedWeather.contains("Rainy") -> Brush.verticalGradient(listOf(Color(0xFF64B5F6), Color(0xFF1976D2)))
        selectedWeather.contains("Cold") -> Brush.verticalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
        selectedWeather.contains("Hot") -> Brush.verticalGradient(listOf(Color(0xFFFF8A65), Color(0xFFD84315)))
        else -> Brush.verticalGradient(listOf(DeepBlack, DarkGray))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_fit_match_logo),
                        contentDescription = "FitMatch Logo",
                        modifier = Modifier.size(48.dp).padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "FitMatch",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Find your perfect outfit vibe",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                IconButton(onClick = { showInstructions = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Instructions", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result Display Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGray.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = NeonBlue)
                    } else {
                        AnimatedVisibility(
                            visible = showResult,
                            enter = fadeIn() + expandVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                var displayedOutfit by remember { mutableStateOf("") }
                                LaunchedEffect(resultOutfit) {
                                    displayedOutfit = ""
                                    resultOutfit.forEach { char ->
                                        displayedOutfit += char
                                        delay(30)
                                    }
                                }
                                Text(
                                    text = displayedOutfit,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "⭐ Style Score: $resultScore%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💬 \"$resultQuote\"",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.LightGray
                                )
                            }
                        }
                        
                        if (!showResult && !isGenerating) {
                            Text(
                                text = "Choose your options and\nclick Generate Fit",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selection Grid
            SelectionSection("WEATHER", weatherOptions, selectedWeather) { selectedWeather = it }
            SelectionSection("MOOD", moodOptions, selectedMood) { selectedMood = it }
            SelectionSection("STYLE", styleOptions, selectedStyle) { selectedStyle = it }
            SelectionSection("OCCASION", occasionOptions, selectedOccasion) { selectedOccasion = it }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (selectedWeather.isNotEmpty() && selectedMood.isNotEmpty() && 
                        selectedStyle.isNotEmpty() && selectedOccasion.isNotEmpty()) {
                        isGenerating = true
                        showResult = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonBlue),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Generate Fit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            LaunchedEffect(isGenerating) {
                if (isGenerating) {
                    delay(1500)
                    resultOutfit = generateOutfitText(selectedWeather, selectedMood, selectedStyle, selectedOccasion)
                    resultScore = Random.nextInt(85, 100)
                    resultQuote = fashionQuotes.random()
                    isGenerating = false
                    showResult = true
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    selectedWeather = ""
                    selectedMood = ""
                    selectedStyle = ""
                    selectedOccasion = ""
                    showResult = false
                }
            ) {
                Text("Clear All", color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "“Confidence is your best outfit.”",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }

    if (showInstructions) {
        InstructionsDialog { showInstructions = false }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectionSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NeonBlue,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selected == option
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) NeonBlue else DarkGray,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) NeonBlue else Color.Gray.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkGray)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "About FitMatch",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "FitMatch is a creative fashion calculator that generates outfit ideas using mood, weather, style, and occasion selections.",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "How to Use",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BeigeAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Choose weather\n2. Choose mood\n3. Choose style\n4. Choose occasion\n5. Click Generate Fit",
                    textAlign = TextAlign.Center,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Credits",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = "Created by: FitMatch Team",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    Text("Got it!", color = Color.Black)
                }
            }
        }
    }
}

fun generateOutfitText(weather: String, mood: String, style: String, occasion: String): String {
    val top = when {
        weather.contains("Cold") -> "❄️ Heavy Wool Coat"
        weather.contains("Rainy") -> "🌧️ Oversized Trench Coat"
        mood.contains("Lazy") -> "👕 Soft Oversized Sweatshirt"
        style.contains("Streetwear") -> "👕 Graphic Hoodie"
        style.contains("Formal") -> "🤵 Slim-fit Blazer"
        style.contains("Sporty") -> "🏃 Technical Quarter-zip"
        else -> "👕 Relaxed Linen Shirt"
    }

    val bottom = when {
        mood.contains("Chill") -> "👖 Loose Lounge Pants"
        style.contains("Streetwear") -> "👖 Baggy Cargo Pants"
        style.contains("Formal") -> "👖 Tailored Trousers"
        style.contains("Sporty") -> "🩳 High-performance Joggers"
        occasion.contains("College") -> "👖 Classic Blue Jeans"
        else -> "👖 Neutral Chinos"
    }

    val shoes = when {
        mood.contains("Confident") -> "👟 Bold Designer Sneakers"
        occasion.contains("Gym") || style.contains("Sporty") -> "👟 Tech Sneakers"
        style.contains("Formal") -> "👞 Polished Loafers"
        weather.contains("Rainy") -> "🥾 Waterproof Chelsea Boots"
        else -> "👟 Minimalist White Sneakers"
    }

    return "$top\n$bottom\n$shoes"
}
