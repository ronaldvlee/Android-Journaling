package activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.final_project_team_02_6.R
import database.JournalEntryDao
import database.JournalEntryDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.AppRepository
import models.JournalEntry
import models.SettingsViewModel
import util.FontUtil

class EditEntryActivity: AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by lazy {
        AppRepository.initialize(this)
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private lateinit var entryDao: JournalEntryDao
    private lateinit var editTitleText: EditText
    private lateinit var descEditText: EditText
    private lateinit var deleteButton: Button
    private lateinit var updateButton: Button
    private lateinit var backButton: ImageButton

    private lateinit var title: String
    private lateinit var date: String
    private lateinit var description: String
    private var fontId: Int = R.font.oswald

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)

        val settingsActivity = SettingsActivity()

        title = intent.getStringExtra("title").toString()
        date = intent.getStringExtra("date").toString()
        description = intent.getStringExtra("description").toString()

        editTitleText = findViewById(R.id.editEntryTitle)
        descEditText = findViewById(R.id.editEntryDesc)
        deleteButton = findViewById(R.id.editDelBtn)
        updateButton = findViewById(R.id.editUpdateBtn)
        backButton = findViewById((R.id.imageButton))

        val database = JournalEntryDatabaseBuilder.getInstance(applicationContext)
        entryDao = database.journalEntryDao()

        editTitleText.hint = title
        descEditText.hint = description

        updateButton.setOnClickListener {
            updateEntry()
        }

        deleteButton.setOnClickListener {
            deleteEntry()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        changeEditEntryActivityFont(settingsViewModel.getFontID())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun updateEntry() {
        var newTitle = editTitleText.text.toString()
        var newDescription = descEditText.text.toString()

        if (newTitle.isBlank()) {
            newTitle = title
        }
        if (newDescription.isBlank()) {
            newDescription = description
        }

        lifecycleScope.launch(Dispatchers.IO) {
            entryDao.updateEntry(newTitle, newDescription, date)
            Log.d("Update Entry", "Updating Entry")
        }
        Log.d("Update Entry", "Finish updateEntry")
        finish()
    }

    private fun deleteEntry() {
        val deletingEntry = JournalEntry(title, description, date)

        lifecycleScope.launch(Dispatchers.IO) {
            entryDao.deleteEntry(deletingEntry)
            Log.d("Deleting Entry", "Deleting entry from edit entry act")
        }
        Log.d("Deleting Entry", "Finish deleteEntry")
        finish()
    }

    private fun changeEditEntryActivityFont(fontResID: Int) {
        FontUtil.setFont(this, editTitleText, fontResID)
        FontUtil.setFont(this, descEditText, fontResID)
        FontUtil.setFont(this, deleteButton, fontResID)
        FontUtil.setFont(this, updateButton, fontResID)
    }
}
