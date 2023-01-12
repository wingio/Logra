package xyz.wingio.logra.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import xyz.wingio.logra.ui.viewmodels.crashes.CrashDetailViewModel
import xyz.wingio.logra.ui.viewmodels.crashes.CrashesViewModel
import xyz.wingio.logra.ui.viewmodels.main.MainScreenViewModel
import xyz.wingio.logra.ui.viewmodels.settings.icon.IconSettingsViewModel

fun viewModelModule() = module {

    factoryOf(::MainScreenViewModel)
    factoryOf(::CrashesViewModel)
    factoryOf(::CrashDetailViewModel)
    factoryOf(::IconSettingsViewModel)

}