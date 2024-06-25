package com.example.jetarticlesapp.components.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Looks3
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetarticlesapp.components.common.NavigationBarSpacer
import com.example.jetarticlesapp.components.common.TextField
import com.example.jetarticlesapp.components.common.dialog.Dialog
import com.example.jetarticlesapp.datastores.ArticleType
import com.example.jetarticlesapp.datastores.ArticlesDataStore
import com.example.jetarticlesapp.functions.Icon
import com.example.jetarticlesapp.functions.IconButton
import com.example.jetarticlesapp.linkpreview.OpenGraphResult
import com.example.jetarticlesapp.linkpreview.getPreview
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialSharedAxisZ
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import java.net.URI
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArticleDialog(
    dataStore: ArticlesDataStore,
    initialValue: String? = null,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val regex = Regex("^(https?://)?([a-zA-Z0-9-.]+\\.[a-zA-Z]{2,6})([/\\w_.\\-~]*)*(\\?\\S*)?\$")
    val pagerState = rememberPagerState { 2 }
    val currentPage = pagerState.currentPage
    var link by remember { mutableStateOf("") }
    var preview by remember { mutableStateOf<OpenGraphResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val rteState = rememberRichTextState()
    val currentSpanStyle = rteState.currentSpanStyle
    val h1Active = (currentSpanStyle.fontSize == 48.sp) and (currentSpanStyle.fontWeight == FontWeight.Black)
    val h2Active = (currentSpanStyle.fontSize == 36.sp) and (currentSpanStyle.fontWeight == FontWeight.SemiBold)
    val h3Active = (currentSpanStyle.fontSize == 28.sp) and (currentSpanStyle.fontWeight == FontWeight.SemiBold)
    var authorEnabled by remember { mutableStateOf(true) }
    var authorError by remember { mutableStateOf(false) }
    var author by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<Bitmap?>(null) }
    var showDateDialog by remember { mutableStateOf(false) }
    var dateEnabled by remember { mutableStateOf(true) }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var reminder by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())
    val reminderDatePickerState = rememberDatePickerState(System.currentTimeMillis())
    val reminderTimePickerState = rememberTimePickerState(
        (((System.currentTimeMillis() / (1000 * 60 * 60)) % 24).toInt() + 1) % 24,
        ((System.currentTimeMillis() / (1000 * 60)) % 60).toInt()
    )
    var focused by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var textError by remember { mutableStateOf(false) }
    var linkError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    fun loadPreview() {
        loading = true
        getPreview(link) {
            preview = this
            loading = false
            if (this == null) {
                scope.launch {
                    if (snackbarHostState
                            .showSnackbar(
                                message = "Error loading link preview",
                                actionLabel = "Retry",
                                withDismissAction = true,
                                duration = SnackbarDuration.Indefinite
                            ) == SnackbarResult.ActionPerformed) {
                        loadPreview()
                    }
                }
            }
        }
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.run {
                image = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        this
                    )
                )
            }
        }
    val options = @Composable {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    if (reminder == null) showReminderDialog = true
                    else reminder = null
                },
            verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(8.dp))
            Checkbox(
                checked = reminder != null,
                onCheckedChange = {
                    if (it) showReminderDialog = true else reminder = null
                }
            )
            Text("Use reminder ")
            reminder?.run {
                Text("(${DateFormat.getDateTimeInstance().format(reminder)})")
            }
        }
    }
    LaunchedEffect(Unit) {
        initialValue?.run {
            if (isEmpty()) return@run
            if (regex.matches(this)) {
                scope.launch { pagerState.scrollToPage(0) }
                link = this
            } else {
                scope.launch { pagerState.scrollToPage(1) }
                if (this.lines().size > 1) description = this else title = this
            }
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Article") },
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .imePadding()) {
                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = focused,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(Icons.Default.FormatBold, true, currentSpanStyle.fontWeight == FontWeight.Bold) {
                            rteState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        }
                        IconButton(Icons.Default.FormatItalic, true, currentSpanStyle.fontStyle == FontStyle.Italic) {
                            rteState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        }
                        IconButton(Icons.Default.FormatUnderlined, true, currentSpanStyle.textDecoration == TextDecoration.Underline) {
                            rteState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        }
                        IconButton(Icons.Default.LooksOne, true, h1Active) {
                            if (h1Active)
                                rteState.toggleSpanStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal))
                            else
                                rteState.addSpanStyle(SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Black))
                        }
                        IconButton(Icons.Default.LooksTwo, true, h2Active) {
                            if (h2Active)
                                rteState.toggleSpanStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal))
                            else
                                rteState.addSpanStyle(SpanStyle(fontSize = 36.sp, fontWeight = FontWeight.SemiBold))
                        }
                        IconButton(Icons.Default.Looks3, true, h3Active) {
                            if (h3Active)
                                rteState.toggleSpanStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal))
                            else
                                rteState.addSpanStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (currentPage == 0) {
                                if (regex.matches(link)) {
                                    scope.launch {
                                        if (dataStore.addArticle(
                                            ArticleType.LINK,
                                            link,
                                            preview?.title ?: URI(link).host,
                                            preview?.author,
                                            preview?.description ?: "",
                                            preview?.text ?: "",
                                            preview?.time,
                                            null,
                                            preview?.image,
                                            reminder
                                        )) onDismissRequest()
                                    }
                                }
                                else linkError = true
                            } else if (currentPage == 1) {
                                if (title.isEmpty()) titleError = true
                                else if (rteState.toHtml().isEmpty()) textError = true
                                else if (authorEnabled and author.isEmpty()) authorError = true
                                else {
                                    scope.launch {
                                        if (dataStore.addArticle(
                                            ArticleType.TEXT,
                                            null,
                                            title,
                                            if (authorEnabled) author else null,
                                            description,
                                            rteState.toHtml(),
                                            if (dateEnabled) date else null,
                                            image,
                                            null,
                                            reminder
                                        )) onDismissRequest()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = (currentPage == 1) or (preview != null)
                    ) {
                        Text("OK")
                    }
                }
                Spacer(Modifier.height(8.dp))
                NavigationBarSpacer()
            }
            Spacer(Modifier.height(16.dp))
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
            SecondaryTabRow(currentPage) {
                listOf(
                    "Link to article" to Icons.Default.Link,
                    "Personal Article" to Icons.AutoMirrored.Default.Article
                ).forEachIndexed { index, item ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text =  { Text(item.first) },
                        icon = { Icon(item.second) }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)) { page ->
                when (page) {
                    0 -> {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())) {
                            TextField(
                                value = link,
                                onValueChange = { link = it; linkError = false },
                                icon = Icons.Default.Link,
                                isError = linkError,
                                label = "Link to article",
                                supportingText = if (linkError) "Invalid link" else null,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            )
                            LaunchedEffect(link) {
                                if (regex.matches(link))
                                    loadPreview()
                                else preview = null
                            }
                            AnimatedVisibility(
                                visible = (preview != null) or loading,
                                label = "",
                                enter = materialSharedAxisZIn(true),
                                exit = materialSharedAxisZOut(true),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AnimatedContent(
                                    targetState = loading,
                                    transitionSpec = { materialSharedAxisZ(true) },
                                    label = "") {
                                    if (it)
                                        LinearProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    else
                                        preview?.run {
                                            ArticleCard(
                                                ArticleType.LINK,
                                                link,
                                                this.title ?: "Article title",
                                                null,
                                                this.description ?: "Article description",
                                                "",
                                                this.time,
                                                null,
                                                this.image,
                                                Modifier.padding(horizontal = 16.dp)
                                            ) {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(link)
                                                    )
                                                )
                                            }
                                        }
                                }
                            }
                            options()
                        }
                    }
                    1 -> {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())) {
                            Spacer(Modifier.height(16.dp))
                            TextField(
                                value = title,
                                onValueChange = { title = it; titleError = false },
                                icon = Icons.Default.Title,
                                label = "Title",
                                isError = titleError,
                                supportingText = if (titleError) "Title should not be empty" else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                icon = Icons.AutoMirrored.Default.ShortText,
                                label = "Description (Optional)",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(2f)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(checked = authorEnabled, onCheckedChange = { authorEnabled = it })
                                        TextField(
                                            value = author,
                                            enabled = authorEnabled,
                                            onValueChange = { author = it; authorError = false },
                                            isError = authorError,
                                            supportingText =
                                                if (authorError) "Author should not be empty"
                                                else null,
                                            icon = Icons.Default.Person,
                                            label = "Author",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(checked = dateEnabled, onCheckedChange = { dateEnabled = it })
                                        OutlinedTextField(
                                            value = DateFormat.getDateInstance().format(date),
                                            onValueChange = { },
                                            enabled = dateEnabled,
                                            readOnly = true,
                                            leadingIcon = { Icon(Icons.Default.DateRange) },
                                            trailingIcon = {
                                                IconButton(Icons.AutoMirrored.Default.OpenInNew) {
                                                    showDateDialog = true
                                                }
                                            },
                                            label = { Text("Publish date") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                                Spacer(Modifier.width(16.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clip(RoundedCornerShape(16.dp))
                                        .clipToBounds()
                                        .clickable {
                                            launcher.launch("image/*")
                                        }
                                ) {
                                    image?.run {
                                        Image(
                                            bitmap = asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (image == null)
                                        Icon(Icons.Default.AddAPhoto, Modifier.align(Alignment.Center))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            OutlinedRichTextEditor(
                                state = rteState,
                                placeholder = { Text("Text") },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .onFocusEvent { focused = it.isFocused }
                            )
                            Spacer(Modifier.height(16.dp))
                            options()
                        }
                    }
                }
            }
        }
    }
    if (showDateDialog) {
        datePickerState.selectedDateMillis = date
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDateDialog = false
                    datePickerState.selectedDateMillis?.run { date = this }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showDateDialog = false
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Dialog(
        visible = showReminderDialog,
        onDismissRequest = { showReminderDialog = false },
        okListener = {
            if (reminderDatePickerState.selectedDateMillis == null)
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            else if (reminderDatePickerState.selectedDateMillis!!
                    .plus((reminderTimePickerState.hour - 1L) * 3600 * 1000)
                    .plus(reminderTimePickerState.minute * 60 * 1000) <= System.currentTimeMillis())
                Toast.makeText(context, "Invalid date and time", Toast.LENGTH_SHORT).show()
            else {
                reminder =
                    reminderDatePickerState.selectedDateMillis!!
                        .plus((reminderTimePickerState.hour - 1L) * 3600 * 1000)
                        .plus(reminderTimePickerState.minute * 60 * 1000)
                showReminderDialog = false
            }
        },
        cancelListener = {
            showReminderDialog = false
        },
        title = "Reminder",
        contentHorizontalAlignment = Alignment.CenterHorizontally
    ) {
        DatePicker(state = reminderDatePickerState)
        TimeInput(state = reminderTimePickerState)
    }
}