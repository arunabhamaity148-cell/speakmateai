package com.example.ui.practice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ChatMessage
import com.example.data.model.ScenarioType
import com.example.data.model.SpeechState
import com.example.data.model.TtsState
import com.example.ui.components.CorrectionCard
import com.example.ui.components.MicButton
import com.example.ui.components.TranscriptEditorDialog
import com.example.ui.components.VocabularySaveDialog
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingPracticeScreen(
    scenarioId: String? = null,
    lessonId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val speechState by viewModel.speechState.collectAsStateWithLifecycle()
    val recognizedText by viewModel.recognizedText.collectAsStateWithLifecycle()
    val partialText by viewModel.partialText.collectAsStateWithLifecycle()
    val soundLevel by viewModel.soundLevel.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()
    val ttsState by viewModel.ttsState.collectAsStateWithLifecycle()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.onMicTapped(true)
        }
    }

    // Initialize session once
    LaunchedEffect(Unit) {
        val type = when {
            !lessonId.isNullOrBlank() -> ScenarioType.DAILY_LESSON
            !scenarioId.isNullOrBlank() -> ScenarioType.ROLE_PLAY
            else -> ScenarioType.FREE_TALK
        }
        val title = when {
            !lessonId.isNullOrBlank() -> "Lesson Practice"
            !scenarioId.isNullOrBlank() -> "Role-Play Practice"
            else -> "Free English Speaking"
        }
        viewModel.initSession(title = title, type = type, scenarioContext = scenarioId, lessonId = lessonId)
    }

    // Auto-open transcript editor dialog when speech recognition completes with text
    LaunchedEffect(speechState, recognizedText) {
        if (speechState == SpeechState.SUCCESS && recognizedText.isNotBlank()) {
            viewModel.onSpeechRecognized(recognizedText)
        }
    }

    // Lifecycle cleanup: stop audio when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.speechManager.stopListening()
            viewModel.ttsManager.stop()
        }
    }

    BackHandler {
        viewModel.endSession(onNavigateBack)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(uiState.messages.size, uiState.isAiResponding) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    var isKeyboardMode by remember { mutableStateOf(false) }
    var typedText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.sessionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = TealPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatSeconds(uiState.durationSeconds),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = TealPrimary
                            )
                            if (uiState.isLiveAi) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• Live Gemini",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.endSession(onNavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "End Session & Go Back")
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { viewModel.endSession(onNavigateBack) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(34.dp)
                            .testTag("end_session_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("End Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Speech Controls Area
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Live Speech Feedback / Partial transcript banner
                    if (speechState == SpeechState.LISTENING || partialText.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (partialText.isNotBlank()) "\"$partialText...\"" else "Listening... Please speak in English",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TealPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            WaveformVisualizer(
                                isListening = speechState == SpeechState.LISTENING,
                                soundLevel = soundLevel,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Speech Error Display
                    if (speechError != null && speechState == SpeechState.ERROR) {
                        Text(
                            text = speechError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    if (isKeyboardMode) {
                        // Typed input fallback
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = typedText,
                                onValueChange = { typedText = it },
                                placeholder = { Text("Type English message...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("typed_input_field"),
                                shape = RoundedCornerShape(16.dp),
                                maxLines = 3
                            )
                            IconButton(
                                onClick = {
                                    if (typedText.isNotBlank()) {
                                        viewModel.confirmAndSendMessage(typedText)
                                        typedText = ""
                                    }
                                },
                                enabled = typedText.isNotBlank() && !uiState.isAiResponding,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (typedText.isNotBlank()) TealPrimary else Color.Gray)
                                    .testTag("typed_send_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                            }
                            IconButton(
                                onClick = { isKeyboardMode = false },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.KeyboardVoice, contentDescription = "Switch to Microphone", tint = TealPrimary)
                            }
                        }
                    } else {
                        // Main Speech Controls Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Switch to Keyboard Button
                            IconButton(
                                onClick = { isKeyboardMode = true },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Keyboard,
                                    contentDescription = "Switch to Keyboard Typing",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Big Center Microphone Button
                            MicButton(
                                speechState = speechState,
                                soundLevel = soundLevel,
                                onClick = {
                                    if (!hasMicPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        viewModel.onMicTapped(true)
                                    }
                                }
                            )

                            // Repeat / TTS audio button
                            IconButton(
                                onClick = { viewModel.repeatLastAiSpeech() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (ttsState == TtsState.SPEAKING) Icons.Default.VolumeUp else Icons.Default.Replay,
                                    contentDescription = "Repeat AI Speech",
                                    tint = if (ttsState == TtsState.SPEAKING) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Active Correction Banner (if any)
            AnimatedVisibility(
                visible = uiState.activeCorrection != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.activeCorrection?.let { correction ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        CorrectionCard(
                            correction = correction,
                            onTryAgain = { correctedSentence ->
                                viewModel.confirmAndSendMessage(correctedSentence)
                                viewModel.closeActiveCorrection()
                            },
                            onSaveToNotebook = { en, bn ->
                                viewModel.promptToSaveVocabulary(en, bn)
                            }
                        )
                    }
                }
            }

            // Message Transcript List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onSpeakAloud = { viewModel.ttsManager.speak(message.content) },
                        onSaveWord = { viewModel.promptToSaveVocabulary(message.content, "") }
                    )
                }

                if (uiState.isAiResponding) {
                    item {
                        AiThinkingBubble()
                    }
                }
            }
        }
    }

    // Modal: Review/Edit Recognized Transcript
    if (uiState.showTranscriptReview) {
        TranscriptEditorDialog(
            initialText = uiState.pendingTranscriptText,
            onDismiss = { viewModel.dismissTranscriptReview() },
            onConfirmSend = { editedText ->
                viewModel.confirmAndSendMessage(editedText)
            }
        )
    }

    // Modal: Save Vocabulary
    if (uiState.showSaveVocabDialog) {
        VocabularySaveDialog(
            initialWord = uiState.vocabToSaveWord,
            initialMeaning = uiState.vocabToSaveMeaning,
            initialExample = uiState.vocabToSaveWord,
            onDismiss = { viewModel.dismissSaveVocabDialog() },
            onSave = { w, m, ex, note ->
                viewModel.saveVocabularyItem(w, m, ex, note)
            }
        )
    }

    // Modal: Microphone Permission Explanation
    if (uiState.showPermissionExplanation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPermissionExplanation() },
            title = { Text("Microphone Permission Required") },
            text = {
                Text(
                    "SpeakMate AI needs access to your microphone so you can practice speaking in English. Your audio is processed directly by Android's Speech Recognizer to convert your speech into text. No raw recordings are permanently saved without your consent."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissPermissionExplanation()
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissPermissionExplanation() }) {
                    Text("Use Keyboard Instead")
                }
            }
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSpeakAloud: () -> Unit,
    onSaveWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TealPrimary)
                    .padding(top = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color(0xFF00382E),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser) Color(0xFF00382E) else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onSpeakAloud,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen to pronunciation",
                                tint = TealPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiThinkingBubble(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TealPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color(0xFF00382E),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tutor is listening & formulating reply...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
