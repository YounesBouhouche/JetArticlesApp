package com.example.jetarticlesapp.datastores

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.jetarticlesapp.functions.dataFlow
import com.example.jetarticlesapp.receiver.AlarmReceiver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class ArticlesDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "articles")
        val ARTICLES_KEY = stringPreferencesKey("articles_key")
        val ASCENDING_KEY = booleanPreferencesKey("ascending_key")
        val SORTING_KEY = booleanPreferencesKey("sorting_key")
        val GRID_SPAN_KEY = intPreferencesKey("grid_span_key")
        val FONT_KEY = intPreferencesKey("font_key")
        val SIZE_KEY = floatPreferencesKey("size_key")
        val DIRECTION_KEY = intPreferencesKey("direction_key")
    }

    val articles: Flow<List<Article>> = context.dataStore.data.map {
        if ((it[ARTICLES_KEY] ?: "").isEmpty()) emptyList()
        else Gson().fromJson(it[ARTICLES_KEY] ?: "", (object : TypeToken<List<Article>>() {}).type)
    }
    val ascending = dataFlow(context.dataStore, ASCENDING_KEY, false)
    val sorting = dataFlow(context.dataStore, SORTING_KEY, true)
    val gridSpan = dataFlow(context.dataStore, GRID_SPAN_KEY, 1)
    val font = dataFlow(context.dataStore, FONT_KEY, 0)
    val size = dataFlow(context.dataStore, SIZE_KEY, 0.5f)
    val direction = dataFlow(context.dataStore, DIRECTION_KEY, 0)

    suspend fun setAscending(asc: Boolean) = context.dataStore.edit {
        it[ASCENDING_KEY] = asc
    }

    suspend fun setSorting(byDate: Boolean) = context.dataStore.edit {
        it[SORTING_KEY] = byDate
    }

    suspend fun setGridSpan(span: Int) = context.dataStore.edit {
        it[GRID_SPAN_KEY] = span
    }

    suspend fun setFont(font: Int) = context.dataStore.edit {
        it[FONT_KEY] = font
    }

    suspend fun setDirection(direction: Int) = context.dataStore.edit {
        it[DIRECTION_KEY] = direction
    }

    suspend fun setSize(size: Float) = context.dataStore.edit {
        it[SIZE_KEY] = size
    }

    private fun getId(list: List<Article>): Int {
        var id = ((Math.random() * list.size) + 2).roundToInt()
        while (list.any { it.id == id }) {
            id = ((Math.random() * list.size) + 2).roundToInt()
        }
        return id
    }

    suspend fun addArticle(
        type: ArticleType,
        link: String?,
        title: String,
        author: String?,
        description: String,
        text: String,
        datePublished: Long?,
        image: Bitmap?,
        imageLink: String?,
        reminder: Long?
    ): Boolean {
        val list = articles.first()
        val id = getId(list)
        reminder?.run {
            if (this <= System.currentTimeMillis()) {
                Toast.makeText(context, "Invalid reminder date and time", Toast.LENGTH_SHORT).show()
                return false
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("text", title)
                    putExtra("id", id)
                },
                PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms())
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder, pendingIntent)
                else {
                    context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    Toast.makeText(
                        context,
                        "Search for JetArticlesApp and grant permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            } else alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder, pendingIntent)
        }
        context.dataStore.edit {
            it[ARTICLES_KEY] = Gson().toJson(list +
                Article(
                    id,
                    type,
                    link,
                    title,
                    author,
                    description,
                    text,
                    datePublished,
                    System.currentTimeMillis(),
                    image,
                    imageLink,
                    reminder
                )
            )
        }
        return true
    }
    suspend fun deleteArticles(ids: Set<Int>) {
        context.dataStore.edit {
            with (articles.first().toMutableList()) {
                ids.forEach { id -> removeIf { it.id == id } }
                it[ARTICLES_KEY] = Gson().toJson(toList())
            }
        }
    }
}