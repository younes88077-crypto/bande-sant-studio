package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AiGenUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGenerationDialog(
    aiState: AiGenUiState,
    onGenerate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPrompt by remember {
        mutableStateOf("شريط طبي كلاسيكي فاخر بزخارف ذهبية وشارة الهلال والطب وأسكليبيوس مع إطار ملكي فخم")
    }

    val stylePresets = listOf(
        "شريط طبي كلاسيكي فاخر بزخارف ذهبية وشارة الهلال والطب وأسكليبيوس مع إطار ملكي فخم",
        "ترويسة طبية زرقاء جراحية ملكية بإطار رسمي للمستشفيات والعيادات الكبرى",
        "شريط صيدلاني أخضر زمردي مع كأس هايجيا وشارة الصليب الأخضر",
        "شريط عيادي حديث مينيمالي بالأزرق السماوي لمخابر التحاليل الطبية والتشخيص"
    )

    AlertDialog(
        onDismissRequest = {
            if (aiState !is AiGenUiState.Loading) onDismiss()
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "توليد بوند سانتي بالذكاء الاصطناعي",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                if (aiState !is AiGenUiState.Loading) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "اختر نمطاً كلاسيكياً أو اكتب وصفاً مخصصاً لإنشاء شريط ترويسة فريد:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Presets
                Text(
                    text = "الأنماط المقترحة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    stylePresets.forEachIndexed { index, preset ->
                        val label = when (index) {
                            0 -> "كلاسيكي ذهبي مع هلال"
                            1 -> "أزرق جراحي ملكي"
                            2 -> "صيدلاني زمردي"
                            else -> "عيادي مينيمالي"
                        }
                        val isSelected = selectedPrompt == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPrompt = preset },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prompt Input
                OutlinedTextField(
                    value = selectedPrompt,
                    onValueChange = { selectedPrompt = it },
                    label = { Text("وصف تصميم البوند") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_prompt_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Loading State feedback
                AnimatedVisibility(visible = aiState is AiGenUiState.Loading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "جاري توليد البوند بالذكاء الاصطناعي (Gemini)...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "يتم رسم شريط عالي الدقة، قد يستغرق 30-45 ثانية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Error State feedback
                AnimatedVisibility(visible = aiState is AiGenUiState.Error) {
                    val errorMsg = (aiState as? AiGenUiState.Error)?.message.orEmpty()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(selectedPrompt) },
                enabled = aiState !is AiGenUiState.Loading && selectedPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_ai_generate_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("توليد الآن")
            }
        },
        dismissButton = {
            if (aiState !is AiGenUiState.Loading) {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )
}
