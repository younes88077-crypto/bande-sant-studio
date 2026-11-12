package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiBannerService
import com.example.model.BandeTemplate
import com.example.model.TemplateSource
import com.example.model.TextOverlayConfig
import com.example.util.ClassicBannerGenerator
import com.example.util.ImageExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface AiGenUiState {
    data object Idle : AiGenUiState
    data object Loading : AiGenUiState
    data class Success(val bitmap: Bitmap) : AiGenUiState
    data class Error(val message: String) : AiGenUiState
}

sealed interface ExportUiState {
    data object Idle : ExportUiState
    data object Saving : ExportUiState
    data class Success(val uri: Uri?, val message: String) : ExportUiState
    data class Error(val message: String) : ExportUiState
}

enum class VoiceTargetField {
    NAME,
    PROFESSION,
    CLINIC_INFO
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AiBannerService()

    // 1. Built-in Classical Templates
    private val builtInTemplates = listOf(
        BandeTemplate(
            id = "classic_gold",
            nameAr = "الكلاسيكي الذهبي",
            descriptionAr = "شريط كلاسيكي فاخر بزخارف ذهبية وشارة الطب وأسكليبيوس",
            source = TemplateSource.BUILT_IN,
            styleKey = "classic_gold"
        ),
        BandeTemplate(
            id = "royal_blue",
            nameAr = "الأزرق الجراحي",
            descriptionAr = "شريط أزرق ملكي مع إطار طبي عيادي وشعار المستشفى",
            source = TemplateSource.BUILT_IN,
            styleKey = "royal_blue"
        ),
        BandeTemplate(
            id = "green_crescent",
            nameAr = "الهلال الزمردي",
            descriptionAr = "شريط الهلال الطبي والصيدلاني باللون الأخضر الأنيق",
            source = TemplateSource.BUILT_IN,
            styleKey = "green_crescent"
        ),
        BandeTemplate(
            id = "clinical_modern",
            nameAr = "العيادي الحديث",
            descriptionAr = "تصميم مينيمالي مع خط نبض القلب ECG ولون فيروزي",
            source = TemplateSource.BUILT_IN,
            styleKey = "clinical_modern"
        ),
        BandeTemplate(
            id = "vintage_filigree",
            nameAr = "الوصفة التاريخية",
            descriptionAr = "إطار وصفة طبية أثري مع زخارف الفيليغري البنية العريقة",
            source = TemplateSource.BUILT_IN,
            styleKey = "vintage_filigree"
        )
    )

    private val _templates = MutableStateFlow<List<BandeTemplate>>(builtInTemplates)
    val templates: StateFlow<List<BandeTemplate>> = _templates.asStateFlow()

    // 2. Active Template and its loaded Bitmap
    private val _currentTemplate = MutableStateFlow(builtInTemplates.first())
    val currentTemplate: StateFlow<BandeTemplate> = _currentTemplate.asStateFlow()

    private val _activeBannerBitmap = MutableStateFlow<Bitmap?>(null)
    val activeBannerBitmap: StateFlow<Bitmap?> = _activeBannerBitmap.asStateFlow()

    // 3. Text Overlay Configuration
    private val _textConfig = MutableStateFlow(TextOverlayConfig())
    val textConfig: StateFlow<TextOverlayConfig> = _textConfig.asStateFlow()

    // 4. AI Generation State
    private val _aiGenState = MutableStateFlow<AiGenUiState>(AiGenUiState.Idle)
    val aiGenState: StateFlow<AiGenUiState> = _aiGenState.asStateFlow()

    // 5. Export State
    private val _exportState = MutableStateFlow<ExportUiState>(ExportUiState.Idle)
    val exportState: StateFlow<ExportUiState> = _exportState.asStateFlow()

    // 6. Voice field currently targeted
    private val _activeVoiceTarget = MutableStateFlow(VoiceTargetField.NAME)
    val activeVoiceTarget: StateFlow<VoiceTargetField> = _activeVoiceTarget.asStateFlow()

    init {
        // Load initial template bitmap
        loadTemplateBitmap(_currentTemplate.value)
    }

    fun selectTemplate(template: BandeTemplate) {
        _currentTemplate.value = template
        loadTemplateBitmap(template)
    }

