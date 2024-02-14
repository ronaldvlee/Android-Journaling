package adapters

import activities.SettingsActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_team_02_6.R
import models.JournalEntry
import models.SettingsViewModel
import util.FontUtil

class EntriesAdapter(private var entries: List<JournalEntry>,
                     private val onItemClick: (JournalEntry) -> Unit,
                     private val onEditClick: (JournalEntry) -> Unit,
                     private val onDeleteClick: (JournalEntry) -> Unit,
                     private val settingsViewModel: SettingsViewModel
    ) : RecyclerView.Adapter<EntriesAdapter.EntryViewHolder>() {

    // ViewHolder class to hold references to the views in each entry item
    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)

        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]

        // Bind data to views
        holder.titleTextView.text = entry.title
        holder.dateTextView.text = entry.date

        // Set click listener on the entire item
        holder.itemView.setOnClickListener {
            onItemClick.invoke(entry)
        }

        holder.editButton.setOnClickListener {
            onEditClick.invoke(entry)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick.invoke(entry)
            Log.d("Delete", "Delete pressed, beginning deletion process")
        }

        changeEntriesAdapterFont(holder.titleTextView, settingsViewModel.getFontID())
        changeEntriesAdapterFont(holder.dateTextView, settingsViewModel.getFontID())

    }

    override fun getItemCount(): Int {
        return entries.size
    }
    fun setData(newEntries: List<JournalEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    private fun changeEntriesAdapterFont(textView: TextView, fontResID: Int) {
        FontUtil.setFont(textView.context, textView, fontResID)
    }
}