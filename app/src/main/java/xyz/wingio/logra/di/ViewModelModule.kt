package xyz.wingio.logra.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import xyz.wingio.logra.ui.viewmodels.main.MainScreenViewModel

fun viewModelModule() = module {

    viewModelOf(::MainScreenViewModel)

}