package com.perennial.movieapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perennial.movieapp.ui.MovieListMapper
import com.perennial.movieapp.ui.repository.MovieRepository
import com.perennial.movieapp.util.ActionStateLiveData

class MainViewModel(
    private val repository: MovieRepository,
    movieListMapper: MovieListMapper
): ViewModel(){

    val popularMovies = ActionStateLiveData(viewModelScope, movieListMapper) {
        repository.getPopularMovies()
    }

    val topRatedMovies = ActionStateLiveData(viewModelScope, movieListMapper) {
        repository.getTopRatedMovies()
    }

    val nowPlayingMovies = ActionStateLiveData(viewModelScope, movieListMapper) {
        repository.getNowPlayingMovies()
    }

}