package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.TextOverlayConfig

data class ColorOption(val nameAr: String, val hex: String, val color: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StylingControlsSection(
    config: TextOverlayConfig,
    onColorChange: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onToggleBold: () -> Unit,
    onToggleSerif: () -> Unit,
    onResetOffset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        ColorOption("أزرق طبي", "#0D47A1", Color(0xFF0D47A1)),
        ColorOption("أسود رسمي", "#1A1C1E", Color(0xFF1A1C1E)),
        ColorOption("ذهبي أسكليبيوس", "#C59B27", Color(0xFFC59B27)),
        ColorOption("فيروزي جراحي", "#00897B", Color(0xFF00897B)),
        ColorOption("عنابي ملكي", "#880E4F", Color(0xFF880E4F)),
        ColorOption("أخضر صيدلاني", "#1B5E20", Color(0xFF1B5E20)),
        ColorOption("أبيض ناصع", "#FFFFFF", Color(0xFFFFFFFF))
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("styling_controls_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatColorFill,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "تنسيق الخط والمظهر",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Swatches
            Text(
                text = "لون الخط:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colors.forEach { option ->
                    val isSelected = config.textColorHex.equals(option.hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(option.hex) }
                            .testTag("color_swatch_${option.hex}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (option.hex == "#FFFFFF") Color.Black else Color.White)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Font Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "حجم الخط: ${config.fontSizeSp.toInt()} sp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Slider(
                value = config.fontSizeSp,
                onValueChange = onFontSizeChange,
                valueRange = 18f..42f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("font_size_slider")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Style Toggles: Bold, Serif & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = config.isBold,
                    onClick = onToggleBold,
                    label = { Text("عريض (Bold)") },
                    leadingIcon = {
                        Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.testTag("bold_toggle")
                )

                FilterChip(
                    selected = config.isSerif,
                    onClick = onToggleSerif,
                    label = { Text(if (config.isSerif) "كلاسيكي (Serif)" else "عصري (Sans)") },
                    modifier = Modifier.testTag("serif_toggle")
                )

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = onResetOffset,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reset_position_button")
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة للمنتصف")
                }
            }
        }
    }
}
