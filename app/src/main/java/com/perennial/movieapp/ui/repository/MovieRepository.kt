package com.perennial.movieapp.ui.repository

import com.perennial.movieapp.shared.model.data.model.MovieResponse
import com.perennial.movieapp.shared.model.data.review.MovieReviewResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MovieRepository {
    suspend fun getPopularMovies(): Response<MovieResponse>
    suspend fun getTopRatedMovies(): Response<MovieResponse>
    suspend fun getNowPlayingMovies(): Response<MovieResponse>
    suspend fun getMovieReviews(movieId: String): Response<MovieReviewResponse>
}