package com.perennial.movieapp.shared.model.data.review

data class MovieReviewResponse(
    val id: Int,
    val page: Int,
    val reviewResults: List<ReviewResult>,
    val total_pages: Int,
    val total_results: Int
)