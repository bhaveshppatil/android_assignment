package com.perennial.movieapp.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import coil.api.load
import com.perennial.movieapp.R
import com.perennial.movieapp.databinding.ActivityMovieDetailBinding
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.ui.adapter.ReviewAdapter
import com.perennial.movieapp.util.UIState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        reviewAdapter = ReviewAdapter()
        var intent = Intent()
        intent = getIntent()
        val response = intent.getParcelableExtra<MovieItemModel>("movieItemModel")
        if (response != null) {
            setupUI(response)
            viewModel.init(response)
        }
        setupRecyclerView()
        setupObserver()
        setupListener()
        viewModel.movieReview.load()
    }

    private fun setupUI(movieItem: MovieItemModel) {
        val view = findViewById<View>(R.id.layout_image_poster)
        val ivDetailPoster = view.findViewById<ImageView>(R.id.iv_detail_poster)

        with(binding) {
            toolbar.title = movieItem.title
            ivBackdrop.load(movieItem.backdropPath) {
                crossfade(true)
                placeholder(R.drawable.avatar)
            }
            ivDetailPoster.load(movieItem?.posterPath) {
                crossfade(true)
                placeholder(R.drawable.avatar)
            }
            tvDetailRating.text = movieItem.voteAverage.toString()
            tvDetailReleaseDate.text = movieItem.releaseDate
            tvDetailVoteCount.text = "${movieItem.voteCount} votes"
            tvDetailDesc.apply {
                addShowMoreText(getString(R.string.label_text_expand))
                addShowLessText(getString(R.string.label_text_collapse))
                setShowMoreColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
                setShowLessTextColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
                setShowingLine(9)
            }
            tvDetailDesc.text = movieItem.overview
        }
    }

    private fun setupRecyclerView() {
        binding.rvReviews.apply {
            addItemDecoration(
                DividerItemDecoration(this@MovieDetailActivity, DividerItemDecoration.VERTICAL)
            )
            adapter = this@MovieDetailActivity.reviewAdapter

        }
    }

    private fun setupObserver() {
        viewModel.movieReview.state.observe(this) {
            when (it) {
                UIState.Loading -> {
                    showLoading()
                }
                is UIState.Success -> {
                    with(binding) {
                        hideLoading()
                        rvReviews.visibility = View.VISIBLE
                        it.data.let {
                            tvReviewTitle.text = "Review (${it?.size})"
                            reviewAdapter.submitList(it)
                            if (it?.isEmpty() == true) viewEmptyReview.visibility = View.VISIBLE
                        }
                    }
                }
                is UIState.Failure -> {
                    hideLoading()
                    binding.layoutReviewError.rootView.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun setupListener() {
        val layout = findViewById<View>(R.id.layout_review_error)
        val btnReload = layout.findViewById<Button>(R.id.btn_reload)
        btnReload.setOnClickListener {
            viewModel.movieReview.load()
        }
    }

    private fun showLoading() {
        binding.shimmerReview.visibility = View.VISIBLE
        binding.shimmerReview.startShimmer()
        binding.rvReviews.visibility = View.GONE
        binding.viewEmptyReview.visibility = View.GONE
        binding.layoutReviewError.rootView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.shimmerReview.visibility = View.GONE
        binding.shimmerReview.stopShimmer()
    }
}