package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademicDocument
import com.example.data.AiHighlight
import com.example.data.PlagiarismHighlight
import com.example.data.RetrofitClient
import com.example.data.ReviewReport
import com.example.data.ReviewRevisionNote
import com.example.ui.AcademicViewModel
import com.example.ui.AnalysisState
import com.example.ui.DocumentListState
import com.example.ui.theme.ColorBelumLayak
import com.example.ui.theme.ColorLayakRevisiRingan
import com.example.ui.theme.ColorRevisiBesar
import com.example.ui.theme.ColorRevisiSedang
import com.example.ui.theme.ColorSangatLayak
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.LightBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SoftGray
import com.example.ui.theme.WhitePure

// Define the Screens of NaskahPro AI
enum class AppScreen {
    Landing,
    Dashboard,
    Upload,
    Result
}

class MainActivity : ComponentActivity() {
    private val viewModel: AcademicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NaskahProApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NaskahProApp(viewModel: AcademicViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.Landing) }
    
    // Track selected navigation tab: 0 for Home, 1 for Histori, 2 for Panduan/Tentang
    var bottomNavTab by remember { mutableStateOf(0) }
    
    val selectedDoc by viewModel.selectedDocument.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()

    // Automatic redirection logic based on state change
    LaunchedEffect(analysisState) {
        if (analysisState is AnalysisState.Success) {
            currentScreen = AppScreen.Result
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen != AppScreen.Landing) {
                NavigationBar(
                    containerColor = WhitePure,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .border(1.dp, SoftGray.copy(alpha = 0.5f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    NavigationBarItem(
                        selected = bottomNavTab == 0 && currentScreen != AppScreen.Result,
                        onClick = {
                            bottomNavTab = 0
                            viewModel.clearAnalysisState()
                            currentScreen = AppScreen.Dashboard
                        },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Home, 
                                contentDescription = "Beranda"
                            ) 
                        },
                        label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            unselectedIconColor = DarkText.copy(alpha = 0.4f),
                            unselectedTextColor = DarkText.copy(alpha = 0.4f),
                            indicatorColor = NavyPrimary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = bottomNavTab == 1,
                        onClick = {
                            bottomNavTab = 1
                            viewModel.clearAnalysisState()
                            currentScreen = AppScreen.Dashboard
                        },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Refresh, 
                                contentDescription = "Histori"
                            ) 
                        },
                        label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            unselectedIconColor = DarkText.copy(alpha = 0.4f),
                            unselectedTextColor = DarkText.copy(alpha = 0.4f),
                            indicatorColor = NavyPrimary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = bottomNavTab == 2,
                        onClick = {
                            bottomNavTab = 2
                        },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Info, 
                                contentDescription = "Tentang"
                            ) 
                        },
                        label = { Text("Info Layanan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            unselectedIconColor = DarkText.copy(alpha = 0.4f),
                            unselectedTextColor = DarkText.copy(alpha = 0.4f),
                            indicatorColor = NavyPrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightBg)
        ) {
            when {
                bottomNavTab == 2 -> {
                    PanduanLayananScreen(onBack = { bottomNavTab = 0 })
                }
                currentScreen == AppScreen.Landing -> {
                    LandingScreen(
                        onNavigateToDashboard = {
                            currentScreen = AppScreen.Dashboard
                            bottomNavTab = 0
                        },
                        onNavigateToUpload = {
                            currentScreen = AppScreen.Upload
                            bottomNavTab = 0
                        }
                    )
                }
                currentScreen == AppScreen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        initiallyShowHistoryOnly = bottomNavTab == 1,
                        onNavigateToUpload = { currentScreen = AppScreen.Upload },
                        onNavigateToResult = { currentScreen = AppScreen.Result }
                    )
                }
                currentScreen == AppScreen.Upload -> {
                    UploadScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Dashboard }
                    )
                }
                currentScreen == AppScreen.Result -> {
                    ResultScreen(
                        viewModel = viewModel,
                        onBack = {
                            viewModel.clearAnalysisState()
                            currentScreen = AppScreen.Dashboard
                        }
                    )
                }
            }
        }
    }
}

// ------------------- SCREEN 1: LANDING SCREEN -------------------

