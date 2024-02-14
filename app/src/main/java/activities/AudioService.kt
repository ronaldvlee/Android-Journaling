package activities

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.final_project_team_02_6.R
import android.os.Binder
import android.util.Log


class AudioService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = AudioBinder()
    private var isMuted = false
    var isServiceBound = false

    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.acousticbreeze) // Replace with your audio file
        mediaPlayer.isLooping = true
        isServiceBound = true
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    //toggling the music on / off
    fun toggleMute() {
        if (isMuted) {
            mediaPlayer.setVolume(1f, 1f) // Unmute: set volume to full
            isMuted = false
        } else {
            mediaPlayer.setVolume(0f, 0f) // Mute: set volume to 0
            isMuted = true
        }
    }

    fun changeBackgroundMusic(resourceId: Int) {
        if (isServiceBound) {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer.isLooping = true
            mediaPlayer.start() // Start playback with the new music
            Log.d("Music Change", "New media player changed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        isServiceBound = false
    }


}

