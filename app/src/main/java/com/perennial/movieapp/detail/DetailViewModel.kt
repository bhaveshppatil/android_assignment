package com.perennial.movieapp.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perennial.movieapp.shared.mapper.ReviewListMapper
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.ui.repository.MovieRepository
import com.perennial.movieapp.util.ActionStateLiveData

class DetailViewModel(
    private val repo: MovieRepository,
    reviewListMapper: ReviewListMapper
) : ViewModel() {

    lateinit var movieItem: MovieItemModel

    val movieReview = ActionStateLiveData(viewModelScope, reviewListMapper) {
        repo.getMovieReviews(movieItem.id.toString())
    }

    fun init(movieItem: MovieItemModel) {
        this.movieItem = movieItem
    }
}