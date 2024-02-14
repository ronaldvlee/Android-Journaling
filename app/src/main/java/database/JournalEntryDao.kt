package database

import android.icu.text.Transliterator.Position
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import models.JournalEntry
import java.util.Date

@Dao
interface JournalEntryDao {

    @Insert
    fun insert(entry: JournalEntry)
    //@Update
    //fun update(entry: JournalEntry)
    @Delete
    fun deleteEntry(entry: JournalEntry)
    @Query("SELECT * FROM journal_entries")
    fun getAllEntries(): LiveData<List<JournalEntry>>


    @Query("UPDATE journal_entries SET title = :title, description = :description WHERE date = :date")
    fun updateEntry(title: String, description: String, date: String)
}