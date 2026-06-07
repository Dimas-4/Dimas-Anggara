package com.example.data

import kotlin.random.Random

object MockReviewer {

    fun generateMockReport(
        title: String,
        content: String,
        manuscriptType: String,
        reviewMode: String,
        commentStyle: String
    ): ReviewReport {
        // Dynamic score generation with logical variation based on comment style
        val originalTitle = if (title.isBlank()) "Analisis Sistem Pengukuran Kinerja Akademik" else title
        val titleWords = originalTitle.split(" ")
        val mainSubject = if (titleWords.size > 2) titleWords.subList(titleWords.size - 2, titleWords.size).joinToString(" ") else "Naskah Penelitian"

        val penalty = when (commentStyle) {
            "Sangat kritis" -> 15
            "Detail" -> 5
            else -> 0
        }

        val scoreStruktur = (80..95).random() - penalty
        val scoreKonsistensi = (70..90).random() - penalty
        val scoreTeori = (65..88).random() - penalty
        val scoreMetode = (72..92).random() - penalty
        val scorePembahasan = (60..85).random() - penalty
        val scoreReferensi = (68..88).random() - penalty
        val scoreOrisinalitas = (75..95).random() - (if (content.length < 500) 10 else 0)
        val scoreKesiapan = (scoreStruktur + scoreKonsistensi + scoreMetode + scorePembahasan) / 4

        val overallScore = (scoreStruktur + scoreKonsistensi + scoreTeori + scoreMetode + scorePembahasan + scoreReferensi + scoreOrisinalitas + scoreKesiapan) / 8

        val categoryStatus = when {
            overallScore >= 86 -> "Sangat layak"
            overallScore >= 76 -> "Layak dengan revisi ringan"
            overallScore >= 61 -> "Perlu revisi sedang"
            overallScore >= 41 -> "Perlu revisi besar"
            else -> "Belum layak"
        }

        // Custom comments based on Mode
        val advisorComments = when (reviewMode) {
            "Dosen Pembimbing" -> {
                "Secara keseluruhan, draf $manuscriptType Anda mengenai '$originalTitle' menunjukkan potensi akademis yang baik. Namun, ada beberapa kelemahan substantif di bagian Pembahasan. Anda hanya menyajikan data statistik tanpa mengaitkannya secara mendalam dengan teori rujukan utama. Tolong ikuti petunjuk perbaikan bab demi bab di bawah untuk bimbingan berikutnya."
            }
            "Penguji Sidang" -> {
                "Draf ini masih menyisakan celah argumen yang cukup besar, terutama pada justifikasi pemilihan metode penelitian ini dibanding metode alternatif. Model analisis teori yang digunakan kurang kuat melandasi operasionalisasi variabel. Bersiaplah menjelaskan argumen ini secara rasional saat dewan penguji menguji metodologi Anda di persidangan."
            }
            "Reviewer Jurnal" -> {
                "This paper addresses an interesting topic related to $mainSubject. However, the theoretical contribution remains vague. The literature review needs updating—some references date back over 10 years. Additionally, the Novelty (keterbaruan) section is weak and fails to highlight the unique research gap this study seeks to address. Risk of desk rejection is moderate unless drastic improvements are made to the Introduction and Discussion sections."
            }
            "Editor Buku" -> {
                "Naskah chapter book ini memiliki kedalaman isi yang memadai, namun gaya penulisannya masih terlalu kaku dan menyerupai format laporan tesis. Alur antar subbab harus dibuat lebih mengalir (fluid) dan ramah pembunyi. Hilangkan subbab bernomor kaku (seperti 1.1, 1.1.1) dan ganti dengan heading naratif yang kontekstual."
            }
            else -> { // Scopus
                "The manuscript displays a solid empirical foundation but misses the global context. The scope is overly localized and doesn't clearly articulate the international gap (international significance). You must integrate recent global literature (2022-2026) in high-impact journals to position this work effectively for Scopus-indexed venues."
            }
        }

        val revisionNotes = listOf(
            ReviewRevisionNote(
                bab = if (manuscriptType == "Artikel jurnal" || manuscriptType == "Chapter book") "Abstract / Abstrak" else "BAB I (Pendahuluan)",
                catat = "Latar belakang terlalu bertele-tele dan belum secara tegas merumuskan fenomena kesenjangan (research gap) penelitian.",
                prioritas = "Tinggi",
                rekomendasi = "Tuliskan dalam 1-2 paragraf akhir tentang apa keunikan naskah ini dibandingkan 3 penelitian utama terdahulu secara presisi."
            ),
            ReviewRevisionNote(
                bab = if (manuscriptType == "Artikel jurnal" || manuscriptType == "Chapter book") "Literature Review" else "BAB II (Tinjauan Pustaka)",
                catat = "Rujukan teori utama kurang mutakhir. Beberapa konsep klasik disitasi dari sumber sekunder (buku rangkuman, bukan jurnal primer asli).",
                prioritas = "Sedang",
                rekomendasi = "Gunakan rujukan jurnal bereputasi 5 tahun terakhir untuk mendefinisikan operasionalisasi teori utama."
            ),
            ReviewRevisionNote(
                bab = if (manuscriptType == "Artikel jurnal" || manuscriptType == "Chapter book") "Method" else "BAB III (Metodologi)",
                catat = "Penjelasan teknik pengambilan sampel dan ukuran sampel minimum tidak disertai rumus pendukung (misalnya Slovin atau Hair).",
                prioritas = "Tinggi",
                rekomendasi = "Tambahkan justifikasi ilmiah di paragraf metodologi mengenai ukuran sampel representatif."
            ),
            ReviewRevisionNote(
                bab = if (manuscriptType == "Artikel jurnal" || manuscriptType == "Chapter book") "Results & Discussion" else "BAB IV (Hasil & Pembahasan)",
                catat = "Bagian pembahasan didominasi penulisan ulang angka tabel. Belum terlihat diskusi kritis mengapa temuan tersebut bisa searah atau bertentangan dengan pakar terdahulu.",
                prioritas = "Tinggi",
                rekomendasi = "Terapkan formula 3D (Describe, Discuss, Distinguish) saat menyusun narasi interpretasi hasil data."
            ),
            ReviewRevisionNote(
                bab = if (manuscriptType == "Artikel jurnal" || manuscriptType == "Chapter book") "Conclusion" else "BAB V (Penutup)",
                catat = "Saran penelitian terlalu klise dan normatif (misal: 'diharapkan bermanfaat bagi masyarakat'), tidak operasional bagi peneliti selanjutnya.",
                prioritas = "Rendah",
                rekomendasi = "Berikan keterbatasan studi empiris Anda yang konkret sebagai saran eksplorasi riset masa depan."
            )
        )

        val keyIssues = listOf(
            "Rumusan masalah tidak sepenuhnya sejalan dengan hipotesis/fokus kajian di pembahasan.",
            "Metode analisis data belum dijelaskan secara mendetail untuk mempermudah replikasi penelitian.",
            "Sebanyak 35% sitasi dalam teks menggunakan format yang salah atau tidak terdaftar di Daftar Pustaka."
        )

        val priorityRecommendations = listOf(
            "Perjelas Novelty atau keterbaruan ilmiah di paragraf terakhir bagian Pendahuluan.",
            "Lakukan restrukturisasi pembahasan dengan mengolaborasikan teori-teori dari Bab II.",
            "Perbarui referensi ke artikel jurnal mutakhir terbitan 2021-2026."
        )

        // Simulated text similarity analysis
        val plagiarismPercentage = if (content.length > 300) Random.nextInt(12, 28) else Random.nextInt(5, 15)
        val plagiarismHighlights = listOf(
            PlagiarismHighlight(
                teks = "tujuan utama dari penelitian ini adalah untuk mengetahui pengaruh signifikan antar variabel independen terhadap variabel dependen",
                sumber = "Repository Universitas Indonesia, 2021",
                saran = "Parafrase menjadi: 'Studi ini difokuskan guna menginvestigasi dampak yang ditimbulkan oleh faktor-faktor internal terhadap outcome utama...'"
            ),
            PlagiarismHighlight(
                teks = "metodologi deskriptif kuantitatif dengan pendekatan asosiatif menggunakan kuesioner berskala likert lima poin",
                sumber = "Jurnal Psikologi & Manajemen Nusantara, 2023",
                saran = "Sesuaikan kalimat menjadi: 'Pendekatan yang diterapkan berupa desain asosiatif kuantitatif, menggunakan kuesioner berskala Likert 1-5.'"
            )
        )

        // Simulated AI writing detector
        val aiPercentage = if (content.contains("Namun, ") || content.contains("Selain itu, ") || content.contains("Oleh karena itu, ")) Random.nextInt(18, 48) else Random.nextInt(5, 20)
        val aiHighlights = listOf(
            AiHighlight(
                teks = "Berdasarkan pemaparan di atas, penting untuk diingat bahwa di era modern saat ini perkembangan sangat pesat...",
                alasan = "Struktur mekanis monoton menggunakan frasa transisi generik ('penting untuk diingat', 'di era modern') khas generator teks AI.",
                saran = "Ubah menjadi narasi langsung berbasis data riil lapangan: 'Dampak nyata di sektor pendidikan terpantau dari adanya fluktuasi indeks...'"
            ),
            AiHighlight(
                teks = "Hal ini menunjukkan bahwa korelasi vertikal berperan krusial dalam menunjang ekosistem yang terintegrasi secara harmonis.",
                alasan = "Pilihan diksi terlalu bombastis ('berperan krusial', 'secara harmonis') tanpa menyajikan referensi rujukan ilmiah konkret.",
                saran = "Sederhanakan kalimat: 'Dampak korelasi ini mendorong sinergi operasional antarunit kerja (Pratama, 2022).'"
            )
        )

        val citationIssues = listOf(
            "Cek Sitasi: 'Sudarsono (2012)' di halaman pendahuluan tergolong usang (>10 tahun). Disarankan mencari literatur alternatif terbitan di atas 2020.",
            "Ketidaksesuaian: Nama 'Prasetyo dkk. (2019)' tertera pada Bab III naskah, tetapi tidak ditemukan di dalam Daftar Pustaka.",
            "Format Referensi: Entri ke-3 di Daftar Pustaka belum melampirkan tautan DOI atau link digital penelusuran artikel."
        )

        val consistencyIssues = listOf(
            "Rumusan masalah nomor 2 ('Apakah terdapat pengaruh asimetri informasi?') belum dijawab secara eksplisit pada bagian kesimpulan naskah.",
            "Terdapat inkonsistensi penulisan istilah: penulisan 'corporate governance' di Bab I, beralih menjadi 'tata kelola perusahaan' di Bab III. Pilih salah satu istilah agar konsisten."
        )

        val noveltySuggestions = "Untuk meningkatkan Novelty naskah riset '$originalTitle', Anda perlu menegaskan kontribusi teoretisnya. Alih-alih hanya mengulang pengujian variabel yang sudah mapan, jelaskan bagaimana konteks objek spesifik penelitian Anda memodifikasi pemahaman konsep teoretis yang sudah ada."

        val potentialQuestions = listOf(
            "Mengapa Anda memilih pendekatan teoretis ini dibanding teori alternatif yang juga relevan?",
            "Bagaimana Anda menjamin validitas dan reliabilitas instrumen kuesioner sebelum instrumen disebarluaskan?",
            "Apa implikasi manajerial konkret dari hasil penelitian Anda bagi pemangku kebijakan?"
        )

        return ReviewReport(
            overallScore = overallScore,
            categoryStatus = categoryStatus,
            scoreStruktur = scoreStruktur,
            scoreKonsistensi = scoreKonsistensi,
            scoreTeori = scoreTeori,
            scoreMetode = scoreMetode,
            scorePembahasan = scorePembahasan,
            scoreReferensi = scoreReferensi,
            scoreOrisinalitas = scoreOrisinalitas,
            scoreKesiapan = scoreKesiapan,
            advisorComments = advisorComments,
            revisionNotes = revisionNotes,
            keyIssues = keyIssues,
            priorityRecommendations = priorityRecommendations,
            plagiarismPercentage = plagiarismPercentage,
            plagiarismHighlights = plagiarismHighlights,
            aiPercentage = aiPercentage,
            aiHighlights = aiHighlights,
            citationIssues = citationIssues,
            consistencyIssues = consistencyIssues,
            noveltySuggestions = noveltySuggestions,
            potentialQuestions = potentialQuestions
        )
    }
}
