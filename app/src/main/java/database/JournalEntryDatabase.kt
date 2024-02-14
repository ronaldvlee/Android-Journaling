package database

import androidx.room.Database
import androidx.room.RoomDatabase
import models.JournalEntry

@Database(entities = [JournalEntry::class], version = 1)
abstract class JournalEntryDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao
}