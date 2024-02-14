package fragments

import activities.EditEntryActivity
import activities.EntryDetailsActivity
import activities.NewEntryActivity
import adapters.EntriesAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_team_02_6.R
import database.JournalEntryDao
import database.JournalEntryDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.AppRepository
import models.JournalEntry
import models.SettingsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [EntriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class EntriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var entriesAdapter: EntriesAdapter
    private lateinit var entryDao: JournalEntryDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entries, container, false)

        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.recyclerViewEntries)
        entriesAdapter = EntriesAdapter(
            emptyList(),
            { entry -> navigateToDetailsActivity(entry)},
            { entry -> navigateToEditEntryActivity(entry)},
            { entry -> deleteEntry(entry)},
            settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        )

        // Set up RecyclerView with a LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = entriesAdapter

        // Initialize the Room database and get the DAO
        val database = JournalEntryDatabaseBuilder.getInstance(requireContext())
        entryDao = database.journalEntryDao()


        entryDao.getAllEntries().observe(viewLifecycleOwner, Observer { entries ->
            entries?.let {
                entriesAdapter.setData(it)
            }
        })

        // Set up the click listener for the FloatingActionButton
        view.findViewById<View>(R.id.fabAddEntry).setOnClickListener {
            // Open the NewEntryActivity when the plus button is clicked
            val intent = Intent(requireContext(), NewEntryActivity::class.java)
            startActivity(intent)
        }


        return view
    }


    private fun navigateToDetailsActivity(entry: JournalEntry) {
        val intent = Intent(requireContext(), EntryDetailsActivity::class.java)
        intent.putExtra("title", entry.title)
        intent.putExtra("date", entry.date)
        intent.putExtra("description", entry.description)
        startActivity(intent)
    }

    private fun navigateToEditEntryActivity(entry: JournalEntry) {
        val intent = Intent(requireContext(), EditEntryActivity::class.java)
        intent.putExtra("title", entry.title)
        intent.putExtra("date", entry.date)
        intent.putExtra("description", entry.description)
        Log.d("Nav", "Entering Edit Entry Activity")
        startActivity(intent)
    }

    private fun deleteEntry(entry: JournalEntry) {
        Log.d("Delete", "Navigated to EntriesFragment, deleting entry")
        lifecycleScope.launch(Dispatchers.IO) {
            entryDao.deleteEntry(entry)
        }
        Log.d("Delete", "Deleted Entry")
    }

}