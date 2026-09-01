package com.example.lexicon.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class HelpItem(val title: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val allHelpItems = listOf(
        HelpItem(
            title = "A. GETTING STARTED",
            content = "What the app is for:\nThis app helps you discover, learn, and practice English vocabulary effectively.\n\n" +
                        "How the vocabulary-learning system works:\nWhen you add a word, it is presented on the main screen. You can mark it as 'learned' or 'saved' to track your progress.\n\n" +
                        "How to add a new word:\nTap the '+' button at the bottom of the main screen.\n\n" +
                        "Copy & Paste vs AI:\nYou can manually enter the vocabulary details (Copy & Paste) or let the AI automatically generate the definition, examples, and other details from just the word (AI input)."
        ),
        HelpItem(
            title = "B. ADDING VOCABULARY",
            content = "The Add Vocabulary page has two input methods:\n\n" +
                        "Copy & Paste:\nAllows you to manually enter the word, its meaning, translations, examples, and other details.\n\n" +
                        "AI Input:\nYou just provide the word/phrase. The AI will generate all the details automatically.\n\n" +
                        "Context and Source:\nIn both modes, you can optionally provide context (where you saw the word) and a source to help you remember it.\n\n" +
                        "AI Generation:\nWhen you use AI, the app sends your word and context to the selected AI provider. The provider returns the vocabulary details, which are then saved to your library."
        ),
        HelpItem(
            title = "C. AI / API SETUP",
            content = "To use the AI features, you need to configure an AI provider in Settings:\n\n" +
                        "1. Go to Settings -> AI.\n" +
                        "2. Select your preferred AI Provider (Gemini, OpenAI / ChatGPT, OpenRouter, or GapGPT).\n" +
                        "3. Enter your API key for the selected provider.\n" +
                        "4. Type in the name of the AI model you want to use.\n\n" +
                        "Your API key is stored locally on your device and is used solely to authenticate your requests to the AI provider. The chosen provider generates the vocabulary information for you."
        ),
        HelpItem(
            title = "D. AI GENERATION PROMPT",
            content = "The AI Prompt setting contains the instructions sent to the AI when generating vocabulary. \n\n" +
                        "You can edit this prompt to customize how the AI formats its response. The prompt controls the structure and content of the generated vocabulary information.\n\n" +
                        "Important: If you modify the prompt, you must ensure the AI's output remains a valid JSON array of objects that the app's vocabulary parser can understand."
        ),
        HelpItem(
            title = "E. MAIN VOCABULARY SCREEN",
            content = "The main screen displays your vocabulary items one by one:\n\n" +
                        "Word: The English word or phrase.\n" +
                        "English pronunciation: The IPA pronunciation guide.\n" +
                        "Persian pronunciation: A Persian phonetic guide (if enabled in settings).\n" +
                        "Short definition: A brief English explanation.\n\n" +
                        "Controls:\n" +
                        "• Save (Checkmark): Marks the word as learned and counts it towards your weekly goal.\n" +
                        "• Like (Heart): Adds the word to your Favorites.\n" +
                        "• Share: Share the word text with other apps.\n" +
                        "• Info: Opens a detailed view with more information about the word.\n\n" +
                        "Top Bar: Displays your weekly progress and current streak."
        ),
        HelpItem(
            title = "F. SAVE SYSTEM",
            content = "When you tap the Save (checkmark) button, the word is marked as 'learned'.\n\n" +
                        "Saved words count towards your weekly learning goal. The progress bar at the top fills up as you save words. " +
                        "When your goal is reached, the progress bar resets for the next week and a new weekly collocation is created.\n\n" +
                        "Saved words are automatically associated with the current week's collocation (e.g., 'Week 1').\n\n" +
                        "If you unsave a word, it is removed from your learned words and the weekly progress decreases accordingly."
        ),
        HelpItem(
            title = "G. WEEKLY LEARNING GOAL",
            content = "In Settings -> Learning, you can set the number of words you want to learn each week.\n\n" +
                        "The progress bar on the main screen shows how many words you've learned out of this goal. " +
                        "When you reach the target, the progress is completed, and a new week begins automatically."
        ),
        HelpItem(
            title = "H. WORD ORDER",
            content = "The 'Word Order' setting (in Settings -> Learning) determines the order of words shown on the main screen.\n\n" +
                        "• Random: (Default) Words are shown in a random order.\n" +
                        "• Newest first: Recently added words are shown first.\n" +
                        "• Oldest first: Older words are shown first."
        ),
        HelpItem(
            title = "I. INFO PAGE",
            content = "The Info page (accessed by swiping up or tapping 'Info' on the main screen) displays detailed information about the word.\n\n" +
                        "It includes:\n" +
                        "• Translations\n" +
                        "• Examples\n" +
                        "• Part of speech\n" +
                        "• Collocations\n" +
                        "• Usage\n" +
                        "• Synonyms & Antonyms\n" +
                        "• Register (formality)\n" +
                        "• Learner Note\n" +
                        "• Context & Memory\n" +
                        "• Source\n" +
                        "• Short definition\n" +
                        "• Quiz (a short multiple-choice question)\n\n" +
                        "These sections can be expanded and collapsed by tapping on them."
        ),
        HelpItem(
            title = "J. COMMONNESS",
            content = "The commonness system (shown on the Info page) uses a 1-5 scale to indicate how frequently a word is used in modern English.\n\n" +
                        "This is visually represented by five circles (filled or empty). More filled circles mean the word is more common."
        ),
        HelpItem(
            title = "K. EXPLORE",
            content = "The Explore tab helps you find and review your vocabulary:\n\n" +
                        "• Favorites: Words you have 'liked'.\n" +
                        "• Saved: Words you have marked as learned.\n" +
                        "• Your Own Words: Words you added manually (Copy & Paste).\n" +
                        "• History: Recently viewed words.\n" +
                        "• Struggling: Words you got wrong in practice quizzes.\n" +
                        "• Needs Review: Words due for spaced-repetition review.\n" +
                        "• Daily Challenge: A daily set of practice questions.\n" +
                        "• Statistics: Your learning stats and streaks.\n" +
                        "• Discover English: Explore random words.\n" +
                        "• Collocations: Review your weekly saved word sets.\n" +
                        "• Search: Find specific words in your library.\n\n" +
                        "Tapping any section opens a list of words or the relevant feature."
        ),
        HelpItem(
            title = "L. SEARCH",
            content = "The Search feature in the Explore tab lets you find specific words.\n\n" +
                        "When you type in the search bar, the app first shows matching Explore categories (if any), followed by a list of vocabulary items containing your search term."
        ),
        HelpItem(
            title = "M. LIBRARY",
            content = "The Library tab shows a complete list of all vocabulary items you have added.\n\n" +
                        "The words are displayed in a list. You can delete any word by long-pressing on it in the list."
        ),
        HelpItem(
            title = "N. PRACTICE",
            content = "The Practice tab allows you to test your knowledge using multiple-choice quizzes.\n\n" +
                        "Practice modes:\n" +
                        "• Saved: Practice only words you have marked as learned.\n" +
                        "• Liked: Practice words from your Favorites.\n" +
                        "• Collocations: Practice words saved during specific weeks.\n" +
                        "• All Words: Practice your entire library.\n\n" +
                        "The Quiz:\n" +
                        "Each question presents a word and asks you to choose the correct meaning from four English options. " +
                        "The correct answer is determined by the word's definition in your library. " +
                        "For Collocations, you first select a specific week (e.g., Week 1), and the quiz will test you on those words in a randomized order."
        ),
        HelpItem(
            title = "O. COLLOCATIONS",
            content = "A 'collocation' in this app represents a weekly set of saved words.\n\n" +
                        "As you save words and complete your weekly goal, the app groups them into sets like 'Week 1', 'Week 2', etc.\n" +
                        "When you complete a week, a new week starts automatically for subsequent saved words.\n\n" +
                        "These weekly sets appear in the Explore -> Collocations section. You can delete a collocation by long-pressing it. " +
                        "If you unsave a word on the main screen, it is removed from its assigned weekly collocation."
        ),
        HelpItem(
            title = "P. IMPORT / EXPORT",
            content = "You can backup and restore your data in Settings -> Data.\n\n" +
                        "Export: Creates a JSON backup file containing all your vocabulary and settings.\n\n" +
                        "Import: Restores data from a backup file. You have two options:\n" +
                        "• ADD: Keeps your existing vocabulary and adds the backup data to it.\n" +
                        "• REPLACE: Erases your current vocabulary and settings, replacing them entirely with the backup data.\n\n" +
                        "If you cancel the operation, nothing is changed."
        ),
        HelpItem(
            title = "Q. SETTINGS",
            content = "The Settings page lets you configure the app:\n\n" +
                        "Learning:\n" +
                        "• Weekly Learning Goal: Target number of words per week.\n" +
                        "• Word Order: Display order on the main screen.\n" +
                        "• Persian pronunciation: Toggle the Persian phonetic guide.\n\n" +
                        "AI:\n" +
                        "• Default vocabulary input: Choose between Copy & Paste or AI for the '+' button.\n" +
                        "• AI Provider / API Key / AI Model: Configure your AI connection.\n" +
                        "• AI Prompt: Edit the instructions sent to the AI.\n\n" +
                        "Data:\n" +
                        "• Export / Import: Manage backups."
        ),
        HelpItem(
            title = "R. PERSIAN PRONUNCIATION",
            content = "The Persian pronunciation setting controls whether the Persian phonetic guide is shown on the main word screen.\n\n" +
                        "When enabled, it appears below the English IPA. When disabled, only the English IPA remains. Disabling this setting only hides the text; it does not delete the pronunciation data from your vocabulary."
        ),
        HelpItem(
            title = "S. DEFAULT VOCABULARY INPUT",
            content = "This setting determines which tab opens first when you press the '+' button to add a word.\n\n" +
                        "• Copy & Paste: Opens the manual entry form.\n" +
                        "• AI: Opens the AI generation form.\n\n" +
                        "Regardless of your choice, you can always switch between the two tabs after the page opens."
        ),
        HelpItem(
            title = "T. SAVED / LIKED WORDS",
            content = "• Saved words: Words you have marked as learned (using the checkmark). They count towards your weekly progress.\n" +
                        "• Liked words: Words you have favorited (using the heart). They are added to your Favorites list.\n\n" +
                        "Both categories can be found in the Explore tab and can be used as sources for Practice quizzes."
        ),
        HelpItem(
            title = "U. DAILY CHALLENGE / NEEDS REVIEW / STRUGGLING",
            content = "These are special review categories found in the Explore tab:\n\n" +
                        "• Struggling: Words you have frequently answered incorrectly during practice.\n" +
                        "• Needs Review: Words that the app determines you should review based on when you last saw them.\n" +
                        "• Daily Challenge: A daily curated quiz of words selected for review."
        ),
        HelpItem(
            title = "V. BACKUP SAFETY",
            content = "Warning: When using the REPLACE import option, all your current data will be erased. Always ensure you have safely stored your backup files before replacing your data."
        ),
    )

    val filteredItems = if (searchQuery.isBlank()) {
        allHelpItems
    } else {
        allHelpItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search help...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    } else {
                        Text("Help & Guide", fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isSearchActive) {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (filteredItems.isEmpty()) {
                Text(
                    text = "No results found for \"${searchQuery}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                filteredItems.forEach { item ->
                    HelpSection(
                        title = item.title,
                        content = item.content,
                        isSearching = searchQuery.isNotBlank()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HelpSection(title: String, content: String, isSearching: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(isSearching) {
        if (isSearching) {
            expanded = true
        } else {
            expanded = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
