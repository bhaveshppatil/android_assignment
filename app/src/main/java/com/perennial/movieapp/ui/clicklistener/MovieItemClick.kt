package com.perennial.movieapp.ui.clicklistener

import com.perennial.movieapp.shared.model.MovieItemModel

interface MovieItemClick {

    fun onItemClick(result: MovieItemModel)
}