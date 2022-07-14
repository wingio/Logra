package xyz.wingio.logra.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.wingio.logra.domain.manager.PreferenceManager

fun managerModule() = module {

    singleOf(::PreferenceManager)

}