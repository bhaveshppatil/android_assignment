package com.perennial.movieapp.ui

import com.perennial.movieapp.shared.model.MovieItemModel

interface MovieItemClick {

    fun onItemClick(result: MovieItemModel)
}