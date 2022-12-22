package com.perennial.movieapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.perennial.movieapp.R
import com.perennial.movieapp.databinding.ListItemReviewBinding
import com.perennial.movieapp.shared.model.data.review.ReviewItemModel
import io.noties.markwon.Markwon

class ReviewAdapter :
    ListAdapter<ReviewItemModel, ReviewAdapter.ViewHolder>(ReviewItemModel.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ListItemReviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(
        private val binding: ListItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewItemModel) = with(binding) {
            val markwon = Markwon.create(root.context)
            tvAuthor.text = item.author
            tvContent.apply {
                addShowMoreText("More")
                addShowLessText("Less")
                setShowMoreColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
                setShowLessTextColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
                setShowingLine(19)
            }
            tvContent.text = item.content
//            markwon.setMarkdown(tvContent, item.content.toString())
        }

    }

}