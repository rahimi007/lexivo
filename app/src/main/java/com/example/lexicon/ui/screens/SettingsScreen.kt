package com.example.lexicon.ui.screens
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import androidx.compose.ui.graphics.Color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lexicon.ui.LexiconViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: LexiconViewModel) {
    val scope = rememberCoroutineScope()
    val currentGoal by viewModel.weeklyGoal.collectAsState()
    val context = LocalContext.current
    var showImportWarning by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.exportData()
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        OutputStreamWriter(stream).use { writer ->
                            writer.write(json)
                        }
                    }
                    Toast.makeText(context, "Export successful", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = context.contentResolver.openInputStream(it)?.use { stream ->
                        InputStreamReader(stream).use { reader ->
                            reader.readText()
                        }
                    }
                    if (json != null) {
                        val success = viewModel.importData(json)
                        if (success) {
                            Toast.makeText(context, "Import successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Import failed: Invalid backup", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    val aiProvider by viewModel.aiProvider.collectAsState()
    
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val geminiModel by viewModel.geminiModel.collectAsState()
    
    val openaiApiKey by viewModel.openaiApiKey.collectAsState()
    val openaiModel by viewModel.openaiModel.collectAsState()
    
    val openrouterApiKey by viewModel.openrouterApiKey.collectAsState()
    val openrouterModel by viewModel.openrouterModel.collectAsState()
    
    val gapgptApiKey by viewModel.gapgptApiKey.collectAsState()
    val gapgptModel by viewModel.gapgptModel.collectAsState()
    
    val homeWordOrder by viewModel.homeWordOrder.collectAsState()
    
    var showCustomDialog by remember { mutableStateOf(false) }
    var customGoalInput by remember { mutableStateOf("") }
    
    var weeklyGoalExpanded by remember { mutableStateOf(false) }
    var homeWordOrderExpanded by remember { mutableStateOf(false) }
    var aiExpanded by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }

    val options = listOf(5, 10, 20, 30, 50)
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.bodyLarge) },
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
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text("LEARNING", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            // Weekly Learning Goal Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { weeklyGoalExpanded = !weeklyGoalExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Weekly Learning Goal", style = MaterialTheme.typography.bodyLarge)
                            Text("$currentGoal words per week", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            imageVector = if (weeklyGoalExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (weeklyGoalExpanded) "Collapse" else "Expand"
                        )
                    }
                    
                    AnimatedVisibility(visible = weeklyGoalExpanded) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(
                                "How many words do you want to learn each week?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            options.forEach { goalOption ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.updateWeeklyGoal(goalOption) }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (currentGoal == goalOption),
                                        onClick = { viewModel.updateWeeklyGoal(goalOption) }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("$goalOption words", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            
                            // Custom Option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCustomDialog = true }
                                    .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isCustom = !options.contains(currentGoal)
                                RadioButton(
                                    selected = isCustom,
                                    onClick = { showCustomDialog = true }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    if (isCustom) "Custom ($currentGoal words)" else "Custom",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Home Page Word Order Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { homeWordOrderExpanded = !homeWordOrderExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Word Order", style = MaterialTheme.typography.bodyLarge)
                            Text(homeWordOrder, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            imageVector = if (homeWordOrderExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (homeWordOrderExpanded) "Collapse" else "Expand"
                        )
                    }
                    
                    AnimatedVisibility(visible = homeWordOrderExpanded) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            val orderOptions = listOf("Random", "Newest first", "Oldest first")
                            orderOptions.forEach { orderOption ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.updateHomeWordOrder(orderOption) }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (homeWordOrder == orderOption),
                                        onClick = { viewModel.updateHomeWordOrder(orderOption) }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(orderOption, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("AI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            // AI / API Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { aiExpanded = !aiExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("AI Provider", style = MaterialTheme.typography.bodyLarge)
                            Text(aiProvider, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            imageVector = if (aiExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (aiExpanded) "Collapse" else "Expand"
                        )
                    }
                    
                    AnimatedVisibility(visible = aiExpanded) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            // Provider
                            var providerExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = providerExpanded,
                                onExpandedChange = { providerExpanded = !providerExpanded }
                            ) {
                                OutlinedTextField(
                                    value = aiProvider,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("API Provider") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = providerExpanded,
                                    onDismissRequest = { providerExpanded = false }
                                ) {
                                    val providers = listOf("Gemini", "OpenAI / ChatGPT", "OpenRouter", "GapGPT")
                                    providers.forEach { providerName ->
                                        DropdownMenuItem(
                                            text = { Text(providerName) },
                                            onClick = { 
                                                viewModel.updateAiProvider(providerName)
                                                providerExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // API Key & Model for selected provider
                            when (aiProvider) {
                                "OpenAI / ChatGPT" -> {
                                    OutlinedTextField(
                                        value = openaiApiKey,
                                        onValueChange = { viewModel.updateOpenaiApiKey(it) },
                                        label = { Text("OpenAI API Key") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = openaiModel,
                                        onValueChange = { viewModel.updateOpenaiModel(it) },
                                        label = { Text("OpenAI Model") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "OpenRouter" -> {
                                    OutlinedTextField(
                                        value = openrouterApiKey,
                                        onValueChange = { viewModel.updateOpenrouterApiKey(it) },
                                        label = { Text("OpenRouter API Key") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = openrouterModel,
                                        onValueChange = { viewModel.updateOpenrouterModel(it) },
                                        label = { Text("OpenRouter Model") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "GapGPT" -> {
                                    OutlinedTextField(
                                        value = gapgptApiKey,
                                        onValueChange = { viewModel.updateGapgptApiKey(it) },
                                        label = { Text("GapGPT API Key") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = gapgptModel,
                                        onValueChange = { viewModel.updateGapgptModel(it) },
                                        label = { Text("GapGPT Model") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                else -> { // Gemini
                                    OutlinedTextField(
                                        value = geminiApiKey,
                                        onValueChange = { viewModel.updateGeminiApiKey(it) },
                                        label = { Text("Gemini API Key") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = geminiModel,
                                        onValueChange = { viewModel.updateGeminiModel(it) },
                                        label = { Text("Gemini Model") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTesting = true
                                        testResult = null
                                        testResult = viewModel.testAiConnection()
                                        isTesting = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isTesting
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    Text("Test Connection")
                                }
                            }
                            
                            testResult?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    it,
                                    color = if (it.contains("Success", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vocabulary AI Prompt Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("prompt_editor") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("AI Prompt", style = MaterialTheme.typography.bodyLarge)
                        Text("Edit generation prompt", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Edit Prompt"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("DATA", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            // Export Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { exportLauncher.launch("lexicon_backup.json") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Export Data", style = MaterialTheme.typography.bodyLarge)
                        Text("Save a backup of your data", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Export"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Import Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showImportWarning = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Import Data", style = MaterialTheme.typography.bodyLarge)
                        Text("Restore your vocabulary and settings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Import"
                    )
                }
            }
        }
        
        if (showImportWarning) {
            AlertDialog(
                onDismissRequest = { showImportWarning = false },
                title = { Text("Import Backup") },
                text = { Text("Importing a backup will replace your current vocabulary, collections, and progress. Your API settings will not be affected.\n\nAre you sure you want to proceed?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showImportWarning = false
                            importLauncher.launch(arrayOf("application/json"))
                        }
                    ) {
                        Text("Import", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportWarning = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (showCustomDialog) {
            AlertDialog(
                onDismissRequest = { showCustomDialog = false },
                title = { Text("Custom Weekly Goal") },
                text = {
                    OutlinedTextField(
                        value = customGoalInput,
                        onValueChange = { customGoalInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Words per week") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val customGoal = customGoalInput.toIntOrNull()
                            if (customGoal != null && customGoal > 0) {
                                viewModel.updateWeeklyGoal(customGoal)
                            }
                            showCustomDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
