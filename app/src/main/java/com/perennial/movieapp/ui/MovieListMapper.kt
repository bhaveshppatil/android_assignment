package com.perennial.movieapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import com.perennial.movieapp.shared.mapper.Mapper
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.shared.model.data.model.MovieResponse
import com.perennial.movieapp.shared.model.data.remote.NetworkConstant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieListMapper :
    Mapper<MovieResponse, List<MovieItemModel>> {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(dataIn: MovieResponse): List<MovieItemModel> {
        return dataIn.results.orEmpty().map {
            MovieItemModel(
                it.id,
                it.title,
                it.release_date.orEmpty().asReadableDate(),
                it.backdrop_path.orEmpty().asBackdropUrl(),
                it.poster_path.orEmpty().asPosterUrl(),
                it.overview.orEmpty(),
                it.popularity,
                it.vote_average,
                it.vote_count
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.asReadableDate(): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDate = LocalDate.parse(this, formatter)
            val newFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            newFormatter.format(localDate)
        } catch (e: Exception) {
            ""
        }
    }

    private fun String.asPosterUrl() =
        if (this.isEmpty()) NetworkConstant.DEFAULT_POSTER_URL
        else NetworkConstant.IMG_BASE_URL + "w342" + this

    private fun String.asBackdropUrl() =
        if (this.isEmpty()) NetworkConstant.DEFAULT_BACKDROP_URL
        else NetworkConstant.IMG_BASE_URL + "w780" + this
}