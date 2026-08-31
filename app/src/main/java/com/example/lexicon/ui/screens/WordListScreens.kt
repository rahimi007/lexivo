package com.example.lexicon.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.data.LearningStatus
import com.example.lexicon.data.WordEntity
import com.example.lexicon.ui.LexiconViewModel

@Composable
fun FavoritesScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val favoriteWords = remember(words) { words.filter { it.favorite } }
    
    FilteredListScreen(
        title = "Favorites",
        words = favoriteWords,
        emptyMessage = "No favorites yet.",
        navController = navController
    )
}

@Composable
fun LearnedScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val learnedWords = remember(words) { words.filter { it.learningStatus == LearningStatus.MASTERED } }
    
    FilteredListScreen(
        title = "Learned",
        words = learnedWords,
        emptyMessage = "No learned words yet.",
        navController = navController
    )
}

@Composable
fun SavedWordsScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val savedWords = remember(words) { words.filter { it.learningStatus == LearningStatus.MASTERED } }
    
    FilteredListScreen(
        title = "Saved Words",
        words = savedWords,
        emptyMessage = "No saved words yet",
        navController = navController
    )
}

@Composable
fun HistoryScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val historyWords = remember(words) { 
        words.filter { it.lastViewedAt > 0L }.sortedByDescending { it.lastViewedAt }.take(100) 
    }
    
    FilteredListScreen(
        title = "History",
        words = historyWords,
        emptyMessage = "No history yet",
        navController = navController
    )
}

@Composable
fun StrugglingScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val strugglingWords = remember(words) { words.filter { it.incorrectAnswers >= 2 && it.consecutiveCorrectAfterStruggling < 3 } }
    
    FilteredListScreen(
        title = "Struggling",
        words = strugglingWords,
        emptyMessage = "You're doing great!\nNothing needs extra attention right now.",
        navController = navController
    )
}

@Composable
fun NeedsReviewScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    val needsReviewWords = remember(words) { 
        val now = System.currentTimeMillis()
        words.filter { it.nextReviewAt > 0L && it.nextReviewAt <= now } 
    }
    
    FilteredListScreen(
        title = "Needs Review",
        words = needsReviewWords,
        emptyMessage = "You're all caught up!",
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredListScreen(
    title: String,
    words: List<WordEntity>,
    emptyMessage: String,
    navController: NavController
) {
    var selectedWord by remember { mutableStateOf<WordEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (words.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                items(words) { word ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedWord = word }
                            .padding(vertical = 16.dp)
                    ) {
                        Text(word.word, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                        word.shortDefinition?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
    
    if (selectedWord != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedWord = null },
            sheetState = sheetState,
            containerColor = androidx.compose.ui.graphics.Color(0xFFFCFBF8)
        ) {
            WordDetailSheet(word = selectedWord!!)
        }
    }
}
