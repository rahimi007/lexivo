package com.example.lexicon.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.data.WordEntity
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    
    // Select 5 words based on priority: needs review, struggling, new, random
    val quizWords = remember(words) {
        val available = words.filter { it.quizQuestion != null && it.quizCorrectOption != null }
        val now = System.currentTimeMillis()
        
        val needsReview = available.filter { it.nextReviewAt > 0L && it.nextReviewAt <= now }
        val struggling = available.filter { it.incorrectAnswers >= 2 && it.consecutiveCorrectAfterStruggling < 3 }
        
        val combined = (needsReview + struggling).distinct().toMutableList()
        if (combined.size < 5) {
            val others = available.filter { !combined.contains(it) }.shuffled()
            combined.addAll(others.take(5 - combined.size))
        }
        
        combined.take(5)
    }
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Challenge", style = MaterialTheme.typography.bodyLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (quizWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Not enough words for a challenge yet.", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (currentIndex >= quizWords.size) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Daily Challenge Complete", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("$correctCount / ${quizWords.size} correct", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Done")
                }
            }
        } else {
            val currentWord = quizWords[currentIndex]
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${quizWords.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentWord.word,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                val options = listOf(
                    "A" to currentWord.quizOptionA,
                    "B" to currentWord.quizOptionB,
                    "C" to currentWord.quizOptionC,
                    "D" to currentWord.quizOptionD
                ).filter { it.second != null }
                
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        onClick = { 
                            if (selectedOption == null) {
                                selectedOption = label
                                val correct = label == currentWord.quizCorrectOption
                                if (correct) correctCount++
                                viewModel.recordPracticeResult(currentWord, correct)
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
