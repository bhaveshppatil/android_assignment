package com.perennial.movieapp.ui.repository

import org.koin.dsl.module

val repositoryModule = module {

    single<MovieRepository> { MovieRepositoryImpl(get()) }

}