package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lexicon.LexiconApp
import com.example.lexicon.ui.LexiconViewModel
import com.example.lexicon.ui.screens.AddWordScreen
import com.example.lexicon.ui.screens.ExploreScreen
import com.example.lexicon.ui.screens.FavoritesScreen
import com.example.lexicon.ui.screens.LearnedScreen
import com.example.lexicon.ui.screens.LibraryScreen
import com.example.lexicon.ui.screens.MainScreen
import com.example.lexicon.ui.screens.PracticeScreen
import com.example.lexicon.ui.screens.SettingsScreen
import com.example.lexicon.ui.screens.SavedWordsScreen
import com.example.lexicon.ui.screens.HistoryScreen
import com.example.lexicon.ui.screens.StrugglingScreen
import com.example.lexicon.ui.screens.NeedsReviewScreen
import com.example.lexicon.ui.screens.DailyChallengeScreen
import com.example.lexicon.ui.screens.StatsScreen
import com.example.lexicon.ui.screens.SearchScreen
import com.example.lexicon.ui.screens.PromptEditorScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as LexiconApp
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LexiconViewModel(app.repository, app.settingsRepository) as T
            }
        }
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: LexiconViewModel = viewModel(factory = factory)
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { MainScreen(navController, viewModel) }
                        composable("add_word") { AddWordScreen(navController, viewModel) }
                        composable("library") { LibraryScreen(navController, viewModel) }
                        composable("practice") { PracticeScreen(navController, viewModel) }
                        composable("settings") { SettingsScreen(navController, viewModel) }
                        composable("prompt_editor") { PromptEditorScreen(navController, viewModel) }
                        composable("explore") { ExploreScreen(navController, viewModel) }
                        composable("search") { SearchScreen(navController, viewModel) }
                        composable("favorites") { FavoritesScreen(navController, viewModel) }
                        composable("learned") { LearnedScreen(navController, viewModel) }
                        composable("saved_words") { SavedWordsScreen(navController, viewModel) }
                        composable("history") { HistoryScreen(navController, viewModel) }
                        composable("struggling") { StrugglingScreen(navController, viewModel) }
                        composable("needs_review") { NeedsReviewScreen(navController, viewModel) }
                        composable("daily_challenge") { DailyChallengeScreen(navController, viewModel) }
                        composable("stats") { StatsScreen(navController, viewModel) }
                        composable("help") { com.example.lexicon.ui.screens.HelpScreen(navController) }
                    }
                }
            }
        }
    }
}
