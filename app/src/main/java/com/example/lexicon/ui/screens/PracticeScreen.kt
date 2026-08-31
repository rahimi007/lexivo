package com.example.lexicon.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lexicon.data.WordEntity
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(navController: NavController, viewModel: LexiconViewModel) {
    var practiceMode by remember { mutableStateOf<String?>(null) }
    var selectedCollocation by remember { mutableStateOf<String?>(null) }
    
    val allWords by viewModel.allWords.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val savedWords = remember(allWords) { allWords.filter { it.learningStatus == com.example.lexicon.data.LearningStatus.MASTERED } }
    
    if (practiceMode == null) {
        PracticeMenuScreen(
            onBack = { navController.popBackStack() },
            onSelectMode = { mode ->
                practiceMode = mode
            }
        )
    } else if (practiceMode == "collocations" && selectedCollocation == null) {
        CollocationsSelectionScreen(
            allWords = allWords,
            onBack = { practiceMode = null },
            onSelectCollocation = { selectedCollocation = it }
        )
    } else {
        val wordsForPractice = when (practiceMode) {
            "saved" -> savedWords
            "liked" -> favorites
            "collocations" -> allWords.filter { word -> 
                val weekRegex = Regex("Week \\d+")
                val matches = weekRegex.findAll(word.collocations ?: "").map { it.value }.toList()
                matches.contains(selectedCollocation)
            }.shuffled()
            else -> allWords
        }
        
        PracticeQuizContent(
            words = wordsForPractice,
            modeTitle = when (practiceMode) {
                "saved" -> "Saved Practice"
                "liked" -> "Liked Practice"
                "collocations" -> "$selectedCollocation Practice"
                else -> "All Words Practice"
            },
            viewModel = viewModel,
            onBack = { 
                if (practiceMode == "collocations") selectedCollocation = null
                else practiceMode = null 
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeMenuScreen(onBack: () -> Unit, onSelectMode: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice", style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PracticeMenuCard(
                title = "Saved",
                    subtitle = "Practice words you've bookmarked",
                    accentColor = Color(0xFFA5D6A7), // Pastel green
                    illustrationType = "bookmark",
                    onClick = { onSelectMode("saved") }
                )
                
                PracticeMenuCard(
                    title = "Liked",
                    subtitle = "Review your favorite words",
                    accentColor = Color(0xFFF48FB1), // Pastel pink
                    illustrationType = "heart",
                    onClick = { onSelectMode("liked") }
                )
                
                PracticeMenuCard(
                    title = "All Words",
                    subtitle = "General practice session",
                    accentColor = Color(0xFF90CAF9), // Pastel blue
                    illustrationType = "stack",
                    onClick = { onSelectMode("all") }
                )
                
                PracticeMenuCard(
                    title = "Collocations",
                    subtitle = "Practice specific collections",
                    accentColor = Color(0xFFFFD54F), // Pastel yellow
                    illustrationType = "stack",
                    onClick = { onSelectMode("collocations") }
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollocationsSelectionScreen(allWords: List<WordEntity>, onBack: () -> Unit, onSelectCollocation: (String) -> Unit) {
    val weekRegex = Regex("Week \\d+")
    val collocationsMap = remember(allWords) {
        val map = mutableMapOf<String, Int>()
        for (word in allWords) {
            val colls = word.collocations ?: continue
            val matches = weekRegex.findAll(colls)
            for (match in matches) {
                val weekName = match.value
                map[weekName] = (map[weekName] ?: 0) + 1
            }
        }
        map.entries.sortedByDescending { 
            it.key.replace("Week ", "").toIntOrNull() ?: 0 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collocations", style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(collocationsMap) { entry ->
                val name = entry.key
                val count = entry.value
                val wordsInCollocation = allWords.filter { word ->
                    val regex = Regex("Week \\d+")
                    val matches = regex.findAll(word.collocations ?: "").map { it.value }.toList()
                    matches.contains(name)
                }
                
                var expanded by remember { mutableStateOf(false) }

                RetroCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = !expanded }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("$count words", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                        
                        if (expanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            wordsInCollocation.forEach { wordEntity ->
                                Text(
                                    text = "• ${wordEntity.word}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { onSelectCollocation(name) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Practice", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
            if (collocationsMap.isEmpty()) {
                item {
                    Text("No collocations available.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun PracticeMenuCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    illustrationType: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        // Thick lower black shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 6.dp)
                .background(Color.Black, shape = RoundedCornerShape(24.dp))
        )
        // Main Card
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFDFCF9), // off-white
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Illustration Box
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        // Organic background blob
                        val path = Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.1f)
                            quadraticBezierTo(size.width * 0.8f, size.height * 0.05f, size.width * 0.9f, size.height * 0.4f)
                            quadraticBezierTo(size.width * 1.0f, size.height * 0.9f, size.width * 0.5f, size.height * 0.85f)
                            quadraticBezierTo(size.width * 0.1f, size.height * 0.8f, size.width * 0.05f, size.height * 0.5f)
                            close()
                        }
                        drawPath(path, color = accentColor)
                        drawPath(path, color = Color.Black, style = Stroke(width = 3f))
                        
                        // Hand drawn foreground symbol
                        when (illustrationType) {
                            "bookmark" -> {
                                val bmPath = Path().apply {
                                    moveTo(size.width * 0.35f, size.height * 0.25f)
                                    lineTo(size.width * 0.65f, size.height * 0.25f)
                                    lineTo(size.width * 0.65f, size.height * 0.75f)
                                    lineTo(size.width * 0.5f, size.height * 0.6f)
                                    lineTo(size.width * 0.35f, size.height * 0.75f)
                                    close()
                                }
                                drawPath(bmPath, color = Color.White)
                                drawPath(bmPath, color = Color.Black, style = Stroke(width = 4f))
                            }
                            "heart" -> {
                                val hPath = Path().apply {
                                    moveTo(size.width * 0.5f, size.height * 0.35f)
                                    cubicTo(size.width * 0.2f, size.height * 0.1f, size.width * 0.1f, size.height * 0.5f, size.width * 0.5f, size.height * 0.75f)
                                    moveTo(size.width * 0.5f, size.height * 0.35f)
                                    cubicTo(size.width * 0.8f, size.height * 0.1f, size.width * 0.9f, size.height * 0.5f, size.width * 0.5f, size.height * 0.75f)
                                }
                                drawPath(hPath, color = Color.White)
                                drawPath(hPath, color = Color.Black, style = Stroke(width = 4f))
                            }
                            "stack" -> {
                                val sPath = Path().apply {
                                    moveTo(size.width * 0.3f, size.height * 0.4f)
                                    lineTo(size.width * 0.7f, size.height * 0.4f)
                                    lineTo(size.width * 0.7f, size.height * 0.7f)
                                    lineTo(size.width * 0.3f, size.height * 0.7f)
                                    close()
                                }
                                drawPath(sPath, color = Color.White)
                                drawPath(sPath, color = Color.Black, style = Stroke(width = 4f))
                                val sPath2 = Path().apply {
                                    moveTo(size.width * 0.4f, size.height * 0.3f)
                                    lineTo(size.width * 0.8f, size.height * 0.3f)
                                    lineTo(size.width * 0.8f, size.height * 0.6f)
                                }
                                drawPath(sPath2, color = Color.Black, style = Stroke(width = 4f))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.DarkGray
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeQuizContent(
    words: List<WordEntity>,
    modeTitle: String,
    viewModel: LexiconViewModel,
    onBack: () -> Unit
) {
    val quizWords = remember(words) { words.filter { it.quizQuestion != null && it.quizCorrectOption != null } }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(modeTitle, style = MaterialTheme.typography.bodyLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (quizWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text("No words available", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You need to add and learn more words for this practice mode.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (currentIndex >= quizWords.size) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Practice complete!", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBack) {
                        Text("Finish")
                    }
                }
            }
        } else {
            val currentWord = quizWords[currentIndex]
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Definition Match",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentWord.word,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                val originalOptions = listOf(
                    "A" to currentWord.quizOptionA,
                    "B" to currentWord.quizOptionB,
                    "C" to currentWord.quizOptionC,
                    "D" to currentWord.quizOptionD
                ).filter { it.second != null }
                
                val options = remember(currentWord, modeTitle) {
                    if (modeTitle.contains("Practice") && !modeTitle.contains("Saved") && !modeTitle.contains("Liked") && !modeTitle.contains("All Words")) {
                        originalOptions.shuffled()
                    } else {
                        originalOptions
                    }
                }
                
                options.forEach { (label, text) ->
                    val isSelected = selectedOption == label
                    val isCorrect = label == currentWord.quizCorrectOption
                    val showResult = selectedOption != null
                    
                    val containerColor = when {
                        showResult && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceContainerLowest
                    }
                    val contentColor = when {
                        showResult && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
                        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    val borderColor = when {
                        showResult && isCorrect -> MaterialTheme.colorScheme.primary
                        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.large,
                        color = containerColor,
                        contentColor = contentColor,
                        border = BorderStroke(1.dp, borderColor),
                        onClick = { 
                            if (selectedOption == null) {
                                selectedOption = label
                                viewModel.recordPracticeResult(currentWord, label == currentWord.quizCorrectOption)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text!!, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            if (showResult && isCorrect) {
                                Icon(Icons.Filled.Check, contentDescription = "Correct", tint = MaterialTheme.colorScheme.primary)
                            } else if (showResult && isSelected && !isCorrect) {
                                Icon(Icons.Filled.Close, contentDescription = "Incorrect", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (selectedOption != null) {
                    Button(
                        onClick = {
                            selectedOption = null
                            currentIndex++
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}
