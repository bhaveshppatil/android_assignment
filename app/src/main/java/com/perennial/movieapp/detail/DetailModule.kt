package com.perennial.movieapp.detail

import com.perennial.movieapp.shared.mapper.ReviewListMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailModule = module {
    single { ReviewListMapper() }
    viewModel { DetailViewModel(get(), get()) }

}