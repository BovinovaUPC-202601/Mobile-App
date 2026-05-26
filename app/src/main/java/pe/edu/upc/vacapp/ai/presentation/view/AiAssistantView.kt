package pe.edu.upc.vacapp.ai.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiAnalysisHistoryItem
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiAssistantViewModel
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiChatMessage
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiChatMode
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.ui.theme.Color
import java.io.ByteArrayOutputStream
import java.util.Locale

private enum class AiAssistantSection {
    CHAT,
    PHOTO
}

@Composable
fun AiAssistantView(
    viewModel: AiAssistantViewModel
) {
    val animals by viewModel.animals.collectAsState()
    val selectedBovineId by viewModel.selectedBovineId.collectAsState()
    val generalMessages by viewModel.generalMessages.collectAsState()
    val bovineMessages by viewModel.bovineMessages.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val analysisHistory by viewModel.analysisHistory.collectAsState()
    val isLoadingAnimals by viewModel.isLoadingAnimals.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val isAnalysisLoading by viewModel.isAnalysisLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var section by remember { mutableStateOf(AiAssistantSection.CHAT) }
    var chatMode by remember { mutableStateOf(AiChatMode.GENERAL) }

    LaunchedEffect(Unit) {
        viewModel.loadAnimals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI Assistant",
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = Color.ForestGreen,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = section == AiAssistantSection.CHAT,
                onClick = { section = AiAssistantSection.CHAT },
                label = { Text("Chat") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = section == AiAssistantSection.PHOTO,
                onClick = { section = AiAssistantSection.PHOTO },
                label = { Text("Photo analysis") },
                modifier = Modifier.weight(1f)
            )
        }

        if (errorMessage != null) {
            ErrorCard(
                message = errorMessage.orEmpty(),
                onDismiss = { viewModel.clearErrorMessage() }
            )
        }

        when (section) {
            AiAssistantSection.CHAT -> ChatSection(
                animals = animals,
                selectedBovineId = selectedBovineId,
                chatMode = chatMode,
                generalMessages = generalMessages,
                bovineMessages = bovineMessages,
                isLoadingAnimals = isLoadingAnimals,
                isChatLoading = isChatLoading,
                onChatModeChange = { chatMode = it },
                onBovineSelected = viewModel::selectBovine,
                onSend = { viewModel.sendMessage(chatMode, it) }
            )

            AiAssistantSection.PHOTO -> PhotoAnalysisSection(
                animals = animals,
                selectedBovineId = selectedBovineId,
                isLoadingAnimals = isLoadingAnimals,
                isAnalysisLoading = isAnalysisLoading,
                analysisResult = analysisResult,
                analysisHistory = analysisHistory,
                onBovineSelected = viewModel::selectBovine,
                onAnalyzePhoto = viewModel::analyzePhoto
            )
        }
    }
}

@Composable
private fun ChatSection(
    animals: List<Animal>,
    selectedBovineId: Int?,
    chatMode: AiChatMode,
    generalMessages: List<AiChatMessage>,
    bovineMessages: List<AiChatMessage>,
    isLoadingAnimals: Boolean,
    isChatLoading: Boolean,
    onChatModeChange: (AiChatMode) -> Unit,
    onBovineSelected: (Int) -> Unit,
    onSend: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val activeMessages = if (chatMode == AiChatMode.GENERAL) generalMessages else bovineMessages
    val chatListState = rememberLazyListState()

    LaunchedEffect(activeMessages.size, isChatLoading, chatMode) {
        val lastIndex = activeMessages.lastIndex + if (isChatLoading) 1 else 0
        if (lastIndex >= 0) {
            chatListState.animateScrollToItem(lastIndex)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = chatMode == AiChatMode.GENERAL,
                onClick = { onChatModeChange(AiChatMode.GENERAL) },
                label = { Text("General") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = chatMode == AiChatMode.BOVINE,
                onClick = { onChatModeChange(AiChatMode.BOVINE) },
                label = { Text("Bovine") },
                modifier = Modifier.weight(1f)
            )
        }

        if (chatMode == AiChatMode.BOVINE) {
            BovineSelector(
                animals = animals,
                selectedBovineId = selectedBovineId,
                isLoadingAnimals = isLoadingAnimals,
                onBovineSelected = onBovineSelected
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                state = chatListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(activeMessages, key = { it.id }) { message ->
                    ChatBubble(message)
                }

                if (isChatLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.ForestGreen,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask about your herd...") },
                modifier = Modifier.weight(1f),
                maxLines = 3,
                enabled = !isChatLoading
            )

            IconButton(
                onClick = {
                    val message = input
                    input = ""
                    onSend(message)
                },
                enabled = input.isNotBlank() && !isChatLoading
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send message",
                    tint = Color.ForestGreen
                )
            }
        }
    }
}

