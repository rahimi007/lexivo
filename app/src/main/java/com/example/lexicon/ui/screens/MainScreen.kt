package com.example.lexicon.ui.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lexicon.ui.LexiconViewModel
import com.example.lexicon.data.WordEntity
import com.example.lexicon.data.LearningStatus

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.ui.theme.literataFont
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: LexiconViewModel) {
    val feedWords by viewModel.feedWords.collectAsState()
    val showPersianPronunciation by viewModel.showPersianPronunciation.collectAsState()
    var selectedWord by remember { mutableStateOf<WordEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 40.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { navController.navigate("library") },
                    shape = CircleShape,
                    color = Color(0xFFF3F3F3),
                    modifier = Modifier.size(48.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Person, contentDescription = "Library", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                val currentWeekLearned by viewModel.currentWeekLearned.collectAsState()
                val weeklyGoal by viewModel.weeklyGoal.collectAsState()
                val progress = if (weeklyGoal > 0) {
                    val p = currentWeekLearned.toFloat() / weeklyGoal
                    if (p > 1f) 1f else p
                } else 0f
                
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 600),
                    label = "progress"
                )
                
                Surface(
                    color = Color(0xFFF3F3F3),
                    shape = CircleShape,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ThinBookmarkOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentWeekLearned/$weeklyGoal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = animatedProgress)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
                
                Surface(
                    onClick = { navController.navigate("settings") },
                    shape = CircleShape,
                    color = Color(0xFFF3F3F3),
                    modifier = Modifier.size(48.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { navController.navigate("explore") },
                    shape = CircleShape,
                    color = Color(0xFFF3F3F3),
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.GridView, contentDescription = "Explore", tint = Color.Black)
                    }
                }
                
                Surface(
                    onClick = { navController.navigate("practice") },
                    shape = CircleShape,
                    color = Color(0xFFF3F3F3),
                    shadowElevation = 4.dp,
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Filled.School, contentDescription = "Practice", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Practice", color = Color.Black, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Surface(
                    onClick = { navController.navigate("add_word") },
                    shape = CircleShape,
                    color = Color(0xFFF3F3F3),
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Word", tint = Color.Black)
                    }
                }
            }
        }
    ) { padding ->
        if (feedWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    "Your vocabulary is empty.\nAdd your first word to start learning.", 
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { feedWords.size })
            
            LaunchedEffect(pagerState.currentPage, feedWords.size) {
                if (feedWords.isNotEmpty() && pagerState.currentPage < feedWords.size) {
                    viewModel.markAsViewed(feedWords[pagerState.currentPage])
                }
            }
            
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding
            ) { page ->
                WordCard(
                    word = feedWords[page],
                    showPersianPronunciation = showPersianPronunciation,
                    onMoreClick = { selectedWord = feedWords[page] },
                    onShareClick = {
                        val word = feedWords[page]
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "${word.word}\n\n${word.shortDefinition ?: ""}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    onFavoriteClick = { viewModel.toggleFavorite(feedWords[page]) },
                    onLearnedClick = { viewModel.markAsLearned(feedWords[page]) }
                )
            }
        }
    }
    
    if (selectedWord != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedWord = null },
            sheetState = sheetState,
            containerColor = Color(0xFFFCFBF8)
        ) {
            WordDetailSheet(word = selectedWord!!)
        }
    }
}

