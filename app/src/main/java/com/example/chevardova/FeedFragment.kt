package com.example.chevardova

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chevardova.NewPostFragment.Companion.textArg
import com.example.chevardova.adapters.OnInteractionListener
import com.example.chevardova.adapters.PostAdapter
import com.example.chevardova.databinding.FragmentFeedBinding
import com.example.chevardova.posts.Post
import com.example.chevardova.utils.IdArg
import com.example.chevardova.view.PostViewModel

class FeedFragment : Fragment() {

    companion object {
        var Bundle.idArg: Long by IdArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel: PostViewModel by activityViewModels()

        val activityBinding = FragmentFeedBinding.inflate(layoutInflater)

        val adapter = PostAdapter(
            object : OnInteractionListener {
                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController()
                        .navigate(
                            R.id.action_feedFragment_to_newPostFragment,
                            Bundle().apply {
                                textArg = post.content
                            }
                        )
                }

                override fun onShare(post: Post) {
                    viewModel.shareById(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val startIntent = Intent.createChooser(intent, getString(R.string.app_name))

                    startActivity(startIntent)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onDeleteEdit(post: Post) {
                    viewModel.deleteEdit()
                }

                override fun onOpenVideo(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    startActivity(intent)
                }

                override fun onDetailsClicked(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_postDetailFragment,
                        Bundle().apply {
                            idArg = post.id
                        }
                    )
                }
            }
        )
        viewModel.data.observe(viewLifecycleOwner)
        { posts ->
            adapter.submitList(posts)
        }

        activityBinding.list.adapter = adapter

        activityBinding.plus?.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return activityBinding.root
    }
}