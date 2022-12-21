package com.perennial.movieapp.ui.home

import com.perennial.movieapp.ui.MovieListMapper
import com.perennial.movieapp.ui.viewModel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mainModule = module {
    single { MovieListMapper() }
    viewModel { MainViewModel(get(), get()) }
}