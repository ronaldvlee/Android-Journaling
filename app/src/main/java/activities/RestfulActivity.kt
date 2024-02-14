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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import api.RetrofitInstance
import com.example.final_project_team_02_6.R
import models.AppRepository
import models.SettingsViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import util.FontUtil

class RestfulActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by lazy {
        AppRepository.initialize(this)
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }
    private val toiletApi = RetrofitInstance.api
    private lateinit var textViewGet: TextView
    private lateinit var textViewTitle: TextView
    private lateinit var editTextPost: EditText
    private lateinit var buttonPost: Button
    private lateinit var buttonGet: Button
    private lateinit var buttonBack: ImageButton

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
        setContentView(R.layout.activity_restful)

        textViewGet = findViewById(R.id.textViewGet)
        editTextPost = findViewById(R.id.editTextPost)
        buttonPost = findViewById(R.id.buttonPost)
        textViewTitle = findViewById(R.id.textViewTitle)

        buttonPost.setOnClickListener {
            uploadToiletData(editTextPost.text.toString())
        }

        buttonGet = findViewById(R.id.buttonGet)
        buttonGet.setOnClickListener {
            getToiletData()
        }

        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }

        val audioServiceIntent = Intent(this, AudioService::class.java)
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)

        changeRestfulActivityFont(settingsViewModel.getFontID())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun getToiletData() {
        val call: Call<ResponseBody> = toiletApi.get()

        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val body: ResponseBody? = response.body()
                    var desc: String? = body?.string()
                    Log.d("Restful api", "desc: $desc")
                    desc = desc?.substring(1, desc.length - 1) // trimming the data to remove ""

                    // Use the advice as needed
                    textViewGet.text = desc
                    Log.i("Restful api", "GET Request Success")

                } else {
                    Log.e("Restful api", "GET error")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Restful api", "Api failure", t)
            }
        })
        Toast.makeText(this, "GET Request Sent", Toast.LENGTH_SHORT).show()
    }

    private fun uploadToiletData(data: String) {

        val call: Call<ResponseBody> = toiletApi.upload(data)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Upload successful
                    Log.i("Restful api", "POST Request Success")
                } else {
                    Log.e("Restful api", "POST error " + response.message())
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Restful api", "Api failure", t)
                // Handle network failure
            }
        })
        Toast.makeText(this, "POST Request Sent", Toast.LENGTH_SHORT).show()
    }

    private fun changeRestfulActivityFont(fontResID: Int) {
        FontUtil.setFont(this, textViewGet, fontResID)
        FontUtil.setFont(this, textViewTitle, fontResID)
        FontUtil.setFont(this, editTextPost, fontResID)
        FontUtil.setFont(this, buttonPost, fontResID)
        FontUtil.setFont(this, buttonGet, fontResID)
    }
}