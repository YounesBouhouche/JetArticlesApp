package com.example.jetarticlesapp.components.main

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jetarticlesapp.activities.ArticleActivity
import com.example.jetarticlesapp.components.common.NavigationBarSpacer
import com.example.jetarticlesapp.datastores.Article
import com.example.jetarticlesapp.functions.search
import com.example.jetarticlesapp.functions.statusBarHeight
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    articles: List<Article>,
    trailingContent: @Composable RowScope.() -> Unit
) {
    val context = LocalContext.current
    val angle by animateFloatAsState(if (active) -360f else 0f, label = "")
    val padding by animateDpAsState(if (active) 0.dp else 32.dp, label = "")
    val titleResult = articles.search(query) { it.title }
    val descriptionResult = articles.search(query) { it.description }
    val textResult = articles.search(query) { it.text }
    val authorResult = articles.search(query) { it.author ?: "" }
    val titleSelected = remember { mutableStateOf(false) }
    val descriptionSelected = remember { mutableStateOf(false) }
    val textSelected = remember { mutableStateOf(false) }
    val authorSelected = remember { mutableStateOf(false) }
    val results = listOf(
        titleResult to titleSelected,
        descriptionResult to descriptionSelected,
        authorResult to authorSelected,
        textResult to textSelected
    )
    val noneSelected by remember { derivedStateOf { !results.all { it.second.value } } }
    val resultsSet = results
            .asSequence()
            .filter { it.second.value or noneSelected }
            .map { it.first }
            .flatten()
            .toSet()
            .toList()
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Spacer(Modifier.height(statusBarHeight + 86.dp))
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {},
            active = active,
            onActiveChange = onActiveChange,
            leadingIcon = {
                IconButton(
                    onClick = { onActiveChange(!active) },
                    modifier = Modifier.rotate(angle)
                ) {
                    AnimatedContent(
                        targetState = active,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = ""
                    ) { target ->
                        if (target)
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null,
                            )
                        else
                            Icon(
                                Icons.Default.Search,
                                null,
                            )
                    }
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = !active,
                    enter = materialFadeThroughIn(),
                    exit = materialFadeThroughOut()
                ) {
                    Row(content = trailingContent)
                }
            },
            placeholder = { Text("Search your Feed") }
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {}
                items(
                    listOf(
                        (Icons.Default.Title to "Title") to titleSelected,
                        (Icons.AutoMirrored.Default.ShortText to "Description") to descriptionSelected,
                        (Icons.AutoMirrored.Default.Article to "Text") to textSelected,
                        (Icons.Default.Person to "Author") to authorSelected
                    )
                ) {
                    FilterChip(
                        selected = it.second.value,
                        onClick = { it.second.value = !it.second.value },
                        label = { Text(it.first.second) },
                        leadingIcon = { com.example.jetarticlesapp.functions.Icon(it.first.first) }
                    )
                }
            }
            AnimatedContent(
                targetState = query.isEmpty(),
                label = "",
                modifier = Modifier.fillMaxSize().imePadding()
            ) { emptyQuery ->
                if (emptyQuery)
                    Column(Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        com.example.jetarticlesapp.functions.Icon(
                            Icons.Default.Search,
                            Modifier.size(100.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Type to search",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                else
                    AnimatedContent(
                        targetState = results.all { it.first.isEmpty() },
                        label = "") { emptyResults ->
                        if (emptyResults)
                            Column(Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                com.example.jetarticlesapp.functions.Icon(
                                    Icons.Default.Search,
                                    Modifier.size(100.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No results",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        else
                            LazyColumn {
                                items(resultsSet, { it.id }) {
                                    ArticleCard(it,
                                        Modifier
                                            .padding(horizontal = 8.dp)
                                            .animateItemPlacement()) {
                                        onActiveChange(false)
                                        context.startActivity(
                                            Intent(context, ArticleActivity::class.java).apply {
                                                putExtra("article", it.id)
                                            }
                                        )
                                    }
                                }
                                item {
                                    NavigationBarSpacer()
                                }
                            }
                    }
            }
        }
    }
}