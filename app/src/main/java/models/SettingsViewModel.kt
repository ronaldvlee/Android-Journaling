package models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_team_02_6.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val DEFAULT_LANG_VAL = "en"


class SettingsViewModel : ViewModel() {
    private val _languageValue = MutableLiveData<String>()
    val languageValue: LiveData<String> get() = _languageValue

    private val _backgroundMusicValue = MutableLiveData<String>()
    val backgroundMusicValue: LiveData<String> get() = _backgroundMusicValue

    private val _fontValue = MutableLiveData<String>()

    val fontValue: LiveData<String> get() = _fontValue

    private val _fontIDValue = MutableLiveData<Int>()
    val fontIDValue: LiveData<Int> get() = _fontIDValue

    private val prefs = AppRepository.getRepository()

    init {
        viewModelScope.launch {
            prefs.languageValue.collectLatest {
                _languageValue.value = it
            }
        }
        viewModelScope.launch {
            prefs.backgroundMusicValue.collectLatest {
                _backgroundMusicValue.value = it
            }
        }
        viewModelScope.launch {
            prefs.fontValue.collectLatest {
                _fontValue.value = it
             }
        }
        viewModelScope.launch {
            prefs.fontIDValue.collectLatest {
                _fontIDValue.value = it
            }
        }
    }

    private fun saveValues() {
        viewModelScope.launch {
            prefs.saveLanguageValue(languageValue.value ?: DEFAULT_LANG_VAL)
        }
        viewModelScope.launch {
            prefs.saveBackgroundMusic(backgroundMusicValue.value ?: "default_background_music")
        }
        viewModelScope.launch {
            prefs.saveFont(fontValue.value ?: "Oswald")
        }
        viewModelScope.launch {
            prefs.saveFontID(fontIDValue.value ?: R.font.oswald)
        }
    }

    fun getLanguage(): String {
        return languageValue.value ?: DEFAULT_LANG_VAL
    }

    fun setLanguage(value: String) {
        _languageValue.value = value
        saveValues()
    }

    fun getBackgroundMusic(): String {
        return backgroundMusicValue.value ?: "default_background_music"
    }

    fun setBackgroundMusic(music: String) {
        _backgroundMusicValue.value = music
        saveValues()
    }

    fun getFont(): String {
        return fontValue.value ?: "Oswald"
    }

    fun setFont(font: String) {
        _fontValue.value = font
        saveValues()
    }

    fun getFontID(): Int {
        return fontIDValue.value ?: R.font.oswald
    }

    fun setFontID(fontID: Int) {
        _fontIDValue.value = fontID
        saveValues()
    }
}