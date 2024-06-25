package com.example.jetarticlesapp.components.main

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jetarticlesapp.datastores.Article
import com.example.jetarticlesapp.datastores.ArticleType
import com.example.jetarticlesapp.datastores.SettingsDataStore
import com.example.jetarticlesapp.functions.Icon
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import java.net.URI
import java.text.DateFormat

@Composable
fun ArticleCard(
    type: ArticleType,
    link: String?,
    title: String,
    author: String?,
    description: String,
    text: String,
    datePublished: Long?,
    image: Bitmap?,
    imageLink: String?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    gridSpan: Int = 1,
    onClick: () -> Unit = {}
) {
    val isDark = when (SettingsDataStore(LocalContext.current).theme.collectAsState(initial = "system").value) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val p by animateDpAsState(
        targetValue = if (selected) 16.dp else 0.dp,
        label = ""
    )
    val c by animateColorAsState(
        targetValue =
            if (selected)
                if (isDark) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.inversePrimary
            else
                MaterialTheme.colorScheme.surface,
        label = ""
    )
    @Composable
    fun <T> T.runItem(icon: ImageVector, text: (T) -> String = { "$it" }) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, Modifier.size((if (gridSpan == 1) 18 else 12).dp))
            Spacer(Modifier.width(4.dp))
            Text(text(this@runItem), style = MaterialTheme.typography.bodySmall)
        }
    }
    OutlinedCard(onClick = onClick, modifier = modifier.padding(vertical = 8.dp)) {
        val m = if (gridSpan == 1) Modifier.size(100.dp) else Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
        val picture = @Composable {
            Box(m
                    .clip(RoundedCornerShape(8.dp))
                    .clipToBounds()
                    .background(c)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(p)) {
                    Icon(
                        if (type == ArticleType.LINK) Icons.Default.Link else Icons.AutoMirrored.Default.Article,
                        Modifier
                            .fillMaxSize(.5f)
                            .align(Alignment.Center)
                    )
                    if (type == ArticleType.TEXT) {
                        if (image != null)
                            Image(
                                image.asImageBitmap(),
                                null,
                                modifier = Modifier.fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clipToBounds(),
                                contentScale = ContentScale.Crop
                            )
                    }
                    else if (imageLink != null)
                        AsyncImage(
                            model = imageLink,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clipToBounds(),
                            contentScale = ContentScale.Crop
                        )
                }
            }
        }
        if (gridSpan == 1)
            Row(Modifier.padding(16.dp)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    datePublished?.runItem(Icons.Default.DateRange) {
                        "Published on ${DateFormat.getDateInstance().format(it)}"
                    }
                    author?.runItem(Icons.Default.Person) { "By $it" }
                    if (type == ArticleType.LINK) URI(link).host.runItem(Icons.Default.Language)
                }
                Spacer(Modifier.width(8.dp))
                picture()
            }
        else
            Column {
                picture()
                Spacer(Modifier.height(12.dp))
                Column(Modifier.padding(horizontal = 12.dp)) {
                    Text(title, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    datePublished?.runItem(Icons.Default.DateRange) {
                        "Published on ${DateFormat.getDateInstance().format(it)}"
                    }
                    author?.runItem(Icons.Default.DateRange) { "By $it" }
                    if (type == ArticleType.LINK) URI(link).host.runItem(Icons.Default.Language)
                }
            }
        if (gridSpan == 1)
            Text(
                description.ifEmpty { text },
                Modifier.padding(horizontal = 16.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun ArticleCard(
    article: Article,
    modifier: Modifier = Modifier,
    gridSpan: Int = 1,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) = ArticleCard(
    type = article.type,
    link = article.link,
    title = article.title,
    author = article.author,
    description = article.description,
    text = article.text,
    datePublished = article.datePublished,
    image = article.image,
    imageLink = article.imageLink,
    modifier = modifier,
    gridSpan = gridSpan,
    selected = selected,
    onClick = onClick
)

@Composable
fun ArticleContainer(
    article: Article,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    inSelectionMode: Boolean,
    modifier: Modifier = Modifier,
    gridSpan: Int = 1,
    onClick: () -> Unit
) {
    val isDark = when (SettingsDataStore(LocalContext.current).theme.collectAsState(initial = "system").value) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val c by animateColorAsState(
        targetValue =
        if (selected)
            if (isDark) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.inversePrimary
        else
            MaterialTheme.colorScheme.surface,
        label = ""
    )
    if (gridSpan == 1)
        Row(modifier.fillMaxWidth().background(c), verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(
                inSelectionMode,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
                modifier = Modifier.fillMaxHeight(),
            ) {
                Box(
                    Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center) {
                    Checkbox(checked = selected, onCheckedChange = onSelectedChange)
                }
            }
            Spacer(Modifier.width(8.dp))
            ArticleCard(article, Modifier.weight(1f), gridSpan) {
                if (inSelectionMode) onSelectedChange(!selected) else onClick()
            }
            Spacer(Modifier.width(8.dp))
        }
    else
        Box(modifier) {
            ArticleCard(article, gridSpan = gridSpan, selected = selected) {
                if (inSelectionMode) onSelectedChange(!selected) else onClick()
            }
            AnimatedVisibility(
                inSelectionMode,
                enter = materialSharedAxisZIn(true),
                exit = materialSharedAxisZOut(true),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                Checkbox(checked = selected, onCheckedChange = onSelectedChange)
            }
        }
}