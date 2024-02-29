package com.example.chevardova.adapters

import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chevardova.R
import com.example.chevardova.databinding.CardPostBinding
import com.example.chevardova.posts.Post
import com.example.chevardova.posts.WallService.updateNumber

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding){

            nameBTPIT.text = post.author
            dateOfPublished.text = post.published
            textOfPost.text = post.content

            likeCheckBox?.isChecked = post.likedByMe

            textShares.text = updateNumber(post.sharetxt)
            textLikes.text = updateNumber(post.liketxt)
            likeCheckBox?.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareCheckBox?.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            videoContent?.isVisible = !post.video.isNullOrBlank()
            treedots?.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            play?.setOnClickListener {
                onInteractionListener.onOpenVideo(post)
            }
            binding.root.setOnClickListener {
                onInteractionListener.onDetailsClicked(post)
            }
        }
    }
}
