package com.example.jetarticlesapp.datastores

import android.graphics.Bitmap

enum class ArticleType {
    LINK,
    TEXT
}

data class Article(
    val id: Int,
    val type: ArticleType,
    val link: String?,
    val title: String,
    val author: String?,
    val description: String,
    val text: String,
    val datePublished: Long?,
    val dateAdded: Long,
    val image: Bitmap?,
    val imageLink: String?,
    val reminder: Long?
)