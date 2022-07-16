package xyz.wingio.logra.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.wingio.logra.di.managerModule
import xyz.wingio.logra.di.viewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@App)

            modules(
                viewModelModule(),
                managerModule()
            )

        }

    }

}