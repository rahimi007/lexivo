package com.example.lexicon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.ui.LexiconViewModel
import com.example.lexicon.domain.VocabularyParser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(navController: NavController, viewModel: LexiconViewModel) {
    var text by remember { mutableStateOf("") }
    
    var aiWord by remember { mutableStateOf("") }
    var aiContext by remember { mutableStateOf("") }
    var aiSource by remember { mutableStateOf("") }
    
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    
    var selectedMode by remember { mutableStateOf(0) } // 0 = Copy & Paste, 1 = AI Generate
    val modes = listOf("Copy & Paste", "AI Generate")
    
    // Duplicate word dialog state
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var pendingWordContent by remember { mutableStateOf("") }
    var pendingContext by remember { mutableStateOf<String?>(null) }
    var pendingSource by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Vocabulary", style = MaterialTheme.typography.bodyLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            TabRow(selectedTabIndex = selectedMode) {
                modes.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedMode == index,
                        onClick = { selectedMode = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (selectedMode == 0) {
                // Copy & Paste Mode
                Text("Paste AI Response", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    placeholder = { Text("[001:WORD]\n...") },
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val parsed = VocabularyParser.parse(text)
                        if (parsed != null) {
                            scope.launch {
                                if (viewModel.checkWordExists(parsed.word)) {
                                    pendingWordContent = text
                                    showDuplicateDialog = true
                                } else {
                                    viewModel.addWordFromText(text)
                                    navController.popBackStack()
                                }
                            }
                        } else {
                            resultMessage = "Failed to parse. Make sure the format is correct."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Import")
                }
                
                resultMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                // AI Generate Mode
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(
                        value = aiWord,
                        onValueChange = { aiWord = it },
                        label = { Text("WORD / PHRASE *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = aiContext,
                        onValueChange = { aiContext = it },
                        label = { Text("CONTEXT (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = aiSource,
                        onValueChange = { aiSource = it },
                        label = { Text("SOURCE (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    resultMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (aiWord.isBlank()) return@Button
                        
                        scope.launch {
                            isGenerating = true
                            resultMessage = null
                            val result = viewModel.generateVocabulary(aiWord.trim(), aiContext.trim(), aiSource.trim())
                            isGenerating = false
                            
                            result.fold(
                                onSuccess = { aiResponseText ->
                                    val parsed = VocabularyParser.parse(aiResponseText)
                                    if (parsed != null) {
                                        if (viewModel.checkWordExists(parsed.word)) {
                                            pendingWordContent = aiResponseText
                                            pendingContext = aiContext.trim()
                                            pendingSource = aiSource.trim()
                                            showDuplicateDialog = true
                                        } else {
                                            viewModel.addWordFromText(aiResponseText, overrideContext = aiContext.trim(), overrideSource = aiSource.trim())
                                            navController.popBackStack()
                                        }
                                    } else {
                                        resultMessage = "AI returned an invalid format. Please try again."
                                    }
                                },
                                onFailure = { error ->
                                    resultMessage = error.message ?: "An error occurred."
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    enabled = aiWord.isNotBlank() && !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Generate")
                    }
                }
            }
        }
        
        if (showDuplicateDialog) {
            AlertDialog(
                onDismissRequest = { showDuplicateDialog = false },
                title = { Text("Word already exists") },
                text = { Text("This word already exists in your library. Do you want to replace it or cancel?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.addWordFromText(pendingWordContent, overrideContext = pendingContext, overrideSource = pendingSource)
                            showDuplicateDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Replace")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDuplicateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
