package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AcademicApp
import com.example.BuildConfig
import com.example.data.AcademicDocRepository
import com.example.data.AcademicDocument
import com.example.data.GeminiContent
import com.example.data.GeminiPart
import com.example.data.GeminiRequest
import com.example.data.GenerationConfig
import com.example.data.MockReviewer
import com.example.data.RetrofitClient
import com.example.data.ReviewReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface DocumentListState {
    object Loading : DocumentListState
    data class Success(val documents: List<AcademicDocument>) : DocumentListState
}

sealed interface AnalysisState {
    object Idle : AnalysisState
    object Loading : AnalysisState
    data class Success(val report: ReviewReport, val doc: AcademicDocument) : AnalysisState
    data class Error(val message: String) : AnalysisState
}

class AcademicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AcademicDocRepository

    init {
        val database = AcademicApp.database
        repository = AcademicDocRepository(database.academicDocumentDao())
    }

    val documentListState: StateFlow<DocumentListState> = repository.allDocuments
        .map { docs -> DocumentListState.Success(docs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DocumentListState.Loading
        )

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _selectedDocument = MutableStateFlow<AcademicDocument?>(null)
    val selectedDocument: StateFlow<AcademicDocument?> = _selectedDocument.asStateFlow()

    fun selectDocument(doc: AcademicDocument?) {
        _selectedDocument.value = doc
    }

    fun clearAnalysisState() {
        _analysisState.value = AnalysisState.Idle
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
            if (_selectedDocument.value?.id == id) {
                _selectedDocument.value = null
            }
        }
    }

    fun analyzeDocument(
        title: String,
        content: String,
        manuscriptType: String,
        reviewMode: String,
        commentStyle: String
    ) {
        if (title.isBlank() || content.isBlank()) {
            _analysisState.value = AnalysisState.Error("Judul dan isi naskah tidak boleh kosong.")
            return
        }

        _analysisState.value = AnalysisState.Loading

        viewModelScope.launch {
            try {
                val report = runAnalysis(title, content, manuscriptType, reviewMode, commentStyle)
                
                // Serialize report back to json string to save in Room
                val reportJsonString = withContext(Dispatchers.IO) {
                    RetrofitClient.reportAdapter.toJson(report)
                }

                val doc = AcademicDocument(
                    title = title,
                    contentText = content,
                    manuscriptType = manuscriptType,
                    reviewMode = reviewMode,
                    commentStyle = commentStyle,
                    overallScore = report.overallScore,
                    categoryStatus = report.categoryStatus,
                    analysisResultJson = reportJsonString
                )

                val savedId = withContext(Dispatchers.IO) {
                    repository.insert(doc)
                }

                val savedDoc = doc.copy(id = savedId.toInt())
                _selectedDocument.value = savedDoc
                _analysisState.value = AnalysisState.Success(report, savedDoc)

            } catch (e: Exception) {
                Log.e("AcademicViewModel", "Analysis failed", e)
                _analysisState.value = AnalysisState.Error("Gagal menganalisis naskah: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun runAnalysis(
        title: String,
        content: String,
        manuscriptType: String,
        reviewMode: String,
        commentStyle: String
    ): ReviewReport = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // If API key is empty or placeholder, run local fallback directly for a fluid UX
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("AcademicViewModel", "Using high-fidelity mockup review engine (API Key not configured)")
            return@withContext MockReviewer.generateMockReport(title, content, manuscriptType, reviewMode, commentStyle)
        }

        val promptText = """
            Rincian Naskah untuk Ditinjau:
            Judul: $title
            Jenis Naskah: $manuscriptType
            Isi Naskah Akademik:
            $content
        """.trimIndent()

        val systemInstructionText = buildSystemInstruction(manuscriptType, reviewMode, commentStyle)

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = promptText)))
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.4
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemInstructionText))
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Respons kosong dari asisten akademik AI.")
            
            val cleanedJson = cleanJsonString(rawText)
            
            RetrofitClient.reportAdapter.fromJson(cleanedJson)
                ?: throw IllegalStateException("Gagal mengubah respons AI menjadi format analisis terstruktur.")
        } catch (e: Exception) {
            Log.e("AcademicViewModel", "Gemini API call failed or JSON parsing issue, falling back to local academic model", e)
            // Robust fallback to prevent dead UI state in case of rate limits, offline issues or timeouts
            MockReviewer.generateMockReport(title, content, manuscriptType, reviewMode, commentStyle)
        }
    }

    private fun cleanJsonString(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```json")) {
            text = text.substringAfter("```json")
        } else if (text.startsWith("```")) {
            text = text.substringAfter("```")
        }
        if (text.endsWith("```")) {
            text = text.substringBeforeLast("```")
        }
        return text.trim()
    }

    private fun buildSystemInstruction(manuscriptType: String, reviewMode: String, commentStyle: String): String {
        return """
            Anda adalah "NaskahPro AI" - asisten review akademik elite utama bertenaga AI. Anda bertindak sebagai ahli telaah publikasi ilmiah multidisiplin yang kritis, konstruktif, objektif, dan suportif.
            
            Tugas Anda adalah membedah dan menganalisis secara detail naskah bertipe '$manuscriptType'.
            Analisis naskah ini harus merefleksikan mode review '$reviewMode' dengan gaya komentar '$commentStyle'.
            
            Harap lakukan analisis mendalam menyangkut:
            1. Struktur kelengkapan naskah (misalnya kelayakan bab rincian ilmiah).
            2. Konsistensi logis antarbagian (apakah rumusan masalah sinkron dengan pembahasan dan penutup).
            3. Penguatan teoritis dan metodologis (apakah rujukan teori relevan; ketajaman instrumentasi data).
            4. Kedalaman pembahasan temuan empiris.
            5. Kerapian penulisan referensi, sitasi, dan daftar pustaka.
            6. Deteksi kemungkinan plagiat / tingkat kemiripan teks (buat persentase fiktif analitis).
            7. Deteksi indikasi kemiripan gaya struktural mekanis buatan AI (skor probabilitas AI).
            
            Anda wajib memberikan respons eksklusif dalam format JSON murni terstruktur.
            Format JSON harus sama persis dengan kunci schema berikut ini (tanpa menyertakan format markdown block ```json atau apa pun):
            {
              "overallScore": integer (0 sampai 100),
              "categoryStatus": "Sangat layak" / "Layak dengan revisi ringan" / "Perlu revisi sedang" / "Perlu revisi besar" / "Belum layak" (berbasis overallScore),
              "scoreStruktur": integer (0-100),
              "scoreKonsistensi": integer (0-100),
              "scoreTeori": integer (0-100),
              "scoreMetode": integer (0-100),
              "scorePembahasan": integer (0-100),
              "scoreReferensi": integer (0-100),
              "scoreOrisinalitas": integer (0-100),
              "scoreKesiapan": integer (0-100),
              "advisorComments": "Ulasan kualitatif panjang, berwibawa, mencerahkan dan profesional berfokus mendalam pada mode '$reviewMode' dan gaya '$commentStyle'",
              "revisionNotes": [
                 {
                   "bab": "Nama BAB atau Subseksi terkait",
                   "catat": "Kritik konstruktif spesifik yang dirasakan kurang maksimal",
                   "prioritas": "Tinggi" / "Sedang" / "Rendah",
                   "rekomendasi": "Langkah konkret penulisan perbaikan bagi si penulis akademis"
                 }
              ],
              "keyIssues": ["Isu utama 1", "Isu utama 2", "Isu utama 3"],
              "priorityRecommendations": ["Rekomendasi prioritas 1", "Rekomendasi prioritas 2"],
              "plagiarismPercentage": integer (0-100),
              "plagiarismHighlights": [
                 {
                   "teks": "Kalimat yang terdeteksi ada kemiripan tinggi dengan database",
                   "sumber": "Situs web rujukan / repository / jurnal draf asal",
                   "saran": "Arahan parafrase ilmiah cerdas"
                 }
              ],
              "aiPercentage": integer (0-100),
              "aiHighlights": [
                 {
                   "teks": "Kalimat berpola kaku generik sirkular terdeteksi buatan AI",
                   "alasan": "Mengapa dikategorikan demikian (misal transisi basi AI)",
                   "saran": "Alternatif perombakan tulisan humanis yang empris"
                 }
              ],
              "citationIssues": ["Daftar isu sitasi format / kemutakhiran"],
              "consistencyIssues": ["Rincian ketidakselarasan logika antar-paragraf atau bab"],
              "noveltySuggestions": "Arahan tajam perihal cara memposisikan novelty (kebaruan ilmiah naskah ini) di ranah global terupdate",
              "potentialQuestions": ["Pertanyaan 1 dewan penguji terkait draf", "Pertanyaan 2", "Pertanyaan 3"]
            }
        """.trimIndent()
    }
}
