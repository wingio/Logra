package xyz.wingio.logra.crashdetector.db

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.wingio.logra.crashdetector.db.entities.Crash

@Database(entities = [Crash::class], version = 1)
abstract class CrashesDatabase : RoomDatabase() {
    abstract fun crashesDao(): CrashesDao
}