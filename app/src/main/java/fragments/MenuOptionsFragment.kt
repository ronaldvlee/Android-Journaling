import activities.RestfulActivity
import activities.SettingsActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.final_project_team_02_6.R
import models.SettingsViewModel
import util.FontUtil

class MenuOptionsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_menu_options, container, false)

        settingsViewModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)

        val buttonSettings = view.findViewById<ImageButton>(R.id.buttonSettings)
        val buttonMail = view.findViewById<ImageButton>(R.id.buttonMail)
        val titleTextView = view.findViewById<TextView>(R.id.textViewTitle)

        buttonMail.setOnClickListener {
            val intent = Intent(activity, RestfulActivity::class.java)
            startActivity(intent)
        }


        buttonSettings.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
        changeMenuOptionsFragmentFont(titleTextView, settingsViewModel.getFontID())
        return view
    }

    private fun changeMenuOptionsFragmentFont(textView: TextView, fontResID: Int) {
        FontUtil.setFont(textView.context, textView, fontResID)
    }
}