@Composable
private fun PhotoAnalysisSection(
    animals: List<Animal>,
    selectedBovineId: Int?,
    isLoadingAnimals: Boolean,
    isAnalysisLoading: Boolean,
    analysisResult: AnalysisResultResponse?,
    analysisHistory: List<AiAnalysisHistoryItem>,
    onBovineSelected: (Int) -> Unit,
    onAnalyzePhoto: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageBase64 by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        selectedBitmap = null
        imageBase64 = uri?.let { uriToBase64(context, it) }.orEmpty()
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        selectedBitmap = bitmap
        selectedImageUri = null
        imageBase64 = bitmap?.let { bitmapToBase64(it) }.orEmpty()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Analyze Bovine",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.ForestGreen,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        BovineSelector(
            animals = animals,
            selectedBovineId = selectedBovineId,
            isLoadingAnimals = isLoadingAnimals,
            onBovineSelected = onBovineSelected
        )

        PhotoPreview(
            selectedImageUri = selectedImageUri,
            selectedBitmap = selectedBitmap
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Gallery")
            }

            OutlinedButton(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Camera")
            }
        }

        Button(
            onClick = { onAnalyzePhoto(imageBase64) },
            enabled = !isAnalysisLoading && imageBase64.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.ForestGreen)
        ) {
            if (isAnalysisLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
            }
            Spacer(Modifier.size(8.dp))
            Text("Analyze with AI")
        }

        if (analysisResult != null) {
            AnalysisResultCard(analysisResult)
        }

        if (analysisHistory.isNotEmpty()) {
            AnalysisHistoryList(analysisHistory)
        }
    }
}

@Composable
private fun BovineSelector(
    animals: List<Animal>,
    selectedBovineId: Int?,
    isLoadingAnimals: Boolean,
    onBovineSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAnimal = animals.firstOrNull { it.id == selectedBovineId }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = animals.isNotEmpty() && !isLoadingAnimals,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "BOVINE",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = Color.Black
                )
                Text(
                    text = when {
                        isLoadingAnimals -> "Loading bovines..."
                        selectedAnimal != null -> selectedAnimal.name
                        animals.isEmpty() -> "No bovines available"
                        else -> "Select bovine"
                    },
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = Color.Black)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            animals.forEach { animal ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${animal.name} - ${animal.breed}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onBovineSelected(animal.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: AiChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isFromUser) Color.ForestGreen else Color.AlmondCream
                )
            ) {
                Text(
                    text = messageContent(message.content),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    color = if (message.isFromUser) Color.White else Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }
            Text(
                text = message.sentAt,
                color = Color.Green,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

private fun messageContent(content: String): AnnotatedString {
    val normalizedContent = content.replace(Regex("(?m)^\\s*\\*\\s+"), "• ")

    return buildAnnotatedString {
        var cursor = 0

        while (cursor < normalizedContent.length) {
            val opening = normalizedContent.indexOf("**", cursor)

            if (opening == -1) {
                append(normalizedContent.substring(cursor))
                break
            }

            append(normalizedContent.substring(cursor, opening))

            val closing = normalizedContent.indexOf("**", opening + 2)
            if (closing == -1) {
                append(normalizedContent.substring(opening))
                break
            }

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(normalizedContent.substring(opening + 2, closing))
            }
            cursor = closing + 2
        }
    }
}

@Composable
private fun PhotoPreview(
    selectedImageUri: Uri?,
    selectedBitmap: Bitmap?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Green)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedBitmap != null -> {
                    Image(
                        bitmap = selectedBitmap.asImageBitmap(),
                        contentDescription = "Captured bovine photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                selectedImageUri != null -> {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected bovine photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pets,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Select or capture a bovine photo",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisResultCard(result: AnalysisResultResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.AlmondCream)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analysis Result",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                UrgencyBadge(result.urgency)
            }

            Text(
                text = "BODY CONDITION SCORE",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color.Black
            )
            Text(
                text = "${String.format(Locale.US, "%.1f", result.score)} / 5",
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = Color.Black
            )

            Text(
                text = "VISIBLE ISSUES",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color.Black
            )
            Text(
                text = result.visibleIssues,
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = "RECOMMENDATION",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color.Black
            )
            Text(
                text = result.recommendation,
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = "Confidence ${String.format(Locale.US, "%.0f", result.confidence * 100)}%",
                fontSize = 12.sp,
                color = Color.Green
            )
        }
    }
}

@Composable
private fun AnalysisHistoryList(history: List<AiAnalysisHistoryItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "AI History",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.ForestGreen
        )

        history.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.AlmondCream)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(34.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.bovineName} - ${item.createdAt}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        UrgencyBadge(item.result.urgency)
                        Text(
                            text = "BCS ${String.format(Locale.US, "%.1f", item.result.score)} / 5",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = item.result.recommendation,
                            fontSize = 12.sp,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UrgencyBadge(urgency: String) {
    val color = when (urgency.lowercase(Locale.US)) {
        "high" -> androidx.compose.ui.graphics.Color(0xFFE53E3E)
        "medium" -> androidx.compose.ui.graphics.Color(0xFFD69E2E)
        "low" -> androidx.compose.ui.graphics.Color(0xFF38A169)
        else -> Color.Green
    }

    Card(
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            text = urgency.uppercase(Locale.US),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFE2E2))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = androidx.compose.ui.graphics.Color(0xFF8A1F1F),
                fontSize = 13.sp
            )
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    }
}

private fun uriToBase64(context: Context, uri: Uri): String {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
    }.orEmpty()
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
    return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
}
