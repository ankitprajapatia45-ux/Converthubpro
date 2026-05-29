package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.ConversionRecord
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConvertHubApp(viewModel: ConvertHubViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg),
        topBar = {
            ConvertHubTopAppBar(viewModel)
        },
        bottomBar = {
            ConvertHubBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = SlateDarkBg,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    // Apple-style background radial gradient glow in the top-right corner
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1500DFD8), Color.Transparent),
                            center = Offset(size.width, 0f),
                            radius = size.width * 0.9f
                        ),
                        radius = size.width * 0.9f,
                        center = Offset(size.width, 0f)
                    )
                    // Stripe-style gradient mesh in the bottom-left corner
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x107928CA), Color.Transparent),
                            center = Offset(0f, size.height),
                            radius = size.height * 0.7f
                        ),
                        radius = size.height * 0.7f,
                        center = Offset(0f, size.height)
                    )
                }
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { 40 } togetherWith
                    fadeOut(animationSpec = tween(250))
                },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    "Home" -> HomeScreen(viewModel)
                    "Tools" -> ToolsCatalogScreen(viewModel)
                    "Dashboard" -> DashboardScreen(viewModel)
                    "Auth" -> AuthenticationScreen(viewModel)
                }
            }
        }
    }
}

// TOP BAR COMPONENT
@Composable
fun ConvertHubTopAppBar(viewModel: ConvertHubViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(BorderGlass, Color.Transparent)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo Segment
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentNeonBlue, AccentCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "ConvertHub Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "ConvertHub",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "PRO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentCyan,
                    modifier = Modifier
                        .border(1.dp, AccentCyan, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            // User Session Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isLoggedIn) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1F1F293D))
                            .border(1.dp, BorderGlass, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPremium) AccentCyan else NeonGreen)
                        )
                        Text(
                            text = userEmail.substringBefore("@"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 80.dp)
                        )
                        if (isPremium) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Premium badge",
                                tint = AccentCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.setTab("Auth") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x330070F3),
                            contentColor = TextLight
                        ),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, Color(0x660070F3)),
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// M3 BOTTOM NAVIGATION
@Composable
fun ConvertHubBottomBar(currentTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = SlateSurface.copy(alpha = 0.92f),
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, "Home"),
            Triple("Tools", Icons.Default.Category, "Tools"),
            Triple("Dashboard", Icons.Default.Dashboard, "Dashboard"),
            Triple("Auth", Icons.Default.Person, "Account")
        )

        items.forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentCyan,
                    selectedTextColor = AccentCyan,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = Color(0x2B00DFD8)
                ),
                modifier = Modifier.testTag("nav_item_${tab.lowercase()}")
            )
        }
    }
}

