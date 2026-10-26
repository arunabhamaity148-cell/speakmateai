package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showTroubleshootDialog by remember { mutableStateOf(false) }
    var showAiStatusDialog by remember { mutableStateOf(false) }
    var dataClearedToast by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 700.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Customize your English learning preferences, voice tutor, and privacy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tutor & Learning Preferences
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Learning Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Explanation Language
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Preferred Explanation Language",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Bengali", "English").forEach { lang ->
                                    val isSelected = settings.preferredExplanationLanguage.equals(lang, ignoreCase = true)
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(preferredExplanationLanguage = lang))
                                        },
                                        label = { Text(if (lang == "Bengali") "বাংলা (Bengali)" else "English Only") },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface,
                                            labelColor = if (isSelected) Color(0xFF00382E) else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }

                        // Difficulty Level
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Learner Ability Level",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                                    val isSelected = settings.difficultyPreference.equals(level, ignoreCase = true)
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(difficultyPreference = level))
                                        },
                                        label = { Text(level) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface,
                                            labelColor = if (isSelected) Color(0xFF00382E) else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }

                        // Daily Goal
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Daily Speaking Goal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(10, 15, 20, 30).forEach { mins ->
                                    val isSelected = settings.dailyGoalMinutes == mins
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(dailyGoalMinutes = mins))
                                        },
                                        label = { Text("$mins mins") },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface,
                                            labelColor = if (isSelected) Color(0xFF00382E) else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Audio & Speech Settings
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Speech & Voice Output",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Sound Enabled Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Spoken AI Replies",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Automatically read AI tutor replies aloud using TextToSpeech",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings.soundEnabled,
                                onCheckedChange = {
                                    viewModel.updateSettings(settings.copy(soundEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TealPrimary
                                )
                            )
                        }

                        // Speech Rate
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Speech Speed",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = String.format("%.2fx", settings.speechRate),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = settings.speechRate,
                                onValueChange = {
                                    viewModel.updateSettings(settings.copy(speechRate = it))
                                },
                                valueRange = 0.7f..1.3f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = TealPrimary,
                                    activeTrackColor = TealPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Diagnostics & Status
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Diagnostics & Troubleshooting",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedButton(
                            onClick = { showTroubleshootDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Microphone & Speech Troubleshooting")
                        }

                        OutlinedButton(
                            onClick = { showAiStatusDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Service & Firebase Integration Status")
                        }
                    }
                }
            }

            // Privacy & Data Management
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Privacy & Local Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "SpeakMate AI stores all practice sessions, vocabulary, and grammar corrections strictly on your device using a local Room database. No account is required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { showClearDataDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clear_data_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete All Local Learning Data")
                        }
                    }
                }
            }
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Delete All Learning Data?") },
            text = {
                Text(
                    "This action will permanently delete all your conversation history, saved vocabulary, grammar corrections, and streak records from this device. This cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData {
                            showClearDataDialog = false
                            dataClearedToast = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Troubleshooting Dialog
    if (showTroubleshootDialog) {
        AlertDialog(
            onDismissRequest = { showTroubleshootDialog = false },
            title = { Text("Microphone & Speech Guide") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Microphone Permission: Ensure microphone permission is allowed in Android App Settings.")
                    Text("2. Google Speech Services: On Android, Android SpeechRecognizer uses the system speech engine. Make sure the Google app / Speech Recognition is enabled and up to date.")
                    Text("3. Background Noise: Speak 6 to 12 inches away from the microphone in a quiet room.")
                    Text("4. Keyboard Fallback: If voice input is unavailable, tap the keyboard icon at the bottom of the practice screen to type in English.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTroubleshootDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Got it")
                }
            }
        )
    }

    // AI Status Dialog
    if (showAiStatusDialog) {
        AlertDialog(
            onDismissRequest = { showAiStatusDialog = false },
            title = { Text("AI Engine Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• Provider: Firebase AI Logic (Gemini 3.8 Flash)",
                        fontWeight = FontWeight.Bold
                    )
                    Text("• Attestation: Firebase App Check with launch intent debug tokens")
                    Text("• Bengali Explanations: Structured JSON extraction with bilingual grammar rules")
                    Text("• Offline Resilience: If the internet drops or Firebase is unprovisioned, the app automatically runs its built-in smart tutor with real conversational responses and Bengali grammar corrections.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAiStatusDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
