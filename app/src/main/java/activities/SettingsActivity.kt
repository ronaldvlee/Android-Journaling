package activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.icu.text.UnicodeSet.EntryRange
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.final_project_team_02_6.R
import fragments.EntriesFragment
import models.AppRepository
import models.SettingsViewModel
import org.w3c.dom.Text
import util.FontUtil
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by lazy {
        AppRepository.initialize(this)
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private lateinit var audioService: AudioService
    private var isServiceBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioService.AudioBinder
            audioService = binder.getService()
            isServiceBound = true
            audioService.play() // Start playback when service is connected
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isServiceBound = false
        }
    }

    private lateinit var fontTextView: TextView
    private lateinit var bgmTextView: TextView
    private lateinit var langTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateLocale(settingsViewModel.getLanguage())

        setContentView(R.layout.activity_settings)

        fontTextView = findViewById(R.id.textViewFontLabel)
        bgmTextView = findViewById(R.id.textViewLanguageLabel)
        langTextView = findViewById(R.id.textViewBGMLabel)

        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        val buttonSave: Button = findViewById(R.id.editUpdateBtn)
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        val musicSpinner: Spinner = findViewById(R.id.musicSpinner)
        val fontSpinner: Spinner = findViewById(R.id.fontSpinner)

        musicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Handle the selected music item
                val selectedMusic = parentView?.getItemAtPosition(position).toString()
                settingsViewModel.setBackgroundMusic(selectedMusic)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }

        val fontOptions = resources.getStringArray(R.array.font_options)
        val fontAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontOptions)
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSpinner.adapter = fontAdapter

        fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedFont = parentView?.getItemAtPosition(position).toString()
                settingsViewModel.setFont(selectedFont)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }

        val languages = arrayOf("en", "es", "fr")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.setSelection(languages.indexOf(settingsViewModel.getLanguage()))

        val musicOptions = resources.getStringArray(R.array.music_options)
        val musicAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, musicOptions)
        musicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        musicSpinner.adapter = musicAdapter

        val currentMusic = settingsViewModel.getBackgroundMusic()
        musicSpinner.setSelection(musicOptions.indexOf(currentMusic))

        val currentFont = settingsViewModel.getFont()
        fontSpinner.setSelection(fontOptions.indexOf(currentFont))

        buttonBack.setOnClickListener {
            onBackPressed()
        }

        buttonSave.setOnClickListener {
            val selectedLanguage = languages[languageSpinner.selectedItemPosition]
            settingsViewModel.setLanguage(selectedLanguage)
            updateLocale(selectedLanguage)

            val selectedMusic = musicOptions[musicSpinner.selectedItemPosition]
            settingsViewModel.setBackgroundMusic(selectedMusic)
            Log.d("Music Change", "Selected Background Music $selectedMusic")

            changeBackgroundMusic(selectedMusic)

            val selectedFont = fontOptions[fontSpinner.selectedItemPosition]
            settingsViewModel.setFont(selectedFont)

            recreate()

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
        }

        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        changeAppFont(settingsViewModel.getFont())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    private fun getMusicResourceId(selectedMusic: String): Int {
        if (selectedMusic == "Acoustic Breeze") {
            return R.raw.acousticbreeze
        }
        else if (selectedMusic == "Dreams") {
            return R.raw.dreams
        }
        else if (selectedMusic == "Hip Jazz") {
            return R.raw.hipjazz
        }
        else if (selectedMusic == "Piano Moment") {
            return R.raw.pianomoment
        }
        else {
            return R.raw.acousticbreeze
            Log.d("Music Change", "Unable to identify new music Switching to Acoustic Breeze")
        }
    }

    private fun getFontResourceId(selectedFont: String): Int {
        if (selectedFont == "Oswald") {
            return R.font.oswald
        }
        else if (selectedFont == "Roboto") {
            return R.font.roboto
        }
        else if (selectedFont == "Silkscreen") {
            return R.font.silkscreen
        }
        else {
            return R.font.oswald
        }
    }

    private fun changeBackgroundMusic(selectedMusic: String) {
        val resourceId = getMusicResourceId(selectedMusic)
        Log.d("Music Change", "Selected Background Music R.id: $resourceId")
        Log.d("Music Change", "Is service bound? $isServiceBound")
        if (isServiceBound) {
            Log.d("Music Change", "Moving to AudioService with R.id. $resourceId")
            audioService.changeBackgroundMusic(resourceId)
        }
        else {
            Log.d("Music Change", "No new music identified")
        }
    }

    private fun changeAppFont(selectedFont: String) {
        val fontResourceId = getFontResourceId(selectedFont)
        settingsViewModel.setFontID(fontResourceId)
        FontUtil.setFont(this, langTextView, fontResourceId)
        FontUtil.setFont(this, bgmTextView, fontResourceId)
        FontUtil.setFont(this, fontTextView, fontResourceId)
    }

    private fun updateLocale(selectedLanguage: String) {
        val newLocale = Locale(selectedLanguage)

        Locale.setDefault(newLocale)

        // Update the app's locale
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}