    private fun loadTemplateBitmap(template: BandeTemplate) {
        viewModelScope.launch(Dispatchers.Default) {
            val bitmap = when {
                template.bitmap != null -> template.bitmap
                template.uri != null -> {
                    try {
                        val context = getApplication<Application>()
                        context.contentResolver.openInputStream(template.uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
                template.source == TemplateSource.BUILT_IN -> {
                    ClassicBannerGenerator.generateClassicTemplate(template.styleKey)
                }
                else -> null
            }
            withContext(Dispatchers.Main) {
                _activeBannerBitmap.value = bitmap
            }
        }
    }

    fun setUploadedImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val context = getApplication<Application>()
                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                if (bitmap != null) {
                    val newTemplate = BandeTemplate(
                        id = "upload_${System.currentTimeMillis()}",
                        nameAr = "بوند مخصص من المعرض",
                        descriptionAr = "شريط ترويسة تم رفعه من جهازك",
                        source = TemplateSource.GALLERY_UPLOAD,
                        bitmap = bitmap,
                        uri = uri
                    )
                    withContext(Dispatchers.Main) {
                        _templates.value = listOf(newTemplate) + _templates.value
                        _currentTemplate.value = newTemplate
                        _activeBannerBitmap.value = bitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generateAiBanner(promptDescription: String) {
        viewModelScope.launch {
            _aiGenState.value = AiGenUiState.Loading
            val result = aiService.generateMedicalBanner(promptDescription)
            result.fold(
                onSuccess = { bitmap ->
                    val aiTemplate = BandeTemplate(
                        id = "ai_${System.currentTimeMillis()}",
                        nameAr = "بوند مولد بالذكاء الاصطناعي",
                        descriptionAr = promptDescription,
                        source = TemplateSource.AI_GENERATED,
                        bitmap = bitmap
                    )
                    _templates.value = listOf(aiTemplate) + _templates.value
                    _currentTemplate.value = aiTemplate
                    _activeBannerBitmap.value = bitmap
                    _aiGenState.value = AiGenUiState.Success(bitmap)
                },
                onFailure = { error ->
                    _aiGenState.value = AiGenUiState.Error(
                        error.localizedMessage ?: "حدث خطأ أثناء توليد البوند بالذكاء الاصطناعي."
                    )
                }
            )
        }
    }

    fun dismissAiState() {
        _aiGenState.value = AiGenUiState.Idle
    }

    fun setVoiceTarget(target: VoiceTargetField) {
        _activeVoiceTarget.value = target
    }

    fun onSpeechResultReceived(spokenText: String) {
        val trimmed = spokenText.trim()
        if (trimmed.isEmpty()) return

        when (_activeVoiceTarget.value) {
            VoiceTargetField.NAME -> {
                // If text begins with "دكتور" or "الدكتور" or "د.", clean or extract
                _textConfig.value = _textConfig.value.copy(doctorName = trimmed)
            }
            VoiceTargetField.PROFESSION -> {
                _textConfig.value = _textConfig.value.copy(doctorProfession = trimmed)
            }
            VoiceTargetField.CLINIC_INFO -> {
                _textConfig.value = _textConfig.value.copy(clinicInfo = trimmed)
            }
        }
    }

    fun updateName(name: String) {
        _textConfig.value = _textConfig.value.copy(doctorName = name)
    }

    fun updateProfession(profession: String) {
        _textConfig.value = _textConfig.value.copy(doctorProfession = profession)
    }

    fun updateClinicInfo(info: String) {
        _textConfig.value = _textConfig.value.copy(clinicInfo = info)
    }

    fun updateTitlePrefix(prefix: String) {
        _textConfig.value = _textConfig.value.copy(titlePrefix = prefix)
    }

    fun updateTextColor(hex: String) {
        _textConfig.value = _textConfig.value.copy(textColorHex = hex)
    }

    fun updateFontSize(sizeSp: Float) {
        _textConfig.value = _textConfig.value.copy(fontSizeSp = sizeSp)
    }

    fun updateOffsets(dragX: Float, dragY: Float) {
        _textConfig.value = _textConfig.value.copy(
            offsetX = _textConfig.value.offsetX + dragX,
            offsetY = _textConfig.value.offsetY + dragY
        )
    }

    fun resetOffsets() {
        _textConfig.value = _textConfig.value.copy(offsetX = 0f, offsetY = 0f)
    }

    fun toggleBold() {
        _textConfig.value = _textConfig.value.copy(isBold = !_textConfig.value.isBold)
    }

    fun toggleSerif() {
        _textConfig.value = _textConfig.value.copy(isSerif = !_textConfig.value.isSerif)
    }

    fun saveToGallery() {
        val base = _activeBannerBitmap.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            _exportState.value = ExportUiState.Saving
            val composite = ImageExporter.createCompositeBitmap(base, _textConfig.value)
            val uri = ImageExporter.saveToGallery(getApplication(), composite)
            withContext(Dispatchers.Main) {
                if (uri != null) {
                    _exportState.value = ExportUiState.Success(uri, "تم حفظ البوند بنجاح في معرض الصور!")
                } else {
                    _exportState.value = ExportUiState.Error("تعذر حفظ الصورة، يرجى التحقق من مساحة التخزين.")
                }
            }
        }
    }

    fun shareCurrentBande() {
        val base = _activeBannerBitmap.value ?: return
        val composite = ImageExporter.createCompositeBitmap(base, _textConfig.value)
        ImageExporter.shareBitmap(getApplication(), composite)
    }

    fun dismissExportState() {
        _exportState.value = ExportUiState.Idle
    }
}
