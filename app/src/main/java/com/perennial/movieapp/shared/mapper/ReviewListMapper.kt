package com.perennial.movieapp.shared.mapper

import com.perennial.movieapp.shared.model.data.review.MovieReviewResponse
import com.perennial.movieapp.shared.model.data.review.ReviewItemModel
import com.perennial.movieapp.shared.model.data.review.ReviewResult


class ReviewListMapper : Mapper<MovieReviewResponse, List<ReviewItemModel>> {
    override fun map(dataIn: MovieReviewResponse): List<ReviewItemModel> {
        return dataIn.reviewResults.orEmpty().map {
            ReviewItemModel(
                it.author,
                it.content,
                it.created_at,
                it.id,
                it.updated_at,
                it.url
            )
        }
    }

}