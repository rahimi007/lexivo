package com.example.lexicon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.lexicon.data.WordEntity
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: LexiconViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val allWords by viewModel.allWords.collectAsState()
    var selectedWord by remember { mutableStateOf<WordEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val searchResults = remember(searchQuery, allWords) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val query = searchQuery.lowercase().trim()
            allWords.filter { word ->
                word.word.lowercase().contains(query) || 
                (word.shortDefinition?.lowercase()?.contains(query) == true)
            }
        }
    }
    
    val matchingSections = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val query = searchQuery.lowercase().trim()
            val allSections = listOf(
                "Discover English" to R.drawable.discover_english_1787666799667,
                "Movies & TV" to R.drawable.movies_tv_1787666820963,
                "Everyday English" to R.drawable.everyday_english_1787666843714,
                "Slang & Informal" to R.drawable.slang_informal_1787666859078,
                "Phrases & Idioms" to R.drawable.phrases_idioms_1787666878373,
                "Common Words" to R.drawable.common_words_1787666917434
            )
            allSections.filter { it.first.lowercase().contains(query) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search words and phrases...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (searchQuery.isNotBlank() && searchResults.isEmpty() && matchingSections.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "No results found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else if (searchResults.isNotEmpty() || matchingSections.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                
                val discoverEnglish = matchingSections.find { it.first == "Discover English" }
                val others = matchingSections.filter { it.first != "Discover English" }
                
                if (discoverEnglish != null) {
                    item {
                        RetroCard(
                            onClick = { /* open discover */ },
                            modifier = Modifier.fillMaxWidth().height(160.dp).padding(vertical = 8.dp),
                            color = Color(0xFF93C1C1)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("Discover English", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Explore useful words, phrases, slang, idioms, and everyday English.", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.discover_english_1787666799667),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                                )
                            }
                        }
                    }
                }
                
                val rows = others.chunked(2)
                items(rows) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DiscoverCard(
                            title = rowItems[0].first,
                            imageRes = rowItems[0].second,
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        )
                        if (rowItems.size > 1) {
                            DiscoverCard(
                                title = rowItems[1].first,
                                imageRes = rowItems[1].second,
                                onClick = { },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                
                if (matchingSections.isNotEmpty() && searchResults.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Vocabulary Words", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                items(searchResults) { word ->
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
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
