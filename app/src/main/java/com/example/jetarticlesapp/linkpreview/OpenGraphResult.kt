package com.example.jetarticlesapp.linkpreview

import org.jsoup.Jsoup
import java.time.Instant

data class OpenGraphResult(
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var image: String? = null,
    var siteName: String? = null,
    var type: String? = null,
    var time: Long? = null,
    var text: String = "",
    var author: String? = null
)

const val AGENT = "Mozilla"
const val REFERRER = "http://www.google.com"
const val TIMEOUT = 10000
const val DOC_SELECT_QUERY = "meta[property^=og:]"
const val OPEN_GRAPH_KEY = "content"
const val PROPERTY = "property"
const val OG_IMAGE = "og:image"
const val OG_DESCRIPTION = "og:description"
const val OG_URL = "og:url"
const val OG_TITLE = "og:title"
const val OG_SITE_NAME = "og:site_name"
const val OG_TYPE = "og:type"
const val ARTICLE_TIME = "article:published_time"
const val ARTICLE_AUTHOR = "article:author"

fun getPreview(link: String, onCompletedListener: OpenGraphResult?.() -> Unit) {
    val openGraphResult = OpenGraphResult()
    Thread {
        try {
            val response = Jsoup.connect(link)
                .ignoreContentType(true)
                .userAgent(AGENT)
                .referrer(REFERRER)
                .timeout(TIMEOUT)
                .followRedirects(true)
                .execute()
            val doc = response.parse()
            val ogTags = doc.select(DOC_SELECT_QUERY)
            if (ogTags.size > 0) {
                ogTags.forEachIndexed { index, _ ->
                    val tag = ogTags[index]
                    val txt = tag.attr(PROPERTY)
                    when (txt) {
                        OG_IMAGE -> {
                            openGraphResult.image = (tag.attr(OPEN_GRAPH_KEY))
                        }
                        OG_DESCRIPTION -> {
                            openGraphResult.description =
                                (tag.attr(OPEN_GRAPH_KEY))
                        }
                        OG_URL -> {
                            openGraphResult.url = (tag.attr(OPEN_GRAPH_KEY))
                        }
                        OG_TITLE -> {
                            openGraphResult.title = (tag.attr(OPEN_GRAPH_KEY))
                        }
                        OG_SITE_NAME -> {
                            openGraphResult.siteName =
                                (tag.attr(OPEN_GRAPH_KEY))
                        }
                        OG_TYPE -> {
                            openGraphResult.type = (tag.attr(OPEN_GRAPH_KEY))
                        }
                        ARTICLE_TIME -> {
                            openGraphResult.time = Instant.parse(tag.attr(OPEN_GRAPH_KEY)).toEpochMilli()
                        }
                        ARTICLE_AUTHOR -> {
                            openGraphResult.author = tag.attr(OPEN_GRAPH_KEY)
                        }
                    }
                }
            }
            val main = (doc.selectFirst("main") ?: doc.body())
            main.children().removeIf { it.`is`("nav,footer,aside") }
            openGraphResult.text = main.html()
            onCompletedListener(openGraphResult)
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletedListener(null)
        }
    }.start()
}