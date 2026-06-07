package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Double? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

// --- Domain Models for review result ---

@JsonClass(generateAdapter = true)
data class ReviewReport(
    val overallScore: Int,
    val categoryStatus: String,
    val scoreStruktur: Int,
    val scoreKonsistensi: Int,
    val scoreTeori: Int,
    val scoreMetode: Int,
    val scorePembahasan: Int,
    val scoreReferensi: Int,
    val scoreOrisinalitas: Int,
    val scoreKesiapan: Int,
    val advisorComments: String,
    val revisionNotes: List<ReviewRevisionNote>,
    val keyIssues: List<String>,
    val priorityRecommendations: List<String>,
    val plagiarismPercentage: Int,
    val plagiarismHighlights: List<PlagiarismHighlight>,
    val aiPercentage: Int,
    val aiHighlights: List<AiHighlight>,
    val citationIssues: List<String>,
    val consistencyIssues: List<String>,
    val noveltySuggestions: String,
    val potentialQuestions: List<String>
)

@JsonClass(generateAdapter = true)
data class ReviewRevisionNote(
    val bab: String,
    val catat: String,
    val prioritas: String, // "Tinggi", "Sedang", "Rendah"
    val rekomendasi: String
)

@JsonClass(generateAdapter = true)
data class PlagiarismHighlight(
    val teks: String,
    val sumber: String,
    val saran: String
)

@JsonClass(generateAdapter = true)
data class AiHighlight(
    val teks: String,
    val alasan: String,
    val saran: String
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    val reportAdapter = moshi.adapter(ReviewReport::class.java)
}
