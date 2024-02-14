package activities

import MenuOptionsFragment
import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.final_project_team_02_6.R
import fragments.EntriesFragment
import models.AppRepository
import models.SettingsViewModel
import java.util.Locale
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Environment
import android.os.IBinder
import android.util.DumpableContainer
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import util.FontUtil
import java.io.File

class MainActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by lazy {
        AppRepository.initialize(this)
        ViewModelProvider(this).get(SettingsViewModel::class.java)
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

    private lateinit var fragmentContainer: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("language", settingsViewModel.getLanguage())
        updateLocale(settingsViewModel.getLanguage())
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            0
        )

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val settingsFragment =this.supportFragmentManager.findFragmentById(R.id.menuOptionsLayout)
            if (settingsFragment == null) {
                val fragment = MenuOptionsFragment()
                this.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menuOptionsLayout, fragment)
                    .commit()
            }
        }

        textViewTitle = findViewById(R.id.textViewTitle)
        val buttonSettings: ImageButton = findViewById(R.id.buttonSettings)
        val buttonRestful: ImageButton = findViewById(R.id.buttonMail)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        if (savedInstanceState == null) {
            // Load the EntriesFragment when the activity is created
            loadFragment(EntriesFragment())
        }

        buttonRestful.setOnClickListener {
            val intent = Intent(this, RestfulActivity::class.java)
            startActivity(intent)
        }


        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        changeMainActivityFont(settingsViewModel.getFontID())
        loadImage()
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateLocale(selectedLanguage: String) {
        val newLocale = Locale(selectedLanguage)

        // Update the app's locale
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

    }

    private fun changeMainActivityFont(fontResID: Int) {
        FontUtil.setFont(this, textViewTitle, fontResID)
    }

    private fun loadImage() {
        Log.d("File Import", "Started Load Image Function")
        val storageRoot = Environment.getExternalStorageDirectory()
        val downloadsDir = File(storageRoot, "Download")

        for (f in downloadsDir.listFiles()!!) {
            if(f.isFile) {
                val bytes = f.readBytes()
                val theBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                val drawable: Drawable = BitmapDrawable(resources, theBitmap)

                this.fragmentContainer.setBackgroundDrawable(drawable)
                Log.d("File Import", "Adding Image as Main Activity Background")
            }
        }
    }
}