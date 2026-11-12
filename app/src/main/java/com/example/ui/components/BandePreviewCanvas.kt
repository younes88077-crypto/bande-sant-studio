package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextOverlayConfig
import kotlin.math.roundToInt

@Composable
fun BandePreviewCanvas(
    bitmap: Bitmap?,
    config: TextOverlayConfig,
    onDragOffsetChanged: (Float, Float) -> Unit,
    onResetOffset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .testTag("bande_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header bar for preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.OpenWith,
                            contentDescription = "تحريك النص بالسحب",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "معاينة حية (اسحب النص لتعديل مكانه)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                if (config.offsetX != 0f || config.offsetY != 0f) {
                    IconButton(
                        onClick = onResetOffset,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("reset_drag_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "إعادة ضبط مكان النص",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Aspect Box
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1800f / 560f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F4F8))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "شريط البوند سانتي",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Interactive Draggable Overlay Text Box
                val parsedColor = try {
                    Color(android.graphics.Color.parseColor(config.textColorHex))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }

                val currentFontFamily = if (config.isSerif) FontFamily.Serif else FontFamily.Default
                val currentWeight = if (config.isBold) FontWeight.Bold else FontWeight.Medium

                val fullName = if (config.titlePrefix.isNotBlank()) {
                    "${config.titlePrefix} ${config.doctorName}"
                } else {
                    config.doctorName
                }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = config.offsetX.roundToInt(),
                                y = config.offsetY.roundToInt()
                            )
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onDragOffsetChanged(dragAmount.x, dragAmount.y)
                            }
                        }
                        .padding(horizontal = 8.dp)
                        .testTag("draggable_text_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Doctor Name
                        Text(
                            text = fullName,
                            color = parsedColor,
                            fontSize = config.fontSizeSp.sp,
                            fontWeight = currentWeight,
                            fontFamily = currentFontFamily,
                            textAlign = TextAlign.Center
                        )

                        // Doctor Profession
                        if (config.doctorProfession.isNotBlank()) {
                            Text(
                                text = config.doctorProfession,
                                color = parsedColor.copy(alpha = 0.9f),
                                fontSize = (config.fontSizeSp * 0.7f).sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = currentFontFamily,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Clinic info
                        if (config.clinicInfo.isNotBlank()) {
                            Text(
                                text = config.clinicInfo,
                                color = Color(0xFF3C4043),
                                fontSize = (config.fontSizeSp * 0.52f).sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Default,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
