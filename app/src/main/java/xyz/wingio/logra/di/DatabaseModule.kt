package xyz.wingio.logra.di

import android.content.Context
import androidx.room.Room
import org.koin.dsl.module
import xyz.wingio.logra.crashdetector.db.CrashesDatabase

fun databaseModule() = module {

    fun provideCrashesDb(context: Context) = Room.databaseBuilder(
        context,
        CrashesDatabase::class.java, "crashes"
    ).build()

    single {
        provideCrashesDb(get())
    }

}