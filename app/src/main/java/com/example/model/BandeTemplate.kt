package com.example.model

import android.graphics.Bitmap
import android.net.Uri

enum class TemplateSource {
    BUILT_IN,
    GALLERY_UPLOAD,
    AI_GENERATED
}

enum class MedicalSpecialtyPreset(val titleAr: String, val titleFr: String) {
    GENERAL_MEDICINE("طبيب عام", "Médecin Généraliste"),
    DENTISTRY("جراح أسنان", "Chirurgien Dentiste"),
    CARDIOLOGY("أخصائي أمراض القلب", "Cardiologue"),
    PEDIATRICS("أخصائي طب الأطفال", "Pédiatre"),
    PHARMACY("صيدلي", "Pharmacien"),
    OPHTHALMOLOGY("أخصائي طب العيون", "Ophtalmologue"),
    DERMATOLOGY("أخصائي أمراض الجلد", "Dermatologue"),
    GYNECOLOGY("أمراض النساء والتوليد", "Gynécologue Obstétricien"),
    RADIOLOGY("أخصائي الأشعة والتصوير", "Radiologue"),
    NURSING("ممرض صحي معتمد", "Infirmier de Santé Publique")
}

data class BandeTemplate(
    val id: String,
    val nameAr: String,
    val descriptionAr: String,
    val source: TemplateSource,
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val styleKey: String = "classic_gold"
)

data class TextOverlayConfig(
    val titlePrefix: String = "د.",
    val doctorName: String = "كريم بن علي",
    val doctorProfession: String = "طبيب عام وجراح",
    val clinicInfo: String = "عيادة النور الطبية - هاتف: 0550 12 34 56",
    val textColorHex: String = "#0D47A1",
    val fontSizeSp: Float = 26f,
    val professionFontSizeSp: Float = 18f,
    val infoFontSizeSp: Float = 14f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val isBold: Boolean = true,
    val isSerif: Boolean = true,
    val showBorderBox: Boolean = false,
    val showEmblem: Boolean = true,
    val emblemPosition: String = "RIGHT" // RIGHT, LEFT, CENTER
)
