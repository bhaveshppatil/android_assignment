package com.perennial.movieapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.perennial.movieapp.R
import com.perennial.movieapp.databinding.ActivityMainBinding
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.ui.MovieItemClick
import com.perennial.movieapp.ui.adapter.MovieHorizontalAdapter
import com.perennial.movieapp.ui.adapter.PopularMovieAdapter
import com.perennial.movieapp.ui.viewModel.MainViewModel
import com.perennial.movieapp.util.UIState
import com.perennial.movieapp.util.observeStateOf
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), MovieItemClick {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    private lateinit var popularMovieAdapter: PopularMovieAdapter
    private lateinit var topRatedAdapter: MovieHorizontalAdapter
    private lateinit var nowPlayingAdapter: MovieHorizontalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        popularMovieAdapter = PopularMovieAdapter(this)
        topRatedAdapter = MovieHorizontalAdapter(this)
        nowPlayingAdapter = MovieHorizontalAdapter(this)
        setUpRecyclerView()
        setUpObserver()
        setupListener()
        initData()
    }

    private fun initData() {
        viewModel.popularMovies.load()
        viewModel.nowPlayingMovies.load()
        viewModel.topRatedMovies.load()
    }

    private fun setUpRecyclerView() {
        with(binding) {
            rvPopular.adapter = popularMovieAdapter
            rvNowPlaying.adapter = nowPlayingAdapter
            rvTopRated.adapter = topRatedAdapter
        }
    }

    private fun setUpObserver() {

        observeStateOf(viewModel.popularMovies, ::handlePopularState)
        observeStateOf(viewModel.topRatedMovies, ::handleTopRatedState)
        observeStateOf(viewModel.nowPlayingMovies, ::handleNowPlayingState)
    }

    private fun setupListener() {
        val view = findViewById<View>(R.id.layout_popular_error)
        val view1 = findViewById<View>(R.id.layout_now_playing_error)
        val view2 = findViewById<View>(R.id.layout_top_rated_error)
        val popularErrorBtn =  view.findViewById<Button>(R.id.btn_reload)
        val nowPlayingBtn =  view1.findViewById<Button>(R.id.btn_reload)
        val topRatedBtn =  view2.findViewById<Button>(R.id.btn_reload)

        with(binding) {
            popularErrorBtn.setOnClickListener {
                viewModel.popularMovies.load()
            }
            topRatedBtn.setOnClickListener {
                viewModel.topRatedMovies.load()
            }
            nowPlayingBtn.setOnClickListener {
                viewModel.nowPlayingMovies.load()
            }
        }
    }

    private fun handlePopularState(state: UIState<List<MovieItemModel>?>) {
        when (state) {
            UIState.Loading -> {
                with(binding) {
                    showLoading(shimmerPopular, rvPopular, binding.layoutPopularError.rootView)
                }
            }
            is UIState.Success -> {
                with(binding) {
                    hideLoading(shimmerPopular, rvPopular)
                    state.data?.let { popularMovieAdapter.submitList(it) }
                }
            }
            is UIState.Failure -> {
                with(binding) {
                    hideLoading(shimmerPopular, rvPopular, false)
                    binding.layoutPopularError.rootView.visibility = View.VISIBLE
                }
            }
            else -> {}
        }
    }

    private fun handleTopRatedState(state: UIState<List<MovieItemModel>?>) {
        when (state) {
            UIState.Loading -> {
                with(binding) {
                    showLoading(shimmerTopRated, rvTopRated, binding.layoutTopRatedError.rootView)
                }
            }
            is UIState.Success -> {
                with(binding) {
                    hideLoading(shimmerTopRated, rvTopRated)
                    state.data?.let { topRatedAdapter.submitList(it) }
                }
            }
            is UIState.Failure -> {
                with(binding) {
                    hideLoading(shimmerTopRated, rvTopRated, false)
                    binding.layoutTopRatedError.rootView.visibility = View.VISIBLE
                }
            }
            else -> {}
        }
    }

    private fun handleNowPlayingState(state: UIState<List<MovieItemModel>?>) {
        when (state) {
            UIState.Loading -> {
                with(binding) {
                    showLoading(
                        shimmerNowPlaying,
                        rvNowPlaying,
                        binding.layoutNowPlayingError.rootView
                    )
                }
            }
            is UIState.Success -> {
                with(binding) {
                    hideLoading(shimmerNowPlaying, rvNowPlaying)
                    state.data?.let { nowPlayingAdapter.submitList(it) }
                }
            }
            is UIState.Failure -> {
                with(binding) {
                    hideLoading(shimmerNowPlaying, rvNowPlaying, false)
                    binding.layoutNowPlayingError.rootView.visibility = View.VISIBLE
                }
            }
            else -> {}
        }
    }


    private fun showLoading(
        shimmerView: ShimmerFrameLayout,
        recyclerView: RecyclerView,
        errorView: View
    ) {
        shimmerView.visibility = View.VISIBLE
        shimmerView.startShimmer()
        recyclerView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    private fun hideLoading(
        shimmerView: ShimmerFrameLayout,
        recyclerView: RecyclerView,
        showRv: Boolean = true
    ) {
        shimmerView.visibility = View.GONE
        shimmerView.stopShimmer()
        if (showRv) recyclerView.visibility = View.VISIBLE
    }


    override fun onItemClick(result: MovieItemModel) {

    }

}