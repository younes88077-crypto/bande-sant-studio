package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.TextOverlayConfig
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageExporter {

    fun createCompositeBitmap(
        baseBitmap: Bitmap,
        config: TextOverlayConfig
    ): Bitmap {
        val width = baseBitmap.width
        val height = baseBitmap.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 1. Draw base bande santé
        canvas.drawBitmap(baseBitmap, 0f, 0f, null)

        // 2. Parse text color
        val textColor = try {
            Color.parseColor(config.textColorHex)
        } catch (e: Exception) {
            Color.parseColor("#0D47A1")
        }

        // 3. Prepare Typeface
        val baseTypeface = if (config.isSerif) Typeface.SERIF else Typeface.SANS_SERIF
        val typefaceStyle = if (config.isBold) Typeface.BOLD else Typeface.NORMAL
        val typeface = Typeface.create(baseTypeface, typefaceStyle)

        // Text scale factor relative to standard 1800px width
        val scale = width.toFloat() / 1800f

        // Center coordinates + user drag offset
        val centerX = (width / 2f) + (config.offsetX * scale * 2f)
        val centerY = (height / 2f) + (config.offsetY * scale * 2f)

        // Paint for Doctor Name
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            this.typeface = typeface
            this.textSize = config.fontSizeSp * 2.6f * scale
            this.textAlign = Paint.Align.CENTER
            // Subtle shadow for legibility over any background
            setShadowLayer(4f * scale, 0f, 2f * scale, Color.argb(40, 0, 0, 0))
        }

        // Paint for Profession
        val profPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = if (Color.alpha(textColor) > 0) {
                // Slightly softer shade of the primary text color
                Color.argb(
                    230,
                    Color.red(textColor),
                    Color.green(textColor),
                    Color.blue(textColor)
                )
            } else {
                Color.DKGRAY
            }
            this.typeface = Typeface.create(baseTypeface, Typeface.BOLD)
            this.textSize = config.professionFontSizeSp * 2.4f * scale
            this.textAlign = Paint.Align.CENTER
        }

        // Paint for Clinic/Phone Info
        val infoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.rgb(60, 64, 67)
            this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            this.textSize = config.infoFontSizeSp * 2.2f * scale
            this.textAlign = Paint.Align.CENTER
        }

        // Compose Doctor Full Name text
        val fullNameText = if (config.titlePrefix.isNotBlank()) {
            "${config.titlePrefix} ${config.doctorName}"
        } else {
            config.doctorName
        }

        // Calculate line heights
        val nameBounds = Rect()
        namePaint.getTextBounds(fullNameText, 0, fullNameText.length, nameBounds)

        // Draw lines vertically centered around centerY
        val hasInfo = config.clinicInfo.isNotBlank()
        val nameY = if (hasInfo) centerY - 25f * scale else centerY - 10f * scale
        val profY = nameY + (config.professionFontSizeSp * 2.8f * scale)
        val infoY = profY + (config.infoFontSizeSp * 2.6f * scale)

        // Draw Name
        canvas.drawText(fullNameText, centerX, nameY, namePaint)

        // Draw Profession
        if (config.doctorProfession.isNotBlank()) {
            canvas.drawText(config.doctorProfession, centerX, profY, profPaint)
        }

        // Draw Clinic / Phone
        if (hasInfo) {
            canvas.drawText(config.clinicInfo, centerX, infoY, infoPaint)
        }

        return resultBitmap
    }

    fun saveToGallery(context: Context, bitmap: Bitmap): Uri? {
        val filename = "BandeSante_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BandeSante")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri)
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            fos?.close()
        }

        return imageUri
    }

    fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val imagesFolder = File(context.cacheDir, "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "bande_sante_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "بوند سانتي طبي - تم إنشاؤه عبر Bande Santé Studio")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة البوند سانتي الطبي"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
