package xyz.wingio.logra.crashdetector.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import xyz.wingio.logra.crashdetector.db.entities.Crash

@Dao
interface CrashesDao {

    @Query("SELECT * FROM crash ORDER BY time DESC")
    fun getAll(): List<Crash>

    @Query("SELECT * FROM crash WHERE id LIKE :id")
    fun getCrash(id: Long): Crash

    @Query("SELECT * FROM crash WHERE package_name LIKE :pkg")
    fun getByPackage(pkg: String): List<Crash>

    @Insert
    fun insert(crash: Crash): Long

    @Query("DELETE FROM crash WHERE id LIKE :id")
    fun deleteCrash(id: Long)

    @Query("DELETE FROM crash")
    fun clear()

}