package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "AiBannerService"

class AiBannerService {

    // Lazy initialization of the generative model with IMAGE response modality
    private val model by lazy {
        val config = generationConfig {
            responseModalities = listOf(ResponseModality.IMAGE)
        }
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = config
        )
    }

    suspend fun generateMedicalBanner(stylePrompt: String): Result<Bitmap> {
        return try {
            val fullPrompt = buildString {
                append("Create a wide horizontal medical prescription header banner (bande santé) in 16:9 ratio. ")
                append("Design style: ")
                append(stylePrompt)
                append(". Features: elegant medical borders, official caduceus emblem, snake and staff motif, clean bright center space suitable for doctor name typography, professional clinic aesthetic, sharp high detail, no pre-printed placeholder names.")
            }

            // 60-second timeout mandated for image generation
            val bitmap = withTimeout(60_000L) {
                val response = model.generateContent(fullPrompt)
                val imagePart = response.candidates.firstOrNull()
                    ?.content?.parts?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()
                imagePart?.image
            }

            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(IllegalStateException("لم يتم استلام صورة من نموذج الذكاء الاصطناعي."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Image generation timed out", e)
            Result.failure(IllegalStateException("استغرقت عملية التوليد وقتاً أطول من المتوقع (أكثر من 60 ثانية). يرجى المحاولة مجدداً."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI generation failed", e)
            Result.failure(e)
        }
    }
}
