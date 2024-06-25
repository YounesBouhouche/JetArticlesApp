package com.example.jetarticlesapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetarticlesapp.components.common.NavigationBarSpacer
import com.example.jetarticlesapp.components.main.SingleChoiceSegmentedButtons
import com.example.jetarticlesapp.datastores.ArticleType
import com.example.jetarticlesapp.datastores.ArticlesDataStore
import com.example.jetarticlesapp.functions.EnableEdgeToEdge
import com.example.jetarticlesapp.functions.Icon
import com.example.jetarticlesapp.functions.IconButton
import com.example.jetarticlesapp.functions.Slider
import com.example.jetarticlesapp.functions.isScrollingUp
import com.example.jetarticlesapp.ui.theme.AppTheme
import com.example.jetarticlesapp.ui.theme.ReadexPro
import com.example.jetarticlesapp.ui.theme.Serif
import com.example.jetarticlesapp.ui.theme.SerifTypography
import com.example.jetarticlesapp.ui.theme.Typography
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ArticleActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalRichTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("article", 0)
        setContent {
            EnableEdgeToEdge()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val rtState = rememberRichTextState()
            val dataStore = ArticlesDataStore(context)
            val articles by dataStore.articles.collectAsState(emptyList())
            val article = articles.firstOrNull { it.id == id }
            var bottomSheetVisible by remember { mutableStateOf(false) }
            val directions = listOf(
                TextDirection.Content to "Content",
                TextDirection.Ltr to "Ltr",
                TextDirection.Rtl to "Rtl",
            )
            val fonts = listOf(
                (ReadexPro to Typography) to "Sans Serif",
                (Serif to SerifTypography) to "Serif",
            )
            val state = rememberLazyListState()
            val size by dataStore.size.collectAsState(initial = 0.5f)
            val direction by dataStore.direction.collectAsState(initial = 0)
            val font by dataStore.font.collectAsState(initial = 0)
            AppTheme {
                article?.run {
                    rtState.setHtml(text)
                    rtState.setConfig(
                        linkColor = MaterialTheme.colorScheme.primary,
                        linkTextDecoration = TextDecoration.Underline
                    )
                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        "Read Article",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                        },
                                navigationIcon = {
                                    IconButton(Icons.AutoMirrored.Default.ArrowBack) {
                                        finish()
                                    }
                                },
                                actions = {
                                    IconButton(Icons.AutoMirrored.Default.VolumeUp) {
                                    }
                                    if (type == ArticleType.LINK)
                                        IconButton(Icons.AutoMirrored.Default.OpenInNew) {
                                            context.startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(link)
                                                )
                                            )
                                        }
                                    else
                                        IconButton(Icons.Default.Edit) {
                                        }
                                    IconButton(Icons.Default.MoreVert) {
                                        bottomSheetVisible = true
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            Column {
                                AnimatedVisibility(
                                    visible = state.isScrollingUp(),
                                    enter = materialSharedAxisZIn(true),
                                    exit = materialSharedAxisZOut(true)
                                ) {
                                    FloatingActionButton(onClick = { }) {
                                        Icon(Icons.Default.Bolt)
                                    }
                                }
                                NavigationBarSpacer()
                            }
                        }
                    ) {
                        LazyColumn(
                            state = state,
                            modifier = Modifier
                                .padding(it)
                                .padding(12.dp)) {
                            item {
                                Text(
                                    title,
                                    style = fonts[font].first.second.headlineSmall.copy(
                                        textDirection = directions[direction].first,
                                        fontSize =
                                            fonts[font].first.second.headlineSmall.fontSize * size * 2
                                    )
                                )
                            }
                            item {
                                Spacer(Modifier.height(32.dp))
                            }
                            item {
                                RichText(
                                    state = rtState,
                                    fontSize = fonts[font].first.second.bodyLarge.fontSize * size * 2,
                                    fontFamily = fonts[font].first.first,
                                    style = fonts[font].first.second.bodyLarge.copy(
                                        textDirection = directions[direction].first
                                    )
                                )
                            }
                            item {
                                NavigationBarSpacer()
                            }
                        }
                    }
                    if (bottomSheetVisible)
                        ModalBottomSheet(
                            onDismissRequest = { bottomSheetVisible = false },
                            windowInsets = WindowInsets(0, 0, 0, 0)) {
                            Spacer(Modifier.height(16.dp))
                            Text("Text direction", Modifier.padding(horizontal = 16.dp))
                            Spacer(Modifier.height(16.dp))
                            SingleChoiceSegmentedButtons(
                                directions,
                                {
                                    Text(it.second)
                                },
                                direction,
                                { scope.launch { dataStore.setDirection(it) } }
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Font", Modifier.padding(horizontal = 16.dp))
                            Spacer(Modifier.height(16.dp))
                            SingleChoiceSegmentedButtons(
                                fonts,
                                {
                                    Text(it.second)
                                },
                                font,
                                { scope.launch { dataStore.setFont(it) } }
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Font size : ${(size * 200).roundToInt()} %", Modifier.padding(horizontal = 16.dp))
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {
                                Spacer(Modifier.width(16.dp))
                                IconButton(Icons.Default.Remove) {
                                    scope.launch { dataStore.setSize(max(size - .05f, .25f)) }
                                }
                                Spacer(Modifier.width(16.dp))
                                Slider(
                                    value = size,
                                    valueRange = 0.25f..1f,
                                    onValueChange = { scope.launch { dataStore.setSize(it) } },
                                    steps = 15,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                                Spacer(Modifier.width(16.dp))
                                IconButton(Icons.Default.Add) {
                                    scope.launch { dataStore.setSize(min(size + .05f, 1f)) }
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            NavigationBarSpacer()
                        }
                }
            }
        }
    }
}