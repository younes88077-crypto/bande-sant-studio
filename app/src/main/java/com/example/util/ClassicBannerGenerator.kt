package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader

object ClassicBannerGenerator {

    private const val BANNER_WIDTH = 1800
    private const val BANNER_HEIGHT = 560

    fun generateClassicTemplate(styleKey: String): Bitmap {
        val bitmap = Bitmap.createBitmap(BANNER_WIDTH, BANNER_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        when (styleKey) {
            "classic_gold" -> drawClassicGold(canvas)
            "royal_blue" -> drawRoyalBlue(canvas)
            "green_crescent" -> drawGreenCrescent(canvas)
            "clinical_modern" -> drawClinicalModern(canvas)
            "vintage_filigree" -> drawVintageFiligree(canvas)
            else -> drawClassicGold(canvas)
        }

        return bitmap
    }

    private fun drawClassicGold(canvas: Canvas) {
        val w = BANNER_WIDTH.toFloat()
        val h = BANNER_HEIGHT.toFloat()

        // Background: Warm ivory to cream gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, 0f, h,
                Color.rgb(255, 253, 247),
                Color.rgb(250, 245, 230),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Outer ornate gold border
        val goldStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(197, 155, 39)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRect(24f, 24f, w - 24f, h - 24f, goldStrokePaint)

        // Inner thin gold border
        val innerGoldStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 185, 75)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(34f, 34f, w - 34f, h - 34f, innerGoldStroke)

        // Corner decorative ornaments
        val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(197, 155, 39)
            style = Paint.Style.FILL
        }
        drawCornerDiamond(canvas, 34f, 34f, 12f, cornerPaint)
        drawCornerDiamond(canvas, w - 34f, 34f, 12f, cornerPaint)
        drawCornerDiamond(canvas, 34f, h - 34f, 12f, cornerPaint)
        drawCornerDiamond(canvas, w - 34f, h - 34f, 12f, cornerPaint)

        // Left Emblem: Medical Caduceus with Snake & Staff in Gold Seal
        val sealRadius = 140f
        val sealCenterX = 180f
        val sealCenterY = h / 2f

        val sealBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                sealCenterX - sealRadius, sealCenterY - sealRadius,
                sealCenterX + sealRadius, sealCenterY + sealRadius,
                Color.rgb(255, 249, 230),
                Color.rgb(240, 220, 170),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(sealCenterX, sealCenterY, sealRadius, sealBgPaint)
        canvas.drawCircle(sealCenterX, sealCenterY, sealRadius, goldStrokePaint)
        canvas.drawCircle(sealCenterX, sealCenterY, sealRadius - 10f, innerGoldStroke)

        // Draw Caduceus in Seal
        drawCaduceusIcon(canvas, sealCenterX, sealCenterY, 95f, Color.rgb(180, 135, 25))

        // Right Emblem: Crescent with Medical Cross
        val rightCenterX = w - 180f
        val rightCenterY = h / 2f
        canvas.drawCircle(rightCenterX, rightCenterY, sealRadius, sealBgPaint)
        canvas.drawCircle(rightCenterX, rightCenterY, sealRadius, goldStrokePaint)
        canvas.drawCircle(rightCenterX, rightCenterY, sealRadius - 10f, innerGoldStroke)
        drawCrescentCross(canvas, rightCenterX, rightCenterY, 80f, Color.rgb(180, 135, 25))

