package com.quijada.campusgps.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "campus_gps_prefs")

class PreferenciasRepositoryDataStore(
    private val context: Context
) : PreferenciasRepository {

    private val KEY_ULTIMO_LUGAR_ID = intPreferencesKey("ultimo_lugar_id")

    override val ultimoLugarId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[KEY_ULTIMO_LUGAR_ID]
    }

    override suspend fun guardarUltimoLugar(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ULTIMO_LUGAR_ID] = id
        }
    }
}
