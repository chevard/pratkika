package com.example.chevardova

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chevardova.databinding.ActivityMainBinding
import java.util.Random
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                nameBTPIT.text = post.author
                dateOfPublished.text = post.published
                textOfPost.text = post.content
                likeBtn.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.heartredd
                    } else {
                        R.drawable.heartblack
                    }
                )
                textLikes.text = updateLikes(post.likes)
                textShares.text = updateShare(post.sharess)
            }

            binding.likeBtn.setOnClickListener() {
                Toast.makeText(this, "Вы лайкнули пост", Toast.LENGTH_SHORT).show()
                viewModel.like()
            }
            binding.shareBtn.setOnClickListener() {
                Toast.makeText(this, "Вы сделали репост", Toast.LENGTH_SHORT).show()
                viewModel.share()
            }
        }

    }

    private fun updateLikes(likes: Int):String {
        return when (likes)
        {
            in 1000..999998 -> {
                String.format("%.1fK", likes / 1000.0)
            }
            in 999999 .. 999999999 -> {
                String.format("%.1fM", likes / 1000000.0)
            }
            else -> likes.toString()
            }
        }

    }
private fun updateShare(share: Int):String {
    return when (share)
    {
        in 1000..999998 -> {
            String.format("%.1fK", share / 1000.0)
        }
        in 999999 .. 999999999 -> {
            String.format("%.1fM", share / 1000000.0)
        }
        else -> share.toString()
    }
}
    data class Post(
        val id: Long,
        val author: String,
        val content: String,
        val published: String,
        val likedByMe: Boolean,
        var likes: Int,
        var sharess: Int,
        var shareByMe: Boolean
    )
    interface PostRepository {
        fun get(): LiveData<Post>
        fun like()
        fun share()
    }
    class PostRepositoryInMemoryImpl : PostRepository {
        public var post = Post(
            id = 1,
            author = "ГБПОУ ВО 'БТПИТ'",
            content = "Студенты ГБПОУ ВО 'БТПИТ' по профессии Сварщик ручной и частично механизированной сварки ( наплавки),под руководством педагогов Завидовской Н.И., Новокрещенова Д.В., приняли участие в региональной научно-практической студенческой конференции «Будущее сварки уже наступило», организованной ГБПОУ ВО «ВТПСТ», которая проходила с 22 января по 8 февраля 2024 года .Наши студенты-участники конференции поделились своим видением современных тенденций и проблем рынка сварочных технологий, обменялись опытом организации эффективного сварочного производства в условиях быстроизменяющегося мира. В работах студентов описаны процессы, которые сейчас внедряются в современную сварочную отрасль: автоматизация, роботизация, цифровизация, онлайн-управление.",
            published = "20 февраля в 12:49",
            likedByMe = false,
            likes = 999,
            sharess = 990,
            shareByMe = false
        )
        private val data = MutableLiveData(post)
        override fun get(): LiveData<Post> = data
        override fun like() {
            if (!post.likedByMe) {
                post.likes++
            } else {
                post.likes--
            }
            post = post.copy(likedByMe = !post.likedByMe)
            data.value = post
        }

        override fun share() {
                post.sharess++
            post = post.copy(shareByMe = !post.shareByMe)
            data.value = post
        }
    }
    class PostViewModel : ViewModel() {
        private val repository: PostRepository = PostRepositoryInMemoryImpl()
        val data = repository.get()
        fun like() = repository.like()
        fun share() = repository.share();
    }