        // Divider Lines for the center text area
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(215, 175, 65)
            strokeWidth = 3f
        }
        canvas.drawLine(350f, 60f, w - 350f, 60f, dividerPaint)
        canvas.drawLine(350f, h - 60f, w - 350f, h - 60f, dividerPaint)

        // Center small decorative diamond on top and bottom
        drawCornerDiamond(canvas, w / 2f, 60f, 10f, cornerPaint)
        drawCornerDiamond(canvas, w / 2f, h - 60f, 10f, cornerPaint)
    }

    private fun drawRoyalBlue(canvas: Canvas) {
        val w = BANNER_WIDTH.toFloat()
        val h = BANNER_HEIGHT.toFloat()

        // Pure white background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Top surgical royal blue bar
        val blueBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, w, 0f,
                Color.rgb(13, 71, 161),
                Color.rgb(25, 118, 210),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, 32f, blueBarPaint)

        // Bottom royal blue bar
        canvas.drawRect(0f, h - 28f, w, h, blueBarPaint)

        // Left sidebar curve
        val sidePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(260f, 0f)
            lineTo(210f, h)
            lineTo(0f, h)
            close()
        }
        val sidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(13, 71, 161)
        }
        canvas.drawPath(sidePath, sidePaint)

        // Draw Caduceus in white on the blue sidebar
        drawCaduceusIcon(canvas, 115f, h / 2f, 110f, Color.WHITE)

        // Right side medical cross and watermark
        val rightCrossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(227, 242, 253)
        }
        drawMedicalCross(canvas, w - 160f, h / 2f, 130f, 44f, rightCrossPaint)

        val rightCrossAccent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(25, 118, 210)
        }
        drawMedicalCross(canvas, w - 160f, h / 2f, 75f, 24f, rightCrossAccent)

        // Subtly framed container for doctor text
        val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(200, 220, 245)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(RectF(290f, 50f, w - 270f, h - 45f), 16f, 16f, framePaint)
    }

    private fun drawGreenCrescent(canvas: Canvas) {
        val w = BANNER_WIDTH.toFloat()
        val h = BANNER_HEIGHT.toFloat()

        // Soft pale green background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(248, 253, 250)
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Emerald borders
        val greenBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 137, 123)
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawRect(20f, 20f, w - 20f, h - 20f, greenBorder)

        val thinGreen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(77, 182, 172)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(32f, 32f, w - 32f, h - 32f, thinGreen)

        // Left Seal: Green Crescent
        val sealX = 180f
        val sealY = h / 2f
        val sealPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 245, 242)
        }
        canvas.drawCircle(sealX, sealY, 130f, sealPaint)
        canvas.drawCircle(sealX, sealY, 130f, greenBorder)
        drawCrescentCross(canvas, sealX, sealY, 85f, Color.rgb(0, 137, 123))

        // Right Seal: Pharmacy Bowl with Snake (Bowl of Hygieia)
        val rightX = w - 180f
        val rightY = h / 2f
        canvas.drawCircle(rightX, rightY, 130f, sealPaint)
        canvas.drawCircle(rightX, rightY, 130f, greenBorder)
        drawCaduceusIcon(canvas, rightX, rightY, 95f, Color.rgb(0, 137, 123))

        // Decorative horizontal header bar
        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 137, 123)
            strokeWidth = 4f
        }
        canvas.drawLine(340f, 65f, w - 340f, 65f, barPaint)
        canvas.drawLine(340f, h - 65f, w - 340f, h - 65f, barPaint)
    }

    private fun drawClinicalModern(canvas: Canvas) {
        val w = BANNER_WIDTH.toFloat()
        val h = BANNER_HEIGHT.toFloat()

        // Pure crisp background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 255, 255)
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Gradient top line
        val topGradient = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, w, 0f,
                Color.rgb(33, 150, 243),
                Color.rgb(0, 188, 212),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, 14f, topGradient)

        // ECG Heartbeat line across bottom
        val ecgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 188, 212)
            strokeWidth = 4f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val ecgPath = Path().apply {
            val y = h - 40f
            moveTo(50f, y)
            lineTo(300f, y)
            // pulse 1
            lineTo(330f, y - 25f)
            lineTo(350f, y + 20f)
            lineTo(370f, y - 60f)
            lineTo(390f, y + 40f)
            lineTo(410f, y)
            lineTo(w - 410f, y)
            // pulse 2
            lineTo(w - 390f, y - 60f)
            lineTo(w - 370f, y + 40f)
            lineTo(w - 350f, y - 25f)
            lineTo(w - 330f, y)
            lineTo(w - 50f, y)
        }
        canvas.drawPath(ecgPath, ecgPaint)

        // Left side modern hexagon shield
        val hexX = 170f
        val hexY = h / 2f - 15f
        val hexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(240, 249, 255)
        }
        canvas.drawCircle(hexX, hexY, 120f, hexPaint)

        val hexStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(33, 150, 243)
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        canvas.drawCircle(hexX, hexY, 120f, hexStroke)
        drawCaduceusIcon(canvas, hexX, hexY, 90f, Color.rgb(33, 150, 243))

        // Right modern cross
        drawMedicalCross(canvas, w - 170f, hexY, 90f, 30f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 188, 212)
        })
    }

    private fun drawVintageFiligree(canvas: Canvas) {
        val w = BANNER_WIDTH.toFloat()
        val h = BANNER_HEIGHT.toFloat()

        // Aged parchment
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(252, 248, 238)
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Vintage double border
        val vintagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(78, 52, 46) // Deep vintage mahogany
            style = Paint.Style.STROKE
            strokeWidth = 7f
        }
        canvas.drawRect(28f, 28f, w - 28f, h - 28f, vintagePaint)

        val thinVintage = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(121, 85, 72)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(42f, 42f, w - 42f, h - 42f, thinVintage)

        // Left Seal with Caduceus
        val sealX = 180f
        val sealY = h / 2f
        val sealFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(245, 238, 224)
        }
        canvas.drawCircle(sealX, sealY, 125f, sealFill)
        canvas.drawCircle(sealX, sealY, 125f, vintagePaint)
        drawCaduceusIcon(canvas, sealX, sealY, 95f, Color.rgb(78, 52, 46))

        // Right side official medical star
        val rightX = w - 180f
        val rightY = h / 2f
        canvas.drawCircle(rightX, rightY, 125f, sealFill)
        canvas.drawCircle(rightX, rightY, 125f, vintagePaint)
        drawCrescentCross(canvas, rightX, rightY, 85f, Color.rgb(78, 52, 46))

        // Filigree ornamental lines
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(121, 85, 72)
            strokeWidth = 3f
        }
        canvas.drawLine(350f, 55f, w - 350f, 55f, linePaint)
        canvas.drawLine(350f, h - 55f, w - 350f, h - 55f, linePaint)
    }

    // Helper: Draw a medical Caduceus (Staff of Asclepius & Snake)
    private fun drawCaduceusIcon(canvas: Canvas, cx: Float, cy: Float, size: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = size * 0.08f
            strokeCap = Paint.Cap.ROUND
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }

        // Center vertical staff
        canvas.drawLine(cx, cy - size * 0.7f, cx, cy + size * 0.75f, paint)

        // Top knob of staff
        canvas.drawCircle(cx, cy - size * 0.72f, size * 0.12f, fillPaint)

        // Snake coils around staff
        val snakePath = Path().apply {
            moveTo(cx, cy + size * 0.65f)
            cubicTo(
                cx - size * 0.45f, cy + size * 0.45f,
                cx + size * 0.45f, cy + size * 0.15f,
                cx, cy - size * 0.05f
            )
            cubicTo(
                cx - size * 0.5f, cy - size * 0.25f,
                cx + size * 0.4f, cy - size * 0.5f,
                cx - size * 0.15f, cy - size * 0.62f
            )
        }
        canvas.drawPath(snakePath, paint)

        // Snake head
        canvas.drawCircle(cx - size * 0.18f, cy - size * 0.63f, size * 0.08f, fillPaint)
    }

    // Helper: Draw Crescent and Medical Cross
    private fun drawCrescentCross(canvas: Canvas, cx: Float, cy: Float, size: Float, color: Int) {
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }

        // Crescent moon path
        val crescentPath = Path().apply {
            addCircle(cx - size * 0.15f, cy, size * 0.6f, Path.Direction.CW)
            val innerCircle = Path().apply {
                addCircle(cx + size * 0.05f, cy, size * 0.52f, Path.Direction.CW)
            }
            op(innerCircle, Path.Op.DIFFERENCE)
        }
        canvas.drawPath(crescentPath, fillPaint)

        // Medical cross next to crescent
        drawMedicalCross(canvas, cx + size * 0.22f, cy, size * 0.55f, size * 0.18f, fillPaint)
    }

    // Helper: Draw Medical Cross
    private fun drawMedicalCross(canvas: Canvas, cx: Float, cy: Float, crossSize: Float, thickness: Float, paint: Paint) {
        val halfS = crossSize / 2f
        val halfT = thickness / 2f

        // Horizontal bar
        canvas.drawRect(cx - halfS, cy - halfT, cx + halfS, cy + halfT, paint)
        // Vertical bar
        canvas.drawRect(cx - halfT, cy - halfS, cx + halfT, cy + halfS, paint)
    }

    private fun drawCornerDiamond(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val path = Path().apply {
            moveTo(cx, cy - size)
            lineTo(cx + size, cy)
            lineTo(cx, cy + size)
            lineTo(cx - size, cy)
            close()
        }
        canvas.drawPath(path, paint)
    }
}
