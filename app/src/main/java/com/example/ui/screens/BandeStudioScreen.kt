package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiGenerationDialog
import com.example.ui.components.BandePreviewCanvas
import com.example.ui.components.DoctorInputSection
import com.example.ui.components.ExportBottomBar
import com.example.ui.components.StylingControlsSection
import com.example.ui.components.TemplatePickerSection
import com.example.viewmodel.AiGenUiState
import com.example.viewmodel.ExportUiState
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.VoiceTargetField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandeStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentTemplate by viewModel.currentTemplate.collectAsStateWithLifecycle()
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val activeBitmap by viewModel.activeBannerBitmap.collectAsStateWithLifecycle()
    val textConfig by viewModel.textConfig.collectAsStateWithLifecycle()
    val aiState by viewModel.aiGenState.collectAsStateWithLifecycle()
    val exportState by viewModel.exportState.collectAsStateWithLifecycle()

    var showAiDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // 1. Photo Picker Launcher (Modern, zero-permission media selection)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setUploadedImage(uri)
        }
    }

    // 2. Speech Recognizer Intent Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spokenMatches?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                viewModel.onSpeechResultReceived(recognizedText)
            }
        }
    }

    fun startSpeechIntent(target: VoiceTargetField) {
        viewModel.setVoiceTarget(target)
        val promptTitle = when (target) {
            VoiceTargetField.NAME -> "تفضل بإملاء اسم ولقب الطبيب..."
            VoiceTargetField.PROFESSION -> "تفضل بإملاء المهنة أو التخصص الطبي..."
            VoiceTargetField.CLINIC_INFO -> "تفضل بإملاء معلومات العيادة أو رقم الهاتف..."
        }

        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, promptTitle)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizerLauncher.launch(speechIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "التعرف الصوتي غير متوفر على هذا الجهاز", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Audio Permission Request Launcher
    var pendingVoiceTarget by remember { mutableStateOf<VoiceTargetField?>(null) }
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingVoiceTarget?.let { startSpeechIntent(it) }
        } else {
            Toast.makeText(context, "يرجى منح إذن الميكروفون لاستخدام الإملاء الصوتي", Toast.LENGTH_LONG).show()
        }
        pendingVoiceTarget = null
    }

    fun handleVoiceClick(target: VoiceTargetField) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startSpeechIntent(target)
        } else {
            pendingVoiceTarget = target
            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle Export State messages
    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportUiState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.dismissExportState()
            }
            is ExportUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.dismissExportState()
            }
            else -> {}
        }
    }

    // Handle AI State completion
    LaunchedEffect(aiState) {
        if (aiState is AiGenUiState.Success) {
            showAiDialog = false
            snackbarHostState.showSnackbar("تم توليد شريط البوند سانتي بالذكاء الاصطناعي بنجاح!")
            viewModel.dismissAiState()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bande Santé Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "استوديو تصميم البوند سانتي والوصفات الطبية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("app_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "معلومات التطبيق",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            ExportBottomBar(
                exportState = exportState,
                onSaveToGallery = { viewModel.saveToGallery() },
                onShare = { viewModel.shareCurrentBande() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Live Interactive Preview Canvas
            BandePreviewCanvas(
                bitmap = activeBitmap,
                config = textConfig,
                onDragOffsetChanged = { dx, dy -> viewModel.updateOffsets(dx, dy) },
                onResetOffset = { viewModel.resetOffsets() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Bande Template Picker & AI Trigger
            TemplatePickerSection(
                templates = templates,
                activeTemplate = currentTemplate,
                onSelectTemplate = { viewModel.selectTemplate(it) },
                onUploadClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onOpenAiDialog = { showAiDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Doctor Details Input Section (Manual + Voice Mic)
            DoctorInputSection(
                config = textConfig,
                onNameChange = { viewModel.updateName(it) },
                onProfessionChange = { viewModel.updateProfession(it) },
                onClinicInfoChange = { viewModel.updateClinicInfo(it) },
                onTitlePrefixChange = { viewModel.updateTitlePrefix(it) },
                onVoiceClick = { handleVoiceClick(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Typography & Styling Controls Section
            StylingControlsSection(
                config = textConfig,
                onColorChange = { viewModel.updateTextColor(it) },
                onFontSizeChange = { viewModel.updateFontSize(it) },
                onToggleBold = { viewModel.toggleBold() },
                onToggleSerif = { viewModel.toggleSerif() },
                onResetOffset = { viewModel.resetOffsets() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // AI Generation Modal Dialog
    if (showAiDialog) {
        AiGenerationDialog(
            aiState = aiState,
            onGenerate = { prompt -> viewModel.generateAiBanner(prompt) },
            onDismiss = {
                viewModel.dismissAiState()
                showAiDialog = false
            }
        )
    }

    // App Information Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(text = "عن تطبيق Bande Santé Studio")
            },
            text = {
                Column {
                    Text(
                        text = "تطبيق مخصص للمهنيين الصحيين لتصميم وتخصيص أشرطة الترويسة الطبية (Bande Santé):\n\n" +
                                "• رفع أي بوند من هاتفك بنقرة واحدة.\n" +
                                "• إملاء الاسم والمهنة صوتياً عبر الميكروفون أو كتابتها يدوياً.\n" +
                                "• سحب وتحريك النص في أي موضع فوق البوند.\n" +
                                "• توليد تصاميم جديدة بالذكاء الاصطناعي (Gemini).\n" +
                                "• قوالب كلاسيكية مدمجة فائقة الدقة.\n" +
                                "• حفظ فوري عالي الدقة في المعرض ومشاركة سريعة."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("حسناً")
                }
            }
        )
    }
}