@Composable
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToUpload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(NavyDark, NavyPrimary)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Brand Identity Box
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(GoldAccent.copy(alpha = 0.15f))
                .border(2.dp, GoldAccent, CircleShape)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Academic Mortarboard Logo",
                tint = GoldAccent,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title and Tagline
        Text(
            text = "NaskahPro AI",
            color = WhitePure,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("app_title")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Asisten review akademik untuk skripsi, artikel jurnal, dan chapter book.",
            color = GoldLight,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .testTag("app_tagline")
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Promotional Headline Card
        Card(
            colors = CardDefaults.cardColors(containerColor = WhitePure.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WhitePure.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bimbingan Lebih Siap, Jurnal Anti-Reject!",
                    color = WhitePure,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Periksa kelayakan naskah ilmiah Anda secara komprehensif mulai dari kelengkapan bab, sinkronisasi logis, daftar rujukan, kemiripan teks, hingga pola repetitif deteksi tulisan AI.",
                    color = SoftGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Large Action Buttons
        Button(
            onClick = onNavigateToUpload,
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(54.dp)
                .testTag("start_upload_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Analisis", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Mulai Upload Naskah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToDashboard,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WhitePure),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(54.dp)
                .testTag("dashboard_button"),
            border = BorderStroke(1.5.dp, GoldLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Dashboard", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Buka Dashboard Utama", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Features Highlight Area
        Text(
            text = "Jenis Naskah & Review",
            color = GoldLight,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeaturePill(icon = Icons.Filled.Star, text = "Skripsi/Tesis")
            FeaturePill(icon = Icons.Filled.Info, text = "Jurnal Paper")
            FeaturePill(icon = Icons.Filled.Settings, text = "Chapter Book")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun FeaturePill(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(WhitePure.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = GoldLight, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = text, color = WhitePure, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// ------------------- SCREEN 2: DASHBOARD SCREEN -------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: AcademicViewModel,
    initiallyShowHistoryOnly: Boolean,
    onNavigateToUpload: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val documentsState by viewModel.documentListState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    // Choose selected document card to proceed or delete
    var showDeleteConfirmDialog by remember { mutableStateOf<AcademicDocument?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(NavyPrimary)
                .padding(start = 24.dp, top = 28.dp, end = 24.dp, bottom = 28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NaskahPro ",
                                color = WhitePure,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "AI",
                                color = GoldAccent,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "PREMIUM REVIEW & ACADEMIC QUALITY",
                            color = GoldLight,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    
                    // Editorial Quick Action & Persona Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateToUpload,
                            modifier = Modifier
                                .background(GoldAccent, CircleShape)
                                .size(40.dp)
                                .testTag("dashboard_quick_add")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add, 
                                contentDescription = "Upload Baru", 
                                tint = NavyPrimary, 
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Persona Circle RA
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(WhitePure.copy(alpha = 0.12f))
                                .border(1.5.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RA",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Document Metadata summary row
                if (documentsState is DocumentListState.Success) {
                    val docs = (documentsState as DocumentListState.Success).documents
                    val totalDocs = docs.size
                    val avgScore = if (totalDocs > 0) docs.map { it.overallScore }.average().toInt() else 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryDashboardCard(
                            title = "TOTAL NASKAH",
                            valStr = totalDocs.toString(),
                            icon = Icons.Filled.Star,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryDashboardCard(
                            title = "RATA-RATA SKOR",
                            valStr = "$avgScore/100",
                            icon = Icons.Filled.Refresh,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Search Input Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Telusuri naskah / jenis naskah...", fontSize = 14.sp) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Cari", tint = DarkText.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Bersihkan", tint = DarkText, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_manuscripts_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = SoftGray,
                    focusedContainerColor = WhitePure,
                    unfocusedContainerColor = WhitePure
                ),
                singleLine = true
            )
        }

        // List Area
        when (documentsState) {
            is DocumentListState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyPrimary)
                }
            }
            is DocumentListState.Success -> {
                val allDocs = (documentsState as DocumentListState.Success).documents
                val filteredDocs = allDocs.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.manuscriptType.contains(searchQuery, ignoreCase = true) ||
                    it.reviewMode.contains(searchQuery, ignoreCase = true)
                }

                if (filteredDocs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Empty",
                            tint = SoftGray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (allDocs.isEmpty()) "Belum Ada Naskah Teranalisis" else "Tidak Ada Hasil Pencocokan",
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (allDocs.isEmpty()) "Analisis draf tulisan skripsi, jurnal, atau chapter book Anda sekarang menggunakan asisten AI." else "Coba cari dengan kata kunci judul atau jenis naskah lainnya.",
                            fontSize = 13.sp,
                            color = DarkText.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                        if (allDocs.isEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onNavigateToUpload,
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Upload Naskah Sekarang")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .testTag("manuscript_history_list"),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = if (initiallyShowHistoryOnly) "Riwayat Seluruh Dokumen" else "Aktivitas Terakhir",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(filteredDocs) { doc ->
                            DocumentRowCard(
                                doc = doc,
                                onClick = {
                                    viewModel.selectDocument(doc)
                                    onNavigateToResult()
                                },
                                onDelete = {
                                    showDeleteConfirmDialog = doc
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Delete Confirm
    showDeleteConfirmDialog?.let { docToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDocument(docToDelete.id)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorRevisiBesar)
                ) {
                    Text("Hapus", color = WhitePure)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Batal")
                }
            },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus arsip analisis naskah '${docToDelete.title}' dari database lokal?") }
        )
    }
}

@Composable
fun SummaryDashboardCard(
    title: String,
    valStr: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WhitePure.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(GoldAccent.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = SoftGray, fontSize = 11.sp)
                Text(text = valStr, color = WhitePure, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DocumentRowCard(
    doc: AcademicDocument,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when {
        doc.overallScore >= 86 -> ColorSangatLayak
        doc.overallScore >= 76 -> ColorLayakRevisiRingan
        doc.overallScore >= 61 -> ColorRevisiSedang
        doc.overallScore >= 41 -> ColorRevisiBesar
        else -> ColorBelumLayak
    }

    // Stable simulated sub-scores representing Editorial's structure, methods, and novelty metrics
    val structureScore = (doc.overallScore * 0.94f).coerceIn(15f, 100f).toInt()
    val methodScore = (doc.overallScore * 0.86f).coerceIn(15f, 100f).toInt()
    val noveltyScore = (doc.overallScore * 0.90f).coerceIn(15f, 100f).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, NavyPrimary.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .testTag("document_card_${doc.id}"),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Type Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(NavyPrimary.copy(alpha = 0.06f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = doc.manuscriptType.uppercase(),
                            color = NavyPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Document Title
                    Text(
                        text = doc.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText,
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Score Display (Editorial styled: large light font)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = doc.overallScore.toString(),
                        color = NavyPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = doc.categoryStatus.uppercase(),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete, 
                            contentDescription = "Hapus", 
                            tint = ColorRevisiBesar.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = SoftGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            // Micro-Data Visualization Row (Metrics: Struktur, Metode, Novelty)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STRUKTUR",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(SoftGray.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(structureScore / 100f)
                                .clip(CircleShape)
                                .background(GoldAccent)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "METODE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(SoftGray.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(methodScore / 100f)
                                .clip(CircleShape)
                                .background(NavyPrimary)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NOVELTY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(SoftGray.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(noveltyScore / 100f)
                                .clip(CircleShape)
                                .background(NavyPrimary)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sub-info: Mode & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Refresh, 
                        contentDescription = null, 
                        tint = DarkText.copy(alpha = 0.3f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mode: ${doc.reviewMode}",
                        fontSize = 11.sp,
                        color = DarkText.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

private fun readTextFromUri(context: android.content.Context, uri: Uri): String {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                val text = reader.readText()
                if (text.length > 300000) {
                    text.substring(0, 300000)
                } else {
                    text
                }
            }
        } ?: ""
    } catch (e: Exception) {
        "Gagal membaca file: ${e.localizedMessage}"
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    if (result != null && result.contains(".")) {
        result = result.substring(0, result.lastIndexOf('.'))
    }
    return result ?: "Naskah Unggahan"
}

private fun getFileExtension(context: android.content.Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    if (result != null && result.contains(".")) {
        return result.substring(result.lastIndexOf('.') + 1).lowercase()
    }
    return ""
}

private fun readTextFromPdf(rawBytes: ByteArray): String {
    return try {
        val reader = com.itextpdf.text.pdf.PdfReader(rawBytes)
        val numberOfPages = reader.numberOfPages
        val sb = java.lang.StringBuilder()
        for (i in 1..numberOfPages) {
            val text = com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader, i)
            if (text != null && text.isNotEmpty()) {
                sb.append(text).append("\n")
            }
        }
        reader.close()
        sb.toString().trim()
    } catch (e: Exception) {
        "Gagal membaca file PDF: ${e.localizedMessage}"
    }
}

private fun readTextFromWordDocx(rawBytes: ByteArray): String {
    try {
        val bais = java.io.ByteArrayInputStream(rawBytes)
        val zis = java.util.zip.ZipInputStream(bais)
        var entry = zis.nextEntry
        var documentXml: String? = null
        while (entry != null) {
            if (entry.name == "word/document.xml") {
                documentXml = zis.bufferedReader(java.nio.charset.StandardCharsets.UTF_8).readText()
                break
            }
            entry = zis.nextEntry
        }
        zis.close()

        if (documentXml == null) {
            return "File tidak dikenali sebagai format Word (.docx) yang valid (word/document.xml tidak ditemukan)."
        }

        val sb = java.lang.StringBuilder()
        var i = 0
        val len = documentXml.length
        while (i < len) {
            val openTagIdx = documentXml.indexOf('<', i)
            if (openTagIdx == -1) {
                break
            }
            i = openTagIdx
            val closeTagIdx = documentXml.indexOf('>', i)
            if (closeTagIdx == -1) {
                break
            }
            val tag = documentXml.substring(i, closeTagIdx + 1)
            i = closeTagIdx + 1
            
            if (tag.startsWith("<w:p") && !tag.endsWith("/>")) {
                // Paragraph start
            } else if (tag.startsWith("</w:p>")) {
                sb.append("\n")
            } else if (tag.startsWith("<w:t") && !tag.endsWith("/>")) {
                val endTextTagIdx = documentXml.indexOf("</w:t>", i)
                if (endTextTagIdx != -1) {
                    val rawText = documentXml.substring(i, endTextTagIdx)
                    val cleanText = rawText
                        .replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("&apos;", "'")
                    sb.append(cleanText)
                    i = endTextTagIdx + 6
                }
            } else if (tag.startsWith("<w:br")) {
                sb.append("\n")
            }
        }
        return sb.toString().trim()
    } catch (e: Exception) {
        return "Gagal membaca file Word: ${e.localizedMessage}"
    }
}

// ------------------- SCREEN 3: UPLOAD & OPTION SELECTION SCREEN -------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Skripsi") }
    var selectedMode by remember { mutableStateOf("Dosen Pembimbing") }
    var selectedStyle by remember { mutableStateOf("Detail") }

    var uploadError by remember { mutableStateOf<String?>(null) }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    var uploadedFileExt by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val rawBytes = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.readBytes()
                } ?: byteArrayOf()

                val ext = getFileExtension(context, uri)
                val fileName = getFileName(context, uri)

                val isPdf = (ext == "pdf") || (rawBytes.size >= 4 && 
                        rawBytes[0] == 0x25.toByte() && 
                        rawBytes[1] == 0x50.toByte() && 
                        rawBytes[2] == 0x44.toByte() && 
                        rawBytes[3] == 0x46.toByte())
                        
                val isZip = (ext == "docx") || (rawBytes.size >= 4 &&
                        rawBytes[0] == 0x50.toByte() && 
                        rawBytes[1] == 0x4B.toByte() && 
                        rawBytes[2] == 0x03.toByte() && 
                        rawBytes[3] == 0x04.toByte())

                if (ext == "doc") {
                    uploadError = "Format Word lama (.doc) tidak didukung secara langsung. Silakan ubah file draf Anda ke format modern (.docx) atau PDF lalu unggah kembali."
                } else if (isPdf) {
                    uploadError = null
                    var text = readTextFromPdf(rawBytes)
                    if (text.startsWith("Gagal membaca file")) {
                        uploadError = text
                    } else {
                        if (text.length > 300000) {
                            text = text.take(300000)
                            uploadError = "Isi draf dokumen PDF berhasil diekstrak dan dipotong otomatis agar pas dengan kapasitas optimal 300.000 karakter."
                        }
                        contentText = text
                        title = fileName
                        uploadedFileName = fileName
                        uploadedFileExt = "pdf"
                    }
                } else if (isZip) {
                    uploadError = null
                    var text = readTextFromWordDocx(rawBytes)
                    if (text.startsWith("Gagal membaca file") || text.startsWith("File tidak dikenali")) {
                        uploadError = text
                    } else {
                        if (text.length > 300000) {
                            text = text.take(300000)
                            uploadError = "Isi draf naskah Word (.docx) berhasil diekstrak dan dipotong otomatis agar pas dengan kapasitas optimal 300.000 karakter."
                        }
                        contentText = text
                        title = fileName
                        uploadedFileName = fileName
                        uploadedFileExt = "docx"
                    }
                } else {
                    uploadError = null
                    var text = String(rawBytes, java.nio.charset.StandardCharsets.UTF_8)
                    if (text.length > 300000) {
                        text = text.take(300000)
                        uploadError = "Isi draf Anda sangat panjang dan dipotong otomatis agar pas dengan kapasitas optimal 300.000 karakter."
                    }
                    contentText = text
                    title = fileName
                    uploadedFileName = fileName
                    uploadedFileExt = if (ext.isNotEmpty()) ext else "txt"
                }
            } catch (e: Exception) {
                uploadError = "Gagal membaca file: ${e.localizedMessage}"
            }
        }
    }

    val analysisState by viewModel.analysisState.collectAsState()

    var typeMenuExpanded by remember { mutableStateOf(false) }
    var modeMenuExpanded by remember { mutableStateOf(false) }
    var styleMenuExpanded by remember { mutableStateOf(false) }

    val manuscriptTypes = listOf("Skripsi", "Tesis", "Disertasi", "Artikel jurnal", "Chapter book", "Proposal penelitian")
    val reviewModes = listOf("Dosen Pembimbing", "Penguji Sidang", "Reviewer Jurnal", "Editor Buku", "Scopus")
    val commentStyles = listOf("Ringkas", "Detail", "Sangat kritis")

    val templates = listOf(
        "Metodologi Peningkatan Motivasi UMKM Nasional berbasis Fintech Terintegrasi" to """
            Abstrak
            Penelitian ini bertujuan untuk menelaah strategi komparatif implementasi platform teknologi finansial (Fintech) dalam mengelevasi kinerja keuangan dan motivasi bisnis Usaha Mikro Kecil dan Menengah (UMKM) di Indonesia. Pendekatan analisis yang digunakan adalah kuesioner asosiatif empiris. Namun perkembangan digital telah mempengaruhi banyak sendi kehidupan manusia di era modern yang serba cepat ini.
            
            Bab I: Pendahuluan
            Latar Belakang: UMKM memegang peranan krusial dalam pertumbuhan ekonomi negara. Namun akses terhadap keuangan formal masih sangat terbatas. Oleh karena itu, kehadiran fintech sangat krusial. Masalahnya adalah apakah pelaku UMKM siap mengadopsi integrasi aplikasi yang ditawarkan secara harmonis.
            
            Bab II: Tinjauan Pustaka
            Menurut Sudarsono (2012) menyatakan bahwa motivasi kerja dipengaruhi oleh faktor internal dan pemenuhan kebutuhan dasar finansial yang memadai secara berkesinambungan. Hal ini diperkuat juga oleh teori adopsi teknologi UTAUT. Namun integrasi korelasi fungsional tersebut sering meleset.
            
            Bab III: Metodologi Penelitian
            Penelitian asosiatif ini mengerahkan responden di wilayah pulau jawa. Sampel dikumpulkan secara online menggunakan google form dengan kuesioner skala likert. Teknik analisis yang dipakai adalah SEM PLS.
            
            Bab IV: Pembahasan
            Mengacu pada uji regresi linier berganda, diperoleh pengaruh fungsional bernilai koefisien r-square sebesar 0.54. Hasil data analisis mendukung asumsi hipotesis yang dibangun. Ini berarti fintech berperan signifikan mendongkrak omzet hingga mencapai 25% setiap tahunnya.
            
            Bab V: Kesimpulan
            Fintech memberikan sumbangsih nyata dalam mendistribusikan modal kerja secara masif bagi seluruh kategori pengusaha kecil di nusantara.
            
            Daftar Pustaka
            1. UTAUT Framework references, Global Tech Journal, 2021.
            2. Sudarsono, R. (2012). Manajemen Keuangan UMKM Modern.
        """.trimIndent(),
        "Pengaruh Asimetri Informasi terhadap Efisiensi Pasar Modal: Studi Kasus" to """
            Abstrak
            Studi empiris ini mengevaluasi seberapa dalam tingkat kecocokan informasi asimetris berimbas pada volatilitas indeks saham pada instrumen bursa efek.
            
            BAB I: Pendahuluan
            Transparansi pelaporan keuangan korporasi dalam standar akuntansi berisiko melahirkan bias informasi bagi investor minoritas. Oleh karena itu, penting untuk ditekankan pentingnya asimetri informasi yang terkendali. Rumusan masalah: 1) Bagaimana pengaruh asimetri informasi terhadap likuiditas pasar? 2) Apakah terdapat pengaruh asimetri informasi terhadap efisiensi keputusan pemegang saham?
            
            BAB II: Tinjauan Pustaka
            Teori Keagenan (Agency Theory) menjelaskan dinamika ketimpangan distribusi fakta bisnis lapangan antara manajemen perusahaan dan investor sekunder.
            
            BAB III: Metodologi
            Menggunakan data sekunder runtun waktu (time-series) kuartalan mulai tahun 2018 sampai dengan 2024. Total sampel sebanyak 45 emiten perbankan.
            
            BAB IV: Pembahasan
            Uji korelasi regresi meyakinkan pengaruh asimetri informasi nyata merusak stabilitas likuiditas bursa keuangan. Hal ini sejalan dengan teori yang dirumuskan sebelumnya.
            
            BAB V: Penutup
            Kesimpulan utama membuktikan bahwa penguatan akuntabilitas data emiten mampu menekan asimetri kapital secara berkelanjutan.
        """.trimIndent()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(SoftGray, CircleShape)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = NavyPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Review Naskah Baru",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick template loader card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Muat Contoh Draf Instan (Rekomendasi)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    templates.forEachIndexed { idx, item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WhitePure)
                                .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .clickable {
                                    title = item.first
                                    contentText = item.second
                                    if (idx == 0) {
                                        selectedType = "Skripsi"
                                        selectedMode = "Dosen Pembimbing"
                                    } else {
                                        selectedType = "Artikel jurnal"
                                        selectedMode = "Reviewer Jurnal"
                                    }
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (idx == 0) "Draf Skripsi UMKM" else "Draf Jurnal Efisiensi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct File Upload Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NavyPrimary.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .clickable {
                    filePickerLauncher.launch("*/*")
                },
            colors = CardDefaults.cardColors(containerColor = LightBg),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Unggah File",
                        tint = NavyPrimary,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(-90f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Unggah File Draf Akademik",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Ketuk untuk memilih file draf Word (.docx), PDF (.pdf), Teks (.txt), atau Markdown (.md) berkapasitas besar hingga 300.000 karakter tanpa harus salin-tempel.",
                    fontSize = 11.sp,
                    color = DarkText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                if (uploadedFileName != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📄 ${uploadedFileName!!}.${uploadedFileExt ?: "docx"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Hapus File",
                            tint = ColorRevisiBesar,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    uploadedFileName = null
                                    contentText = ""
                                    title = ""
                                    uploadError = null
                                }
                        )
                    }
                }
            }
        }

        if (uploadError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorRevisiBesar.copy(alpha = 0.08f))
                    .border(1.dp, ColorRevisiBesar.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Warning, 
                        contentDescription = "Error Upload", 
                        tint = ColorRevisiBesar, 
                        modifier = Modifier.size(16.dp).padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uploadError!!,
                        color = ColorRevisiBesar,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Judul Naskah Akademik", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Ketikkan judul utama rancangan draf ilmiah...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("manuscript_title_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyPrimary,
                unfocusedBorderColor = SoftGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Jenis Naskah", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WhitePure)
                        .border(1.dp, SoftGray, RoundedCornerShape(8.dp))
                        .clickable { typeMenuExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = selectedType, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    DropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        manuscriptTypes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedType = item
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Mode Review", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WhitePure)
                        .border(1.dp, SoftGray, RoundedCornerShape(8.dp))
                        .clickable { modeMenuExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = selectedMode, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    DropdownMenu(
                        expanded = modeMenuExpanded,
                        onDismissRequest = { modeMenuExpanded = false }
                    ) {
                        reviewModes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedMode = item
                                    modeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Text(text = "Gaya Komentar Reviewer", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhitePure)
                    .border(1.dp, SoftGray, RoundedCornerShape(8.dp))
                    .clickable { styleMenuExpanded = true }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = selectedStyle, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                DropdownMenu(
                    expanded = styleMenuExpanded,
                    onDismissRequest = { styleMenuExpanded = false }
                ) {
                    commentStyles.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedStyle = item
                                styleMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Isi / Abstrak Naskah Lengkap", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = "${contentText.length} Karakter", fontSize = 11.sp, color = DarkText.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            placeholder = { Text("Rekatkan draf teks bab ilmiah Anda di sini (misal Abstrak, Pendahuluan, Metode bimbingan dsb, agar AI dapat menganalisis argumen)...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("manuscript_content_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyPrimary,
                unfocusedBorderColor = SoftGray
            ),
            maxLines = 15
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Trigger analysis or showing loader button
        when (analysisState) {
            is AnalysisState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = NavyPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Menganalisis Kualitas Akademik...",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Mengecek logika bimbingan, referensi, keselarasan bab, plagiasi & kemiripan AI..",
                        fontSize = 11.sp,
                        color = DarkText.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            is AnalysisState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorRevisiBesar.copy(alpha = 0.08f))
                        .border(1.dp, ColorRevisiBesar, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Warning, contentDescription = "Eror", tint = ColorRevisiBesar, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (analysisState as AnalysisState.Error).message,
                            color = ColorRevisiBesar,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.analyzeDocument(title, contentText, selectedType, selectedMode, selectedStyle)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_analysis_button_retry"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Coba Lagi Analisis", fontWeight = FontWeight.Bold)
                }
            }
            else -> {
                Button(
                    onClick = {
                        viewModel.analyzeDocument(title, contentText, selectedType, selectedMode, selectedStyle)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_analysis_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "Mulai", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mulai Peninjauan AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ------------------- SCREEN 4: RESULTS SCREEN WITH METICULOUS REVIEW TABS -------------------

@Composable
fun ResultScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    val selectedDoc by viewModel.selectedDocument.collectAsState()
    
    val report = remember(selectedDoc) {
        selectedDoc?.analysisResultJson?.let { json ->
            try {
                RetrofitClient.reportAdapter.fromJson(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = WhitePure, modifier = Modifier.size(24.dp))
                    }
                },
                title = { 
                    Text(
                        text = "Hasil Review NaskahPro", 
                        fontWeight = FontWeight.Bold, 
                        color = WhitePure,
                        fontSize = 18.sp
                    ) 
                },
                actions = {
                    if (selectedDoc != null && report != null) {
                        val context = LocalContext.current
                        IconButton(
                            onClick = {
                                val shareTxt = buildShareReportText(selectedDoc!!, report)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Laporan Review Akademik - NaskahPro AI")
                                    putExtra(Intent.EXTRA_TEXT, shareTxt)
                                }
                                context.startActivity(Intent.createChooser(intent, "Ekspor Laporan NaskahPro AI"))
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = "Bagi Laporan", tint = GoldLight, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { innerPadding ->
        if (selectedDoc == null || report == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Belum ada dokumen yang dipilih.", color = DarkText)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                DocumentResultsHeader(doc = selectedDoc!!, report = report)
                ResultsTabsSection(doc = selectedDoc!!, report = report)
            }
        }
    }
}

@Composable
fun DocumentResultsHeader(doc: AcademicDocument, report: ReviewReport) {
    val statusColor = when {
        report.overallScore >= 86 -> ColorSangatLayak
        report.overallScore >= 76 -> ColorLayakRevisiRingan
        report.overallScore >= 61 -> ColorRevisiSedang
        report.overallScore >= 41 -> ColorRevisiBesar
        else -> ColorBelumLayak
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, WhitePure.copy(alpha = 0.08f), RoundedCornerShape(32.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(2.5.dp, statusColor, CircleShape)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = report.overallScore.toString(),
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "SKOR",
                        color = SoftGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    color = WhitePure,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(GoldAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = doc.manuscriptType.uppercase(), 
                            color = GoldLight, 
                            fontSize = 8.sp, 
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mode: ${doc.reviewMode}", 
                        color = SoftGray, 
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = report.categoryStatus.uppercase(),
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ResultsTabsSection(doc: AcademicDocument, report: ReviewReport) {
    var selectedTabIdx by remember { mutableStateOf(0) }
    
    val tabTitles = listOf("Catatan", "Skor Kriteria", "Selingkung/AI", "Konsistensi", "Sidang")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = NavyPrimary,
            contentColor = WhitePure,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
                    color = GoldAccent
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTabIdx == idx,
                    onClick = { selectedTabIdx = idx },
                    text = { 
                        Text(
                            text = title, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = if (selectedTabIdx == idx) GoldLight else SoftGray
                        ) 
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTabIdx) {
                0 -> CatatanRevisiTab(report = report)
                1 -> SkorKriteriaTab(report = report)
                2 -> PlagiasiDanAiTab(report = report)
                3 -> KonsistensiTab(report = report)
                4 -> PrediksiSidangTab(report = report)
            }
        }
    }
}

// Tab 1: Catatan Revisi per Bab & Komentar Pembimbing

@Composable
fun CatatanRevisiTab(report: ReviewReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Komentar Umum Penelaah",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.advisorComments,
                        color = DarkText,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Catatan Koreksi Bab demi Bab",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 14.sp
            )
        }

        items(report.revisionNotes) { note ->
            RevisionNoteRowCard(note = note)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rekomendasi Revisi Prioritas Utama",
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    report.priorityRecommendations.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tip, fontSize = 12.sp, color = DarkText)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RevisionNoteRowCard(note: ReviewRevisionNote) {
    val badgeColor = when (note.prioritas) {
        "Tinggi" -> ColorRevisiBesar
        "Sedang" -> ColorRevisiSedang
        else -> ColorLayakRevisiRingan
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = note.bab, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = note.prioritas, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = note.catat,
                fontSize = 13.sp,
                color = DarkText,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SoftGray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Filled.Refresh, 
                    contentDescription = null, 
                    tint = ColorSangatLayak, 
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(text = "Rekomendasi Tindakan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorSangatLayak)
                    Text(text = note.rekomendasi, fontSize = 12.sp, color = DarkText)
                }
            }
        }
    }
}

// Tab 2: Skor Kriteria Diagram/Meters

@Composable
fun SkorKriteriaTab(report: ReviewReport) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Kelayakan Kinerja per Parameter",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 15.sp
        )

        CriteriaScoreMeter(label = "Kelengkapan Struktur", score = report.scoreStruktur)
        CriteriaScoreMeter(label = "Konsistensi Logika Akademik", score = report.scoreKonsistensi)
        CriteriaScoreMeter(label = "Kedalaman Pokok Teori", score = report.scoreTeori)
        CriteriaScoreMeter(label = "Kesesuaian Metodologi", score = report.scoreMetode)
        CriteriaScoreMeter(label = "Kedalaman Pembahasan", score = report.scorePembahasan)
        CriteriaScoreMeter(label = "Kerapian Referensi", score = report.scoreReferensi)
        CriteriaScoreMeter(label = "Orisinalitas Naskah", score = report.scoreOrisinalitas)
        CriteriaScoreMeter(label = "Kesiapan Submit Pengiriman", score = report.scoreKesiapan)

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun CriteriaScoreMeter(label: String, score: Int) {
    val dynamicColor = when {
        score >= 86 -> ColorSangatLayak
        score >= 76 -> ColorLayakRevisiRingan
        score >= 61 -> ColorRevisiSedang
        score >= 41 -> ColorRevisiBesar
        else -> ColorBelumLayak
    }

    val animatedProgress by animateFloatAsState(targetValue = score / 100f, animationSpec = tween(1000))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = DarkText)
            Text(text = "$score/100", fontWeight = FontWeight.Bold, color = dynamicColor, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = dynamicColor,
            trackColor = SoftGray
        )
    }
}

// Tab 3: Plagiasi & Deteksi AI

@Composable
fun PlagiasiDanAiTab(report: ReviewReport) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OrisinalitasMetricCard(
                title = "Kemiripan Teks",
                percentage = report.plagiarismPercentage,
                alertThreshold = 20,
                modifier = Modifier.weight(1f)
            )
            OrisinalitasMetricCard(
                title = "Probabilitas AI",
                percentage = report.aiPercentage,
                alertThreshold = 30,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Anomali & Highlight Kemiripan Kalimat",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 14.sp
        )

        if (report.plagiarismHighlights.isEmpty()) {
            Text(
                text = "Luar biasa! Tidak ada kalimat yang terdeteksi meniru basis data secara berlebihan.",
                fontSize = 12.sp,
                color = ColorSangatLayak
            )
        } else {
            report.plagiarismHighlights.forEach { item ->
                PlagiarismItemCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kajian Kalimat Berpola Monoton (Identifikasi AI)",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 14.sp
        )

        if (report.aiHighlights.isEmpty()) {
            Text(
                text = "Gaya penulisan natural empiris terlacak kuat pada seluruh bagian naskah draf.",
                fontSize = 12.sp,
                color = ColorSangatLayak
            )
        } else {
            report.aiHighlights.forEach { item ->
                AiItemCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun OrisinalitasMetricCard(
    title: String,
    percentage: Int,
    alertThreshold: Int,
    modifier: Modifier = Modifier
) {
    val color = if (percentage > alertThreshold) ColorRevisiBesar else ColorSangatLayak
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$percentage%",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (percentage > alertThreshold) "Perlu Tindakan" else "Masih Aman",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlagiarismItemCard(item: PlagiarismHighlight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.dp, SoftGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = ColorRevisiBesar, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kemiripan Sumber: ${item.sumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ColorRevisiBesar
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"${item.teks}\"",
                fontSize = 12.sp,
                color = DarkText,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SoftGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saran Parafrase: ${item.saran}",
                fontSize = 11.sp,
                color = ColorSangatLayak,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AiItemCard(item: AiHighlight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.dp, SoftGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = ColorRevisiSedang, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gaya AI: Terlalu Mekanis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ColorRevisiSedang
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"${item.teks}\"",
                fontSize = 12.sp,
                color = DarkText,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Alasan: ${item.alasan}",
                fontSize = 11.sp,
                color = DarkText.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SoftGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saran Humanisasi: ${item.saran}",
                fontSize = 11.sp,
                color = ColorLayoutHighlight,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

val ColorLayoutHighlight = Color(0xFF1565C0)

// Tab 4: Konsistensi Akademik & Referensi

@Composable
fun KonsistensiTab(report: ReviewReport) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Kompabilitas Logika & Konsistensi Alur",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 14.sp
        )

        report.consistencyIssues.forEach { issue ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorRevisiBesar.copy(alpha = 0.05f))
                    .border(0.5.dp, ColorRevisiBesar.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = ColorRevisiBesar, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = issue, fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Masalah Kerapian Sitasi (Kurang Rujukan/Usang)",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 14.sp
        )

        report.citationIssues.forEach { issue ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorRevisiSedang.copy(alpha = 0.05f))
                    .border(0.5.dp, ColorRevisiSedang.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = ColorRevisiSedang, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = issue, fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.04f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Saran Optimalisasi Novelty (Keterbaruan Silang)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = report.noveltySuggestions,
                    fontSize = 12.sp,
                    color = DarkText,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Tab 5: Prediksi Pertanyaan Sidang / Penguji

@Composable
fun PrediksiSidangTab(report: ReviewReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Prediksi Pertanyaan Sidang & Penguji",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gunakan skenario simulasi dewan penguji/reviewer jurnal ini untuk mengasah argumentasi lisan dan pertahanan substansi penelitian Anda sebelum sidang atau seminar hasil bimbingan.",
                        color = WhitePure,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(report.potentialQuestions) { q ->
            Card(
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "?", color = NavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = q,
                        fontWeight = FontWeight.Medium,
                        color = DarkText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Info & Layanan Screen

@Composable
fun PanduanLayananScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(SoftGray, CircleShape)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = NavyPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "INFO LAYANAN",
                    color = GoldAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Struktur & Paket Layanan",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = WhitePure),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, SoftGray.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Mengapa Memilih NaskahPro AI?",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = NavyPrimary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                BulletTextPoint("Bukan sekedar grammar checker, asisten kami menganalisis logika substantif riset.")
                BulletTextPoint("Mengecek sinkronisasi rumusan tujuan, hipotesis dengan data per bab.")
                BulletTextPoint("Memberikan sudut pandang komentar bimbingan spesifik (Dosen Pembimbing, Penguji Sidang, Reviewer Jurnal, Editor Buku, Scopus).")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "PAKET LAYANAN TERSEDIA",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = NavyPrimary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        PaketInfoCard(
            tier = "Paket Basic (Mahasiswa)",
            desc = "Direkomendasikan untuk bimbingan draf skripsi awal.",
            features = listOf("Review bab detail (BAB I - BAB V)", "Catatan revisi rujukan dasar", "Skor kelayakan bab masif", "Ekspor PDF laporan")
        )

        PaketInfoCard(
            tier = "Paket Pro (Dosen & Peneliti)",
            desc = "Optimasi artikel jurnal dan rujukan chapter book bimbingan.",
            features = listOf("Review komparatif chapter book", "Identifikasi Novelty dan Kontribusi", "Cek akurasi referensi sitasi", "Cek Detektor AI & Plagiat")
        )

        PaketInfoCard(
            tier = "Paket Premium (Publikasi Scopus)",
            desc = "Telaah tingkat tinggi menembus jurnal internasional bereputasi.",
            features = listOf("Mode telaah review Scopus komprehensif", "Evaluasi desk-rejection risk", "Saran perbaikan abstrak abstrak", "Rekomendasi target jurnal relevan")
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun BulletTextPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = ColorSangatLayak, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 13.sp, color = DarkText, lineHeight = 18.sp)
    }
}

@Composable
fun PaketInfoCard(
    tier: String,
    desc: String,
    features: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = tier, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = NavyPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 12.sp, color = DarkText.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SoftGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = feature, fontSize = 13.sp, color = DarkText)
                }
            }
        }
    }
}

// Share text builder equivalent of PDF downloader
fun buildShareReportText(doc: AcademicDocument, report: ReviewReport): String {
    val notesStr = report.revisionNotes.joinToString("\n\n") { note ->
        "• ${note.bab} (${note.prioritas})\n  Isi Koreksi: ${note.catat}\n  Rekomendasi: ${note.rekomendasi}"
    }

    val qStr = report.potentialQuestions.joinToString("\n") { q -> "- $q" }

    return """
        ====================================================
        LAPORAN REVIEW AKADEMIK NASKAHPRO AI
        ====================================================
        Judul: ${doc.title}
        Jenis Naskah: ${doc.manuscriptType}
        Kategori Layanan: ${doc.reviewMode} (Style ${doc.commentStyle})
        
        SKOR KELAYAKAN UTAMA: ${report.overallScore} / 100
        Status Kelayakan: ${report.categoryStatus}
        
        Komentar Umum Penelaah:
        ${report.advisorComments}
        
        SKOR RINCIAN EVALUASI:
        1. Kelengkapan Struktur: ${report.scoreStruktur}
        2. Konsistensi Logika: ${report.scoreKonsistensi}
        3. Landasan Teoretis: ${report.scoreTeori}
        4. Kejelasan Metode: ${report.scoreMetode}
        5. Kedalaman Pembahasan: ${report.scorePembahasan}
        6. Akurasi Referensi: ${report.scoreReferensi}
        7. Indeks Orisinalitas: ${report.scoreOrisinalitas}
        8. Kesiapan Submit Pengiriman: ${report.scoreKesiapan}
        
        KEMIRIPAN TEKS & INTEGRITAS:
        • Indeks Kemiripan Teks: ${report.plagiarismPercentage}%
        • Indeks Indikasi Gaya AI: ${report.aiPercentage}%
        
        CATATAN REVISI SEURUT BAB:
        $notesStr
        
        SARAN OPTIMALISASI NOVELTY:
        ${report.noveltySuggestions}
        
        SIMULASI PERTANYAAN SIDANG:
        $qStr
        
        -- Diulas secara pintar & komprehensif menggunakan NaskahPro AI --
    """.trimIndent()
}
