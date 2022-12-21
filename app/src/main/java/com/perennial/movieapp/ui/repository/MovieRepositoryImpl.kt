package com.perennial.movieapp.ui.repository

import com.perennial.movieapp.shared.model.data.model.MovieResponse
import com.perennial.movieapp.shared.model.data.remote.MovieAPIService
import retrofit2.Response

class MovieRepositoryImpl(
    private val apiService: MovieAPIService,
) : MovieRepository {

    override suspend fun getPopularMovies(): Response<MovieResponse> {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRatedMovies(): Response<MovieResponse> {
        return apiService.getTopRatedMovies()
    }

    override suspend fun getNowPlayingMovies(): Response<MovieResponse> {
        return apiService.getNowPlayingMovies()
    }

    override suspend fun getMovieReviews(movieId: String): Response<MovieResponse> {
        return apiService.getMovieReviews(movieId)
    }


}