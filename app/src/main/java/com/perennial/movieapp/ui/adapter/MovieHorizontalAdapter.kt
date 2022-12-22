package com.perennial.movieapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.perennial.movieapp.databinding.TopRatedListItemBinding
import com.perennial.movieapp.shared.model.MovieItemModel
import com.perennial.movieapp.ui.clicklistener.MovieItemClick
import com.perennial.movieapp.util.NumberFormatter


class MovieHorizontalAdapter(
    private val onItemClick: MovieItemClick
) : ListAdapter<MovieItemModel, MovieHorizontalAdapter.ViewHolder>(
    MovieItemModel.DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            TopRatedListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], onItemClick)
    }

    inner class ViewHolder(
        private val binding: TopRatedListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieItemModel, onItemClick: MovieItemClick) = with(binding) {
            root.setOnClickListener {
                onItemClick.onItemClick(item)
            }
            ivPoster.load(item.posterPath) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f, 16f, 0f, 0f))
            }
            tvTitle.text = item.title
            tvReleaseDate.text = "Release Date\n${item.releaseDate}"
            tvRating.text = item.voteAverage.toString()
            val votes = NumberFormatter.formatSuffix(item.voteCount)
            tvVoteCount.text = "($votes votes)"
        }
    }

}