// 1. HOME SCREEN SECTION
@Composable
fun HomeScreen(viewModel: ConvertHubViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sourceFormat by viewModel.sourceFormat.collectAsState()
    val targetFormat by viewModel.targetFormat.collectAsState()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isConverting by viewModel.isConverting.collectAsState()
    val conversionProgress by viewModel.conversionProgress.collectAsState()
    val activeMessage by viewModel.activeMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val pickedNames by viewModel.pickedFileNames.collectAsState()
    val pickedSizes by viewModel.pickedFilesSizes.collectAsState()
    val currentImageUri by viewModel.imageUri.collectAsState()

    // Animating numbers for live stats
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    // Local Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.imageUri.value = it
            val name = getFileName(context, it) ?: "local_image.jpg"
            val size = getFileSize(context, it)
            viewModel.pickedFileNames.value = listOf(name)
            viewModel.pickedFilesSizes.value = listOf(size)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0x1F00DFD8))
                            .border(1.dp, Color(0x3B00DFD8), RoundedCornerShape(30.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "⚡ Real-Time Local & Cloud Conversions",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Convert Any File Format Instantly",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fast, Secure & Free Online File Converter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // GLASSMORPHIC CONVERTER DASHBOARD CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(BorderGlass, Color.Transparent, BorderGlass)
                        ),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category Selector Bar
                    Text(
                        text = "SELECT CONVERTER TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val categories = listOf("Image", "PDF", "Document", "Audio", "Video", "Data")
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) AccentNeonBlue else Color(0x0CFFFFFF))
                                    .border(
                                        1.dp,
                                        if (isSelected) AccentCyan else BorderGlass,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.selectedCategory.value = cat
                                        // Set smart defaults
                                        when (cat) {
                                            "Image" -> {
                                                viewModel.sourceFormat.value = "JPG"
                                                viewModel.targetFormat.value = "PNG"
                                            }
                                            "PDF" -> {
                                                viewModel.sourceFormat.value = "PDF"
                                                viewModel.targetFormat.value = "COMPRESS"
                                            }
                                            "Document" -> {
                                                viewModel.sourceFormat.value = "TXT"
                                                viewModel.targetFormat.value = "PDF"
                                            }
                                            "Audio" -> {
                                                viewModel.sourceFormat.value = "MP3"
                                                viewModel.targetFormat.value = "WAV"
                                            }
                                            "Video" -> {
                                                viewModel.sourceFormat.value = "MP4"
                                                viewModel.targetFormat.value = "MP3"
                                            }
                                            "Data" -> {
                                                viewModel.sourceFormat.value = "JSON"
                                                viewModel.targetFormat.value = "XML"
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }

                    Divider(color = BorderGlass)

                    // FROM-TO Dropdowns block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // From Setup
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FROM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x12FFFFFF))
                                    .border(1.dp, BorderGlass, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(sourceFormat, color = TextLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap icon",
                            tint = AccentCyan,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val temp = viewModel.sourceFormat.value
                                    viewModel.sourceFormat.value = viewModel.targetFormat.value
                                    viewModel.targetFormat.value = temp
                                }
                        )

                        // To Setup
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x12FFFFFF))
                                    .border(1.dp, BorderGlass, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(targetFormat, color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    // FILE UPLOADER TARGET / INPUT SECTION
                    if (selectedCategory == "Document" && sourceFormat == "TXT" && targetFormat == "PDF") {
                        // Direct Native Input Box for conversion!
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ENTER TEXT CONTENT TO GENERATE PDF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            val textInput by viewModel.textToPdfContent.collectAsState()
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { viewModel.textToPdfContent.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = BorderGlass,
                                    focusedContainerColor = Color(0x0CFFFFFF),
                                    unfocusedContainerColor = Color(0x05FFFFFF),
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(fontSize = 13.sp)
                            )
                        }
                    } else if (selectedCategory == "Data") {
                        // Raw text data parser
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "PASTE RAW $sourceFormat DATA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            val dataInput by viewModel.dataInputContent.collectAsState()
                            OutlinedTextField(
                                value = dataInput,
                                onValueChange = { viewModel.dataInputContent.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = BorderGlass,
                                    focusedContainerColor = Color(0x0CFFFFFF),
                                    unfocusedContainerColor = Color(0x05FFFFFF),
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            )
                        }
                    } else {
                        // Standard Interactive Drag & Drop Box (Supports Actual Image Picker for Images!)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x0CFFFFFF))
                                .border(
                                    BorderStroke(
                                        1.5.dp,
                                        Brush.swipeBrush(
                                            listOf(BorderGlass, AccentCyan, BorderGlass),
                                            shineOffset
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (selectedCategory == "Image") {
                                        photoPickerLauncher.launch(arrayOf("image/*"))
                                    } else {
                                        // Mock other conversions file picker
                                        viewModel.pickedFileNames.value =
                                            listOf("document_contract_${(10..99).random()}.${sourceFormat.lowercase()}")
                                        viewModel.pickedFilesSizes.value =
                                            listOf((500000..7500000).random().toLong())
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x1F0070F3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (selectedCategory == "Image") Icons.Default.PhotoCamera else Icons.Outlined.UploadFile,
                                        contentDescription = "Upload trigger",
                                        tint = AccentCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (selectedCategory == "Image") "Tap to Pick Image from Gallery" else "Tap to choose workspace files",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextLight
                                    )
                                    Text(
                                        text = "Supports local sandbox execution up to 50MB",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Display Picked Files
                    if (pickedNames.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x1F10B981))
                                .border(1.dp, Color(0x3B10B981), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InsertDriveFile,
                                    contentDescription = "Ready node",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = pickedNames.first(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatSize(pickedSizes.getOrElse(0) { 0 }),
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            IconButton(onClick = {
                                viewModel.pickedFileNames.value = emptyList()
                                viewModel.pickedFilesSizes.value = emptyList()
                                viewModel.imageUri.value = null
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove file", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Convert Action trigger or Real-time simulation loading
                    if (isConverting) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = activeMessage,
                                    fontSize = 12.sp,
                                    color = AccentCyan,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${(conversionProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            }
                            LinearProgressIndicator(
                                progress = { conversionProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = AccentCyan,
                                trackColor = Color(0x1AFFFFFF)
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startConversion() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentNeonBlue
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("convert_button"),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Transform, contentDescription = "Transform action", modifier = Modifier.size(20.dp))
                                Text(
                                    text = "CONVERT FORMAT NOW",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = "❌ Error: $it",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Toggles layout for enhancements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Secure indicator", tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Text("100% Secure Local Sandbox", fontSize = 11.sp, color = TextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CloudQueue, contentDescription = "Cloud fallback", tint = AccentPurple, modifier = Modifier.size(14.dp))
                            Text("Cloud Assist Ready", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // STATS ROW CARDS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple("12,400+", "Active Users", Icons.Default.People),
                    Triple("2.4M+", "Files Converted", Icons.Default.DoneAll),
                    Triple("100%", "No Data Leaks", Icons.Default.Security)
                ).forEach { (value, desc, icon) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BorderGlass, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = desc, tint = AccentCyan, modifier = Modifier.size(18.dp))
                            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Text(desc, fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // PREMIUM FEATURES ACCORDION SECTION
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "WHY CONVERTHUB PRO RULES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                val featuresList = listOf(
                    Triple("Fast Offline Processing", "No internet? No problem. Convert images, write PDFs and parse structures locally in milliseconds.", Icons.Default.FlashOn),
                    Triple("Maximum Cloud Security", "Files processed via our optional API are fully encrypted and purged strictly within 60 minutes.", Icons.Default.Lock),
                    Triple("Intelligent Batch Engine", "Queues up to 50 files simultaneously with responsive throttling control for smooth multitasking.", Icons.Default.Queue),
                    Triple("Dynamic AI Enhancement", "Integrates robust smart features for OCR scans, document summarizations, and file routing.", Icons.Default.AutoAwesome)
                )

                featuresList.forEach { (title, info, icon) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGlass, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x0CFFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = title, tint = AccentCyan, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                Text(info, fontSize = 11.sp, color = TextMuted, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // FAQ SECTION
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    modifier = Modifier.padding(start = 4.dp)
                )

                val faqs = listOf(
                    "Are my files kept private?" to "Absolutely! ConvertHub Pro does real offline native executions directly on your device. Zero cloud uploads occur unless you specifically request web-based services.",
                    "Is ConvertHub Pro really free?" to "Yes! Our core conversion tools are 100% free with no limits on local processing. Power users can subscribe to premium plans for massive parallel cloud pipelines.",
                    "What formats are supported?" to "We support thousands of combinations! This includes JPEG, PNG, WEBP, SVG, text to PDF, data schemas like JSON/XML, and various audio/video integrations."
                )

                faqs.forEach { (question, answer) ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGlass, RoundedCornerShape(14.dp))
                            .clickable { isExpanded = !isExpanded },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(question, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextLight, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand FAQ",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = answer,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // FOOTER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider(color = BorderGlass)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ConvertHub Pro • Android Edition v1.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AccentCyan
                )
                Text(
                    text = "Crafted for extreme performance, security, and responsiveness using Jetpack Compose & M3.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "© 2026 ConvertHub Inc. All rights reserved.",
                    fontSize = 10.sp,
                    color = TextGray
                )
            }
        }
    }
}

// 2. TOOLS CATALOG SECTION
@Composable
fun ToolsCatalogScreen(viewModel: ConvertHubViewModel) {
    val presets = listOf(
        PresetTool("JPG to PNG", "Image", "JPG", "PNG", Icons.Default.Image),
        PresetTool("PNG to JPG", "Image", "PNG", "JPG", Icons.Default.Landscape),
        PresetTool("PNG to WEBP", "Image", "PNG", "WEBP", Icons.Default.PhotoSizeSelectLarge),
        PresetTool("WEBP to JPG", "Image", "WEBP", "JPG", Icons.Default.FilterHdr),
        PresetTool("CSV to JSON", "Data", "CSV", "JSON", Icons.Default.Code),
        PresetTool("JSON to XML", "Data", "JSON", "XML", Icons.Default.IntegrationInstructions),
        PresetTool("XML to JSON", "Data", "XML", "JSON", Icons.Default.DataObject),
        PresetTool("TXT to PDF", "Document", "TXT", "PDF", Icons.Default.Description),
        PresetTool("Merge PDF", "PDF", "PDF", "MERGE", Icons.Default.MergeType),
        PresetTool("MP4 to MP3", "Video", "MP4", "MP3", Icons.Default.Audiotrack),
        PresetTool("MP3 to WAV", "Audio", "MP3", "WAV", Icons.Outlined.VolumeUp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tools_catalog_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Preset Conversion Tools",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Text(
                text = "Select a rapid workspace configuration to instantly format your assets.",
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(presets) { preset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.configureConverter(preset.category, preset.from, preset.to)
                            viewModel.setTab("Home")
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1A00DFD8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(preset.icon, contentDescription = preset.name, tint = AccentCyan, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(preset.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x337928CA))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(preset.category.uppercase(), fontSize = 9.sp, color = AccentPurple, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x1FDEEEEE))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Offline Ready", fontSize = 9.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Load folder",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

data class PresetTool(
    val name: String,
    val category: String,
    val from: String,
    val to: String,
    val icon: ImageVector
)

// 3. DASHBOARD SCREEN & ANALYTICS
@Composable
fun DashboardScreen(viewModel: ConvertHubViewModel) {
    val context = LocalContext.current
    val records by viewModel.allRecords.collectAsState()
    val scope = rememberCoroutineScope()

    // Aggregate Analytics Calculations
    val totalCount = records.size
    val totalSuccessCount = records.count { it.status == "Success" }
    val totalSize = records.sumOf { it.fileSize }
    val favoritesCount = records.count { it.isFavorite }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Dashboard Title
        item {
            Column {
                Text(
                    text = "Analytics Command Center",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = "Inspect local usage statistics, history logs, and file size allocations.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Stats Overview Row Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Files Handled",
                    value = totalSuccessCount.toString(),
                    icon = Icons.Default.InsertDriveFile,
                    iconColor = AccentCyan
                )
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Storage Saved",
                    value = formatSize(totalSize),
                    icon = Icons.Default.Storage,
                    iconColor = AccentPurple
                )
            }
        }

        // CUSTOM DRAWN GLOWING ANALYTICS BAR CHART (DIRECT CANVAS)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGlass, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CONVERSIONS ACTIVITY PER CATEGORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    // Draw chart using standard canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val labels = listOf("Img", "PDF", "Doc", "Aud", "Vid", "Dat")
                            // Mock tallies matched with standard database categories
                            val values = listOf(5f, 3f, 4f, 2f, 1f, 3f)
                            val maxVal = 6f
                            
                            val width = size.width
                            val height = size.height
                            
                            val numBars = labels.size
                            val barSpacing = 24.dp.toPx()
                            val totalSpacingSpace = barSpacing * (numBars - 1)
                            val availableWidthForBars = width - totalSpacingSpace
                            val barWidth = availableWidthForBars / numBars

                            // Draw light grey background lines
                            val gridLines = 4
                            for (i in 0..gridLines) {
                                val gridY = height * (i.toFloat() / gridLines)
                                drawLine(
                                    color = Color(0x0EFFFFFF),
                                    start = Offset(0f, gridY),
                                    end = Offset(width, gridY),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            // Draw Rounded Custom Bars with Gradient
                            for (idx in labels.indices) {
                                val x = idx * (barWidth + barSpacing)
                                val ratio = values[idx] / maxVal
                                val barHeight = height * ratio * 0.85f // leave room for labels
                                val y = height - barHeight - 16.dp.toPx()

                                // Draw glowing bar
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = if (idx % 2 == 0) listOf(AccentCyan, AccentNeonBlue) else listOf(AccentPurple, AccentNeonBlue)
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                            }
                        }

                        // Add simple labels row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val labels = listOf("Images", "PDFs", "Docs", "Audio", "Videos", "Data")
                            labels.forEach { label ->
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(50.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // HISTORY LIST TITLE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Conversion Transaction Logs",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = "Clear All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .clickable { viewModel.clearHistory() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // IF HISTORY IS EMPTY Showcase empty state UI
        if (records.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .border(1.dp, BorderGlass, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x07FFFFFF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Empty list placeholder",
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No recorded transactions yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextLight,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Start by converting a local image or compiling text into a PDF file on the home screen.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // HISTORIC LIST RECORDS FETCHED DYNAMICALLY
        items(records) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGlass, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SlateSurface.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x0CFFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = record.targetFormat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (record.status == "Success") AccentCyan else Color(0xFFEF4444)
                            )
                        }

                        Column(modifier = Modifier.weight(0.9f)) {
                            Text(
                                text = record.fileName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(record.sourceFormat, fontSize = 10.sp, color = TextMuted)
                                Text("→", fontSize = 11.sp, color = TextGray)
                                Text(record.targetFormat, fontSize = 10.sp, color = AccentCyan)
                                Text("•", fontSize = 10.sp, color = TextGray)
                                Text(formatSize(record.fileSize), fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    // Log triggers share/fav/delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite toggle
                        IconButton(onClick = { viewModel.toggleFavorite(record) }) {
                            Icon(
                                imageVector = if (record.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Fav action",
                                tint = if (record.isFavorite) Color(0xFFEF4444) else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Open / Share Converted file if local path is functional
                        if (record.resultFilePath != null) {
                            IconButton(onClick = {
                                val file = File(record.resultFilePath)
                                if (file.exists()) {
                                    shareFile(context, file)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share output",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Delete from database
                        IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete log node",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier.border(1.dp, BorderGlass, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextLight
            )
        }
    }
}

// 4. AUTHENTICATION & UPGRADE PLANS SCREEN
@Composable
fun AuthenticationScreen(viewModel: ConvertHubViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isRegisterState by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("auth_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoggedIn) {
            // Logged in Profile & Upgrade Pricing Node
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(colors = listOf(AccentNeonBlue, AccentCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userEmail.take(2).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text("Active Account Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Text(userEmail, fontSize = 13.sp, color = TextMuted)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isPremium) Color(0x3300DFD8) else Color(0x1F0070F3))
                            .border(1.dp, if (isPremium) AccentCyan else AccentNeonBlue, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isPremium) "👑 ConvertHub Pro Premium Tier" else "Free Account Plan Tier",
                            color = if (isPremium) AccentCyan else TextLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Upgrade details and CTA
            if (!isPremium) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Brush.linearGradient(listOf(AccentCyan, AccentPurple)), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "UPGRADE TO PRO PASS",
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentCyan,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Unlock Cloud Pipelines & OCR",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight,
                                textAlign = TextAlign.Center
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val features = listOf(
                                    "✨ Parallel cloud processing pipelines",
                                    "⚡ Accelerate speed by 10x with dedicated RAM",
                                    "👁️ Real OCR text scanning on bulk images",
                                    "📦 Unlimited file sizes up to 5GB per compile"
                                )
                                features.forEach { feat ->
                                    Text("• $feat", fontSize = 12.sp, color = TextMuted)
                                }
                            }

                            Button(
                                onClick = { viewModel.login("premium_account@admin.com") },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(
                                    "ACTIVATE PREMIUM PASS NOW",
                                    color = SlateDarkBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out of Session", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            }

        } else {
            // Glassmorphic Login / Register form
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterState) "Create Pro Account" else "Sign In to ConvertHub",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = "Access your files sync and cloud history on any device.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGlass, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderGlass,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password credentials") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderGlass,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (emailInput.isNotEmpty()) {
                                    viewModel.login(emailInput)
                                } else {
                                    viewModel.login("developer_demo@converthub.com")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentNeonBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(
                                if (isRegisterState) "CONTINUE AND SETUP" else "SECURE SIGN IN NOW",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Demo account button
                        Button(
                            onClick = { viewModel.login("premium_account@admin.com") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x330070F3)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0x660070F3)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Fast Demo Log In", color = TextLight)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (isRegisterState) "Already have an account? " else "Don't have an account yet? ",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = if (isRegisterState) "Login" else "Register",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                modifier = Modifier
                                    .clickable { isRegisterState = !isRegisterState }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// FORMAT HELPER FUNCTIONS
fun formatSize(size: Long): String {
    if (size <= 0) return "0.0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.1").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

// Custom sweep brush logic to simulate high-fidelity glows
fun Brush.Companion.swipeBrush(colors: List<Color>, offset: Float): Brush {
    return linearGradient(
        colors = colors,
        start = Offset(offset, 0f),
        end = Offset(offset + 300f, 300f)
    )
}

// Share function helper to invoke Android native shares
fun shareFile(context: Context, file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "com.example.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = context.contentResolver.getType(uri) ?: "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Converted File"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
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
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

private fun getFileSize(context: Context, uri: Uri): Long {
    var result: Long = 0
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (index != -1) {
                    result = cursor.getLong(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    return result
}