@Composable
fun WordCard(
    word: WordEntity, 
    showPersianPronunciation: Boolean = true,
    onMoreClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onLearnedClick: () -> Unit
) {
    var showSaveAnimation by remember { mutableStateOf(false) }
    var previousStatus by remember { mutableStateOf(word.learningStatus) }
    var showLikeAnimation by remember { mutableStateOf(false) }
    var previousFavorite by remember { mutableStateOf(word.favorite) }

    LaunchedEffect(word.learningStatus) {
        if (previousStatus != LearningStatus.MASTERED && word.learningStatus == LearningStatus.MASTERED) {
            showSaveAnimation = true
            kotlinx.coroutines.delay(1000)
            showSaveAnimation = false
        } else {
            showSaveAnimation = false
        }
        previousStatus = word.learningStatus
    }

    LaunchedEffect(word.favorite) {
        if (!previousFavorite && word.favorite) {
            showLikeAnimation = true
            kotlinx.coroutines.delay(1000)
            showLikeAnimation = false
        } else {
            showLikeAnimation = false
        }
        previousFavorite = word.favorite
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 160.dp), // Shift content slightly lower by changing padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        val wordCount = word.word.trim().split("\\s+".toRegex()).size
        var textSize by remember(word.word) { mutableFloatStateOf(46f) }
        
        Text(
            text = word.word,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = textSize.sp),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = if (wordCount <= 3) 1 else Int.MAX_VALUE,
            onTextLayout = { textLayoutResult ->
                if (wordCount <= 3 && (textLayoutResult.hasVisualOverflow || textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight)) {
                    if (textSize > 12f) { // Prevent shrinking indefinitely
                        textSize *= 0.9f
                    }
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        word.pronunciation?.let {
            Surface(
                color = Color(0xFFF3F3F3),
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    val displayPronunciation = if (showPersianPronunciation) {
                        it.replace("\r\n", "  ").replace("\n", "  ")
                    } else {
                        it.substringBefore("\n").substringBefore("\r")
                    }
                    Text(
                        text = displayPronunciation,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = "Pronounce",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        word.shortDefinition?.let {
            Text(
                text = it,
                fontFamily = literataFont,
                style = MaterialTheme.typography.bodySmall, // Smaller definition
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(170.dp)) // Push action row lower on the screen
        
        // Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMoreClick, modifier = Modifier.size(56.dp)) {
                Icon(ThinInfo, contentDescription = "Info", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = onShareClick, modifier = Modifier.size(56.dp)) {
                Icon(ThinShare, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(56.dp)) {
                if (word.favorite) {
                    Icon(ThinFavoriteFilled, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                } else {
                    Icon(ThinFavoriteOutline, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            IconButton(onClick = onLearnedClick, modifier = Modifier.size(56.dp)) {
                if (word.learningStatus == LearningStatus.MASTERED) {
                    Icon(ThinBookmarkFilled, contentDescription = "Saved", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                } else {
                    Icon(ThinBookmarkOutline, contentDescription = "Save", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
    
    AnimatedVisibility(
        visible = showSaveAnimation,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.7f, animationSpec = tween(350)),
        exit = fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 0.7f, animationSpec = tween(250)),
        modifier = Modifier.padding(bottom = 120.dp)
    ) {
        Icon(
            imageVector = ThinBookmarkFilled,
            contentDescription = "Saved confirmation",
            modifier = Modifier.size(120.dp),
            tint = Color.Black
        )
    }
    AnimatedVisibility(
        visible = showLikeAnimation,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.7f, animationSpec = tween(350)),
        exit = fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 0.7f, animationSpec = tween(250)),
        modifier = Modifier.padding(bottom = 120.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Liked confirmation",
            modifier = Modifier.size(120.dp),
            tint = Color.Black
        )
    }
    }
}

@Composable
fun WordDetailSheet(word: WordEntity) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(word.word, style = MaterialTheme.typography.displayMedium, fontFamily = literataFont)
            CommonnessIndicator(score = word.commonness)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        // Extract synonyms and antonyms
        val related = word.relatedWords ?: ""
        var currentCategory = "SYNONYMS"
        val synLines = mutableListOf<String>()
        val antLines = mutableListOf<String>()
        
        for (line in related.lines()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            
            val upper = trimmed.uppercase()
            if (upper.matches(Regex("^[A-Z\\-]+\\s*:.*"))) {
                if (upper.startsWith("ANTONYMS")) {
                    currentCategory = "ANTONYMS"
                } else {
                    currentCategory = "SYNONYMS"
                }
            }
            
            if (currentCategory == "ANTONYMS") {
                antLines.add(line)
            } else {
                synLines.add(line)
            }
        }
        
        val formatList = { lines: List<String> ->
            if (lines.isEmpty()) null
            else lines.filter { it.trim().isNotEmpty() }.joinToString("\n\n").replace(Regex("([A-Z\\-]+:)\\n\\n"), "$1\n")
        }
        
        val synonymsFinal = formatList(synLines)
        val antonymsFinal = formatList(antLines)

        CollapsibleSection(title = "Translations", content = word.translations, initiallyExpanded = true)
        CollapsibleSection(title = "Examples", content = word.examples)
        CollapsibleSection(title = "Synonyms", content = synonymsFinal)
        CollapsibleSection(title = "Antonyms", content = antonymsFinal)
        CollapsibleSection(title = "Part of Speech", content = word.partOfSpeech)
        CollapsibleSection(title = "Collocations", content = word.collocations)
        CollapsibleSection(title = "Usage", content = word.usage)
        CollapsibleSection(title = "Register", content = word.register)
        CollapsibleSection(title = "Learner Note", content = word.learnerNote)
        CollapsibleSection(title = "Context", content = word.context)
        CollapsibleSection(title = "Source", content = word.source)
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CommonnessIndicator(score: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val filled = i <= score
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (filled) Color(0xFF93C1C1) else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF93C1C1),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    content: String?,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val displayContent = if (content.isNullOrBlank()) "N/A" else content
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { expanded = !expanded }
            .padding(vertical = 12.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, fontFamily = literataFont, color = Color(0xFF000000))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF93C1C1)
            )
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp, bottom = if (expanded) 12.dp else 0.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        
        if (expanded) {
            Text(
                text = displayContent,
                fontFamily = literataFont,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
