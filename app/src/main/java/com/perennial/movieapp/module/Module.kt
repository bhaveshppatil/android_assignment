package com.perennial.movieapp.module

import com.perennial.movieapp.shared.model.data.remote.networkModule
import com.perennial.movieapp.ui.repository.repositoryModule


val module = listOf(
    networkModule,
    repositoryModule
)