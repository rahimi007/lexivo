package com.example.lexicon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.data.LearningStatus
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    
    val totalWords = words.size
    val savedWords = words.count { it.learningStatus == LearningStatus.MASTERED }
    val likedWords = words.count { it.favorite }
    val learnedWords = savedWords // Mastered is treated as learned in this app
    
    val correctAnswers = words.sumOf { it.correctAnswers }
    val incorrectAnswers = words.sumOf { it.incorrectAnswers }
    val totalPracticed = correctAnswers + incorrectAnswers
    
    val accuracy = if (totalPracticed > 0) {
        (correctAnswers.toFloat() / totalPracticed.toFloat() * 100).toInt()
    } else 0
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(title = "Total Words", value = totalWords.toString())
            StatCard(title = "Saved Words", value = savedWords.toString())
            StatCard(title = "Liked Words", value = likedWords.toString())
            StatCard(title = "Learned Words", value = learnedWords.toString())
            StatCard(title = "Words Practiced (Total)", value = totalPracticed.toString())
            StatCard(title = "Correct Answers", value = correctAnswers.toString())
            StatCard(title = "Incorrect Answers", value = incorrectAnswers.toString())
            StatCard(title = "Practice Accuracy", value = "$accuracy%")
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
