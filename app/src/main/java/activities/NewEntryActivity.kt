package activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import models.JournalEntry
import database.JournalEntryDao
import database.JournalEntryDatabaseBuilder
import com.example.final_project_team_02_6.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.AppRepository
import models.SettingsViewModel
import util.FontUtil

class NewEntryActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by lazy {
        AppRepository.initialize(this)
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private lateinit var entryDao: JournalEntryDao
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var backButton: ImageButton
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
        setContentView(R.layout.activity_new_entry)

        editTextTitle = findViewById<EditText>(R.id.editEntryTitle)
        editTextDescription = findViewById<EditText>(R.id.editEntryDesc)
        backButton = findViewById<ImageButton>(R.id.imageBackButton)

        // Initialize the Room database and get the DAO
        val database = JournalEntryDatabaseBuilder.getInstance(applicationContext)
        entryDao = database.journalEntryDao()

        val buttonSave: Button = findViewById<Button>(R.id.editUpdateBtn)
        buttonSave.setOnClickListener {
            saveEntry()
        }

        backButton.setOnClickListener{
            onBackPressed()
        }
        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        changeNewEntryActivityFont(settingsViewModel.getFontID())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun saveEntry() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())

        // Validate that title is not empty
        if (title.isNotBlank()) {
            val newEntry = JournalEntry(title, description, currentDate)

            // Insert the new entry into the database
            lifecycleScope.launch(Dispatchers.IO) {
                entryDao.insert(newEntry)
            }

            // Navigate back to the previous activity (EntriesFragment)
            finish()
        } else {
            // Display an error or inform the user that the title is required
            // You can customize this based on your application's requirements
            // For simplicity, you may want to show a Toast message
        }
    }

    private fun changeNewEntryActivityFont(fontResID: Int) {
        FontUtil.setFont(this, editTextTitle, fontResID)
        FontUtil.setFont(this, editTextDescription, fontResID)
    }
}