package com.example.jetarticlesapp.components.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jetarticlesapp.activities.ArticleActivity
import com.example.jetarticlesapp.activities.SettingsActivity
import com.example.jetarticlesapp.components.common.NavigationBarSpacer
import com.example.jetarticlesapp.components.common.RadiosGroup
import com.example.jetarticlesapp.components.common.TopAppBarActions
import com.example.jetarticlesapp.components.common.dialog.Dialog
import com.example.jetarticlesapp.datastores.ArticleType
import com.example.jetarticlesapp.datastores.ArticlesDataStore
import com.example.jetarticlesapp.functions.Icon
import com.example.jetarticlesapp.functions.IconButton
import com.example.jetarticlesapp.functions.articlesDragHandler
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialSharedAxisZ
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun App(shareContent: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val dataStore = ArticlesDataStore(context)
    val articles by dataStore.articles.collectAsState(emptyList())
    val sortByDate by dataStore.sorting.collectAsState(true)
    val gridSpan by dataStore.gridSpan.collectAsState(1)
    val ascending by dataStore.ascending.collectAsState(false)
    val articlesSorted = articles.run {
        (if (sortByDate) sortedBy { it.dateAdded } else sortedBy { it.title }).run {
            if (ascending) this else reversed()
        }
    }
    var dialogVisible by rememberSaveable { mutableStateOf(shareContent.isNotEmpty()) }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    var initialValue by rememberSaveable { mutableStateOf(shareContent) }
    val state = rememberLazyGridState()
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AnimatedContent(
                targetState = inSelectionMode,
                transitionSpec = { materialSharedAxisZ(true) },
                label = "") { selectionMode ->
                if (selectionMode)
                    CenterAlignedTopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedIds.value.size,
                                label = "",
                                transitionSpec = {
                                    (if (targetState > initialState)
                                        slideInVertically { height -> height } + fadeIn() togetherWith
                                                slideOutVertically { height -> -height } + fadeOut()
                                    else
                                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                                slideOutVertically { height -> height } + fadeOut()
                                            ).using(
                                            SizeTransform(clip = false)
                                        )
                                }) {
                                Text("$it Selected")
                            }
                                },
                        actions = {
                            AnimatedVisibility(
                                visible = (articles.filter { selectedIds.value.contains(it.id) }.all { it.type == ArticleType.LINK })
                            ) {
                                IconButton(Icons.Default.Share) {
                                    val shareIntent = Intent.createChooser(
                                        Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, articles
                                                .filter { (selectedIds.value.contains(it.id)) and (it.link != null) }
                                                .joinToString("\n") { it.link!! }
                                            )
                                            type = "text/uri-list"
                                        }, null)
                                    context.startActivity(shareIntent)
                                    selectedIds.value = emptySet()
                                }
                            }
                            IconButton(Icons.Default.Delete) {
                                deleteDialog = true
                            }
                        },
                        navigationIcon = {
                            Checkbox(
                                checked = selectedIds.value.size == articles.size,
                                onCheckedChange = { checked ->
                                    if (checked) selectedIds.value = articles.map { it.id }.toSet()
                                    else selectedIds.value = emptySet()
                                },
                                colors = CheckboxDefaults.colors().copy(
                                    checkedCheckmarkColor = MaterialTheme.colorScheme.primary,
                                    checkedBoxColor = MaterialTheme.colorScheme.surface,
                                    checkedBorderColor = MaterialTheme.colorScheme.surface,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.surface,
                                    uncheckedBoxColor = MaterialTheme.colorScheme.primary,
                                ))
                        },
                        scrollBehavior = scrollBehavior,
                        windowInsets = WindowInsets.systemBars,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            scrolledContainerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.surface,
                            navigationIconContentColor = MaterialTheme.colorScheme.surface,
                            actionIconContentColor = MaterialTheme.colorScheme.surface
                        )
                    )
                else
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        active = active,
                        onActiveChange = { active = it; if (!it) query = "" },
                        articles = articles
                    ) {
                        if (articles.isNotEmpty())
                            TopAppBarActions(
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                icon = Icons.Default.FilterList,
                                dismissWhenClick = true,
                                groups = listOf(
                                    RadiosGroup(
                                        listOf(
                                            Icons.Default.DateRange to "Date Added",
                                            Icons.Default.Title to "Title",
                                        ),
                                        listOf(true, false).indexOf(sortByDate)
                                    ) {
                                        scope.launch { dataStore.setSorting(listOf(true, false)[it]) }
                                    },
                                    RadiosGroup(
                                        listOf(
                                            Icons.Default.ArrowUpward to "Ascending",
                                            Icons.Default.ArrowDownward to "Descending",
                                        ),
                                        listOf(true, false).indexOf(ascending)
                                    ) {
                                        scope.launch { dataStore.setAscending(listOf(true, false)[it]) }
                                    },
                                    with (1..3) {
                                        RadiosGroup(
                                            map {
                                                listOf(
                                                    Icons.AutoMirrored.Default.List,
                                                    Icons.Default.GridView,
                                                    Icons.Default.GridOn,
                                                )[it - 1] to it.toString()
                                            },
                                            gridSpan - 1
                                        ) {
                                            scope.launch { dataStore.setGridSpan(it + 1) }
                                        }
                                    }
                                )
                            )
                        IconButton(Icons.Default.Settings) {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        }
                    }
            }
        },
        floatingActionButton = {
            Column {
                AnimatedVisibility(
                    visible = !(inSelectionMode or active),
                    enter = materialSharedAxisZIn(true),
                    exit = materialSharedAxisZOut(true),
                ) {
                    FloatingActionButton(onClick = { dialogVisible = true }) {
                        Icon(Icons.Default.Add)
                    }
                }
                NavigationBarSpacer()
            }
        }
    ) { paddingValues ->
        if (articles.isEmpty())
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Icon(Icons.AutoMirrored.Default.Article, Modifier.size(100.dp))
                Spacer(Modifier.height(20.dp))
                Text("All your articles will appear here", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        else
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSpan),
                state = state,
                modifier = Modifier.padding(paddingValues)
            ) {
                items(articlesSorted, { it.id }) {
                    ArticleContainer(
                        it,
                        selectedIds.value.contains(it.id),
                        { isSelected ->
                            if (isSelected)
                                selectedIds.value += it.id
                            else
                                selectedIds.value -= it.id
                        },
                        inSelectionMode,
                        Modifier
                            .animateItemPlacement()
                            .articlesDragHandler(
                                state,
                                it.id,
                                selectedIds
                            )
                            .padding(horizontal = (if (gridSpan == 1) 0 else 4).dp),
                        gridSpan
                    ) {
                        context.startActivity(
                            Intent(context, ArticleActivity::class.java).apply {
                                putExtra("article", it.id)
                            }
                        )
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    NavigationBarSpacer()
                }
            }
    }
    Dialog(
        visible = deleteDialog,
        onDismissRequest = { deleteDialog = false },
        title = "Delete articles",
        okListener = {
            scope.launch {
                dataStore.deleteArticles(selectedIds.value)
                selectedIds.value = emptySet()
            }
            deleteDialog = false
        },
        cancelListener = {
            deleteDialog = false
        }
    ) {
        Text("Do you want to delete selected article(s) ?", Modifier.padding(horizontal = 24.dp))
    }
    AnimatedVisibility(dialogVisible, enter = slideInVertically { it }, exit = slideOutVertically { it }) {
        AddArticleDialog(dataStore, initialValue) {
            dialogVisible = false
            initialValue = ""
        }
    }
    BackHandler(dialogVisible) {
        dialogVisible = false
    }
    BackHandler(inSelectionMode) {
        selectedIds.value = emptySet()
    }
}