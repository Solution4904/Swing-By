package app.solution.swing_by

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.solution.swing_by.api.LocalDataConstant
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class LocalDataManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = LocalDataConstant.USER_PREFERENCE)

    suspend fun setString(key: String, string: String) {
        context.dataStore.edit { preference ->
            preference[stringPreferencesKey(key)] = string
        }
    }

    suspend fun getString(key: String): String {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preference ->
                preference[stringPreferencesKey(key)] ?: ""
            }.first()
    }
}