package com.example.lexicon.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.data.WordEntity
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController, viewModel: LexiconViewModel) {
    val words by viewModel.allWords.collectAsState()
    var wordToDelete by remember { mutableStateOf<WordEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library", style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            items(words) { word ->
                WordListItem(
                    word = word,
                    onLongClick = { wordToDelete = word }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
        
        wordToDelete?.let { word ->
            AlertDialog(
                onDismissRequest = { wordToDelete = null },
                title = { Text("Delete Word") },
                text = { Text("Are you sure you want to delete '${word.word}' from your vocabulary?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteWord(word)
                            wordToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { wordToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordListItem(word: WordEntity, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { },
                onLongClick = onLongClick
            )
            .padding(vertical = 16.dp)
    ) {
        Text(word.word, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        word.shortDefinition?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
