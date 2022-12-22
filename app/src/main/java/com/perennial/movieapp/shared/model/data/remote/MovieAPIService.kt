package com.perennial.movieapp.shared.model.data.remote

import com.perennial.movieapp.shared.model.data.model.MovieResponse
import com.perennial.movieapp.shared.model.data.review.MovieReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieAPIService {
    @GET("movie/popular?region=IN")
    suspend fun getPopularMovies(): Response<MovieResponse>

    @GET("movie/top_rated?region=IN")
    suspend fun getTopRatedMovies(): Response<MovieResponse>

    @GET("movie/now_playing?region=IN")
    suspend fun getNowPlayingMovies(): Response<MovieResponse>

    @GET("movie/{movieId}/reviews")
    suspend fun getMovieReviews(
        @Path("movieId") movieId: String
    ): Response<MovieReviewResponse>

}