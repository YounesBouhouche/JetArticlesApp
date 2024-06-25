package com.example.jetarticlesapp.functions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> dataFlow(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, default: T): Flow<T> =
    dataStore.data.map {
        it[key] ?: default
    }

fun <T> listDataFlow(dataStore: DataStore<Preferences>, key: Preferences.Key<String>): Flow<List<T>> =
    dataStore.data.map {
        if ((it[key] ?: "").isEmpty()) emptyList()
        else Gson().fromJson(it[key] ?: "", (object : TypeToken<List<T>>() {}).type)
    }
