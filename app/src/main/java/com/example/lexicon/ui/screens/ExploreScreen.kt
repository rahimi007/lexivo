package com.example.lexicon.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.lexicon.data.WordEntity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.lexicon.ui.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController, viewModel: LexiconViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore", style = MaterialTheme.typography.displayMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEFEBE0)
                )
            )
        },
        containerColor = Color(0xFFEFEBE0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Top Feature Card
            RetroCard(
                onClick = { /* open discover */ },
                modifier = Modifier.fillMaxWidth().height(160.dp),
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

            // Quick Access Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickAccessCard(
                    title = "Favorites",
                    icon = Icons.Outlined.FavoriteBorder,
                    onClick = { navController.navigate("favorites") },
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    title = "Saved",
                    icon = Icons.Outlined.BookmarkBorder,
                    onClick = { navController.navigate("saved_words") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickAccessCard(
                    title = "Your Own Words",
                    icon = Icons.Outlined.Edit,
                    onClick = { navController.navigate("add_word") },
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    title = "History",
                    icon = Icons.Outlined.History,
                    onClick = { navController.navigate("history") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Discover Section
            Column {
                SectionHeader("Discover")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DiscoverCard(
                        title = "Movies & TV",
                        imageRes = R.drawable.movies_tv_1787666820963,
                        onClick = { /* navigate */ },
                        modifier = Modifier.weight(1f)
                    )
                    DiscoverCard(
                        title = "Everyday English",
                        imageRes = R.drawable.everyday_english_1787666843714,
                        onClick = { /* navigate */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DiscoverCard(
                        title = "Slang & Informal",
                        imageRes = R.drawable.slang_informal_1787666859078,
                        onClick = { /* navigate */ },
                        modifier = Modifier.weight(1f)
                    )
                    DiscoverCard(
                        title = "Phrases & Idioms",
                        imageRes = R.drawable.phrases_idioms_1787666878373,
                        onClick = { /* navigate */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DiscoverCard(
                        title = "Common Words",
                        imageRes = R.drawable.common_words_1787666917434,
                        onClick = { /* navigate */ },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Learning Section
            Column {
                SectionHeader("Keep Learning")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuickAccessCard(
                        title = "Struggling",
                        icon = Icons.Outlined.WarningAmber,
                        onClick = { navController.navigate("struggling") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessCard(
                        title = "Needs Review",
                        icon = Icons.Outlined.Autorenew,
                        onClick = { navController.navigate("needs_review") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuickAccessCard(
                        title = "Daily Challenge",
                        icon = Icons.Outlined.StarBorder,
                        onClick = { navController.navigate("daily_challenge") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Your Progress
            Column {
                SectionHeader("Your Progress")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuickAccessCard(
                        title = "Statistics",
                        icon = Icons.Outlined.BarChart,
                        onClick = { navController.navigate("stats") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            // Collections
            val words by viewModel.allWords.collectAsState()
            
            // Extract all weekly collocations. 
            // We look for collocations matching "Week X"
            val weekRegex = Regex("Week \\d+")
            val weeklyCollections = remember(words) {
                val collections = mutableMapOf<String, MutableList<WordEntity>>()
                for (word in words) {
                    val collocations = word.collocations ?: continue
                    val matches = weekRegex.findAll(collocations)
                    for (match in matches) {
                        val weekName = match.value
                        collections.getOrPut(weekName) { mutableListOf() }.add(word)
                    }
                }
                // Sort by week number descending
                collections.entries.sortedByDescending { 
                    it.key.replace("Week ", "").toIntOrNull() ?: 0 
                }
            }

            Column {
                SectionHeader("Collections")
                
                if (weeklyCollections.isEmpty()) {
                    RetroCard(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No collections yet", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                    }
                } else {
                    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
                    
                    if (showDeleteDialog != null) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = null },
                            title = { Text("Delete Collocation") },
                            text = { Text("Are you sure you want to delete the collection '${showDeleteDialog}'? The vocabulary words inside it will not be deleted.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.deleteCollocation(showDeleteDialog!!)
                                        showDeleteDialog = null
                                    }
                                ) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = null }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    weeklyCollections.forEach { (weekName, weekWords) ->
                        var expanded by remember { mutableStateOf(false) }
                        
                        RetroCard(
                            onClick = { expanded = !expanded },
                            onLongClick = { showDeleteDialog = weekName },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(), 
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = weekName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${weekWords.size} words",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                
                                if (expanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    weekWords.forEach { word ->
                                        Text(
                                            text = "• ${word.word}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RetroCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    color: Color = Color(0xFFFDFCF9),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black, shape = RoundedCornerShape(24.dp))
        )
        // Main Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = color,
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (onLongClick != null) {
                            Modifier.combinedClickable(
                                onClick = onClick,
                                onLongClick = onLongClick
                            )
                        } else {
                            Modifier.clickable(onClick = onClick)
                        }
                    ),
                content = content
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RetroCard(
        onClick = onClick,
        modifier = modifier.height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DiscoverCard(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RetroCard(
        onClick = onClick,
        modifier = modifier.height(180.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
