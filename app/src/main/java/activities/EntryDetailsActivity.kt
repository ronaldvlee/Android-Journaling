package activities

import android.content.ClipDescription
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_project_team_02_6.R
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import models.AppRepository
import models.SettingsViewModel
import org.w3c.dom.Text
import util.FontUtil

class EntryDetailsActivity : AppCompatActivity() {
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
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewDescription: TextView

    private var fontId: Int = R.font.oswald

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)

        textViewTitle = findViewById(R.id.textViewTitle)
        textViewDate = findViewById(R.id.textViewDate)
        textViewDescription = findViewById(R.id.textViewDescription)
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)

        // Retrieve data from intent
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")

        // Set data to views
        textViewTitle.text = title
        textViewDate.text = date
        textViewDescription.text = description

        buttonBack.setOnClickListener {
            // Handle button click to go back to the previous activity
            onBackPressed()
        }

        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        changeEntryDetailsActivityFont(settingsViewModel.getFontID())
    }
    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun changeEntryDetailsActivityFont(fontResID: Int) {
        FontUtil.setFont(this, textViewTitle, fontResID)
        FontUtil.setFont(this, textViewDescription, fontResID)
        FontUtil.setFont(this, textViewDate, fontResID)
    }

}