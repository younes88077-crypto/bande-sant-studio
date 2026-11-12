package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.MedicalSpecialtyPreset
import com.example.model.TextOverlayConfig
import com.example.viewmodel.VoiceTargetField

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorInputSection(
    config: TextOverlayConfig,
    onNameChange: (String) -> Unit,
    onProfessionChange: (String) -> Unit,
    onClinicInfoChange: (String) -> Unit,
    onTitlePrefixChange: (String) -> Unit,
    onVoiceClick: (VoiceTargetField) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("doctor_input_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "بيانات الطبيب / المهني الصحي",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اكتب يدوياً أو اضغط الميكروفون للإملاء الصوتي",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prefix Chips (د. / Dr. / Pr. / صيدلي ...)
            val prefixes = listOf("د.", "دكتور", "Dr.", "Pr.", "الصيدلي", "جراح أسنان")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                prefixes.forEach { prefix ->
                    val isSelected = config.titlePrefix == prefix
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onTitlePrefixChange("") else onTitlePrefixChange(prefix)
                        },
                        label = { Text(prefix) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("prefix_chip_$prefix")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Doctor Name Field
            OutlinedTextField(
                value = config.doctorName,
                onValueChange = onNameChange,
                label = { Text("الاسم واللقب") },
                placeholder = { Text("مثال: كريم بن علي") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (config.doctorName.isNotEmpty()) {
                            IconButton(onClick = { onNameChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(20.dp))
                            }
                        }
                        IconButton(
                            onClick = { onVoiceClick(VoiceTargetField.NAME) },
                            modifier = Modifier.testTag("mic_name_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "إملاء صوتي للاسم",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("doctor_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Profession Field
            OutlinedTextField(
                value = config.doctorProfession,
                onValueChange = onProfessionChange,
                label = { Text("المهنة / التخصص الطبي") },
                placeholder = { Text("مثال: طبيب عام وجراح") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (config.doctorProfession.isNotEmpty()) {
                            IconButton(onClick = { onProfessionChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(20.dp))
                            }
                        }
                        IconButton(
                            onClick = { onVoiceClick(VoiceTargetField.PROFESSION) },
                            modifier = Modifier.testTag("mic_profession_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "إملاء صوتي للمهنة",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("doctor_profession_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Specialty Preset Chips
            Text(
                text = "اقتراحات التخصص السريعة:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MedicalSpecialtyPreset.values().take(6).forEach { preset ->
                    FilterChip(
                        selected = config.doctorProfession == preset.titleAr,
                        onClick = { onProfessionChange(preset.titleAr) },
                        label = { Text(preset.titleAr, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Clinic Info & Phone Field
            OutlinedTextField(
                value = config.clinicInfo,
                onValueChange = onClinicInfoChange,
                label = { Text("معلومات العيادة / الهاتف (اختياري)") },
                placeholder = { Text("مثال: عيادة الشفاء - هاتف: 0550 12 34 56") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (config.clinicInfo.isNotEmpty()) {
                            IconButton(onClick = { onClinicInfoChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(20.dp))
                            }
                        }
                        IconButton(
                            onClick = { onVoiceClick(VoiceTargetField.CLINIC_INFO) },
                            modifier = Modifier.testTag("mic_clinic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "إملاء صوتي للعيادة",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clinic_info_input")
            )
        }
    }
}
