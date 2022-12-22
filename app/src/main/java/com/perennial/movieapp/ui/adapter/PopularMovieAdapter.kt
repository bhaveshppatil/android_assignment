package com.perennial.movieapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.perennial.movieapp.databinding.PopularMovieContainerBinding
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.ui.clicklistener.MovieItemClick

class PopularMovieAdapter(
    private val onItemClick: MovieItemClick
) : ListAdapter<MovieItemModel, PopularMovieAdapter.ViewHolder>(MovieItemModel.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            PopularMovieContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], onItemClick)
    }

    inner class ViewHolder(
        private val binding: PopularMovieContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieItemModel, onItemClick: MovieItemClick) = with(binding) {
            root.setOnClickListener { onItemClick.onItemClick(item) }
            ivBanner.load(item.backdropPath) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
            tvTitle.text = item.title
            tvRating.text = item.voteAverage.toString()
        }
    }
}