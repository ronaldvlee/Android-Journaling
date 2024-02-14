package database

import android.content.Context
import android.util.Log
import androidx.room.Room

object JournalEntryDatabaseBuilder {

    private var instance: JournalEntryDatabase? = null

    fun getInstance(context: Context): JournalEntryDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also {
                instance = it
                Log.i("AppDatabase", "JournalEntryDatabaseBuilder.getInstance() - database created")
            }
        }
    }

    private fun buildDatabase(context: Context): JournalEntryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            JournalEntryDatabase::class.java,
            "journal_entry_database"
        ).build()
    }
}