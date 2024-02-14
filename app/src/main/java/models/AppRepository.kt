package models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.final_project_team_02_6.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository private constructor(private val dataStore: DataStore<Preferences>) {
    private val languageValueKey = stringPreferencesKey("languageValue")
    private val backgroundMusicValueKey = stringPreferencesKey("backgroundMusic")
    private val fontValueKey = stringPreferencesKey("font")
    private val fontIDValueKey = intPreferencesKey("fontID")

    val languageValue: Flow<String> = this.dataStore.data.map { prefs ->
        prefs[languageValueKey] ?: DEFAULT_LANG_VAL
    }

    val backgroundMusicValue: Flow<String> = this.dataStore.data.map { prefs ->
        prefs[backgroundMusicValueKey] ?: "default_background_music"
    }

    val fontValue: Flow<String> = this.dataStore.data.map { prefs ->
        prefs[fontValueKey] ?: "Oswald"
    }

    val fontIDValue: Flow<Int> = this.dataStore.data.map { prefs ->
        prefs[fontIDValueKey] ?: R.font.oswald
    }

    suspend fun saveLanguageValue(value: String) {
        saveStringValue(languageValueKey, value)
    }

    suspend fun saveBackgroundMusic(value: String) {
        saveStringValue(backgroundMusicValueKey, value)
    }

    suspend fun saveFont(value: String) {
        saveFontValue(fontValueKey, value)
    }

    suspend fun saveFontID(value: Int) {
        saveFontIDValue(fontIDValueKey, value)
    }

    private suspend fun saveStringValue(key: Preferences.Key<String>, value: String) {
        this.dataStore.edit {preferences ->
            preferences[key] = value
        }
    }

    private suspend fun saveFontValue(key: Preferences.Key<String>, value: String) {
        this.dataStore.edit {preferences ->
            preferences[key] = value
        }
    }

    private suspend fun saveFontIDValue(key: Preferences.Key<Int>, value: Int) {
        this.dataStore.edit {preferences ->
            preferences[key] = value
        }
    }
    companion object {
        private const val PREFERENCES_DATA_FILE_NAME = "DataStoreFile"
        private var singleInstanceOfMyAppRepository: AppRepository? = null
        fun initialize(context: Context) {
            if (singleInstanceOfMyAppRepository == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile(PREFERENCES_DATA_FILE_NAME)
                }
                singleInstanceOfMyAppRepository = AppRepository(dataStore)
            }
        }
        fun getRepository(): AppRepository {
            return singleInstanceOfMyAppRepository ?: throw IllegalStateException("singleInstanceOfMyAppRepository is not initialized yet")
        }
    }
}