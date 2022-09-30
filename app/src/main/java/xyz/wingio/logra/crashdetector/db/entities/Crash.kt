package xyz.wingio.logra.crashdetector.db.entities

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Crash(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "stacktrace") val stacktrace: String
) : Serializable {

    context(Context)
    fun getApp(): ApplicationInfo? = if (packageName != "unknown")
        packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
    else null

}
