package com.example.chevardova


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chevardova.databinding.ActivityMainBinding
import com.example.chevardova.databinding.CardPostBinding



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        val adapter = PostAdapter(
            onLikeListener = { post ->
                viewModel.likeById(post.id)
            },
            onShareListener = { post ->
                viewModel.shareById(post.id)
            }
        )
        binding.list?.adapter = adapter
        viewModel.data.observe(this) { post ->
            adapter.submitList(post)
        }
    }
}

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit
class PostAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}



class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            nameBTPIT.text = post.author
            dateOfPublished.text = post.published
            textOfPost.text = post.content
            likeBtn.setImageResource(
                if (post.likedByMe)
                    R.drawable.heartredd
                else
                    R.drawable.heartblack
            )
            textLikes.text = updateNumber(post.liketxt)
            textShares.text = updateNumber(post.sharetxt)
            likeBtn.setOnClickListener {
                onLikeListener(post)
                if (post.likedByMe) {
                    post.liketxt++
                } else {
                    post.liketxt--
                }

            }
            shareBtn.setOnClickListener {
                onShareListener(post)
                if (post.shareByMe) {
                    post.sharetxt++
                } else {
                    post.sharetxt--
                }

            }

        }
    }
}

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    var liketxt:Int,
    var sharetxt:Int,
    val shareByMe: Boolean,
)



fun updateNumber(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 1000000 -> String.format("%.1fK", count / 1000.0)
        else -> String.format("%.1fM", count / 1000000.0)
    }
}




interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
}

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    fun likeById(id: Long) = repository.likeById(id)

    fun shareById(id: Long) = repository.shareById(id)
}

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = listOf(
        Post(
            id = 1,
            author = "ГБПОУ ВО 'БТПИТ'",
            content = "Студенты ГБПОУ ВО 'БТПИТ' по профессии Сварщик ручной и частично механизированной сварки ( наплавки),под руководством педагогов Завидовской Н.И., Новокрещенова Д.В., приняли участие в региональной научно-практической студенческой конференции «Будущее сварки уже наступило», организованной ГБПОУ ВО «ВТПСТ», которая проходила с 22 января по 8 февраля 2024 года .Наши студенты-участники конференции поделились своим видением современных тенденций и проблем рынка сварочных технологий, обменялись опытом организации эффективного сварочного производства в условиях быстроизменяющегося мира. В работах студентов описаны процессы, которые сейчас внедряются в современную сварочную отрасль: автоматизация, роботизация, цифровизация, онлайн-управление.",
            published = "20 февраля в 12:49",
            likedByMe = false,
            liketxt = 0,
            sharetxt = 990,
            shareByMe = false
        ),
        Post(
            id = 2,
            author = "ГБПОУ ВО 'БТПИТ'",
            content = "26 января 2024 года в мастерских Борисоглебского техникума промышленных и информационных технологий прошел семинар для педагогов, направленный на профилактику экстремисткой деятельности.Наши студенты-участники конференции поделились своим видением современных тенденций и проблем рынка сварочных технологий, обменялись опытом организации эффективного сварочного производства в условиях быстроизменяющегося мира.",
            published = "26 января в 18:49",
            likedByMe = false,
            liketxt = 0,
            sharetxt = 990,
            shareByMe = false
        )
    )
    private val data = MutableLiveData(post)

    override fun getAll(): LiveData<List<Post>> = data


    override fun likeById(id: Long) {
        post = post.map {
            if (it.id == id) {
                if (!it.likedByMe) {
                    it.liketxt++
                } else {
                    it.liketxt--
                }
                it.copy(likedByMe = !it.likedByMe)
            } else {
                it
            }
        }
        data.value = post
    }

    override fun shareById(id: Long) {
        post = post.map {
            if (it.id == id) {
                if (!it.shareByMe) {
                    it.sharetxt++
                } else {
                    it.sharetxt--
                }
                it.copy(shareByMe = !it.shareByMe)
            } else {
                it
            }
        }
        data.value = post
    }


}




