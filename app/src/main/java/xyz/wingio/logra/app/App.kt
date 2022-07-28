package xyz.wingio.logra.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.wingio.logra.di.managerModule
import xyz.wingio.logra.di.viewModelModule
import xyz.wingio.logra.utils.Logger
import kotlin.system.exitProcess


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Logger.default.error("Error on thread ${thread.name}", throwable)
            exitProcess(1)
        }

        startKoin {

            androidContext(this@App)

            modules(
                viewModelModule(),
                managerModule()
            )

        }

    }

}