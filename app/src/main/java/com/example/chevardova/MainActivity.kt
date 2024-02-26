package com.example.chevardova
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.launch
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
        val newPostLauncher = registerForActivityResult(Activity2.Contract) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }
        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                newPostLauncher.launch(post.content)
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
            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun onDeleteEdit(post: Post){
                viewModel.deleteEdit()
            }
        }
        )

//        viewModel.edited.observe(this) { post ->
//            if (post.id == 0L) {
//                binding.cancelGroup?.visibility = View.GONE
//                return@observe
//            }
//           binding.cancelGroup?.visibility = View.VISIBLE
//            binding.content?.requestFocus()
//            binding.content?.setText(post.content)
//        }
//        binding.ca?.setOnClickListener{
//            binding.cancelGroup?.visibility = View.GONE
//            viewModel.deleteEdit()
//            binding.content?.clearFocus()
//            binding.content?.setText("")
//        }
//        binding.save?.setOnClickListener {
//            with(binding.content) {
//                if (this?.text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Содержание не может быть пустым",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//                viewModel.changeContent(this?.text.toString())
//                viewModel.save()
//                this?.setText("")
//                this?.clearFocus()
//                this?.let { it1 -> AndroidUtils.hideKeyboard(it1) }
//            }
//        }
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        binding.list?.adapter = adapter

        binding.plus?.setOnClickListener{
            newPostLauncher.launch("")
        }
    }
}
class PostAdapter(
    private val interactionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, interactionListener)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
interface OnInteractionListener{
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onDeleteEdit(post:Post){}
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
        }
    }
}
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
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
    fun save(post:Post)
    fun removeById(id:Long)
}
private val empty = Post(
    id = 0,
    content = "",
    author = "Я",
    likedByMe = false,
    published = "только что",
    liketxt = 0,
    sharetxt = 0,
    shareByMe = false
)
class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun save(){
        edited.value?.let{
            repository.save(it)
        }
        edited.value = empty
    }
    fun edit(post:Post){
        edited.value = post
    }
    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (edited.value?.content == text) {
                return
            }
            edited.value = edited.value?.copy(content = text)
        }
    }
    fun deleteEdit(){
        edited.value = empty
    }
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}

private var nextid = 0L
class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
        Post(
            id = ++nextid,
            author = "ГБПОУ ВО 'БТПИТ'",
            content = "Студенты ГБПОУ ВО 'БТПИТ' по профессии Сварщик ручной и частично механизированной сварки ( наплавки),под руководством педагогов Завидовской Н.И., Новокрещенова Д.В., приняли участие в региональной научно-практической студенческой конференции «Будущее сварки уже наступило», организованной ГБПОУ ВО «ВТПСТ», которая проходила с 22 января по 8 февраля 2024 года .Наши студенты-участники конференции поделились своим видением современных тенденций и проблем рынка сварочных технологий, обменялись опытом организации эффективного сварочного производства в условиях быстроизменяющегося мира. В работах студентов описаны процессы, которые сейчас внедряются в современную сварочную отрасль: автоматизация, роботизация, цифровизация, онлайн-управление.",
            published = "20 февраля в 12:49",
            likedByMe = false,
            liketxt = 0,
            sharetxt = 990,
            shareByMe = false
        ),
        Post(
            id = ++nextid,
            author = "ГБПОУ ВО 'БТПИТ'",
            content = "26 января 2024 года в мастерских Борисоглебского техникума промышленных и информационных технологий прошел семинар для педагогов, направленный на профилактику экстремисткой деятельности.Наши студенты-участники конференции поделились своим видением современных тенденций и проблем рынка сварочных технологий, обменялись опытом организации эффективного сварочного производства в условиях быстроизменяющегося мира.",
            published = "26 января в 18:49",
            likedByMe = false,
            liketxt = 0,
            sharetxt = 990,
            shareByMe = false
        ),
    )
    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        val existingPosts = data.value.orEmpty().toMutableList()
        val index = existingPosts.indexOfFirst { it.id == id }
        if (index != -1) {
            val post = existingPosts[index]
            existingPosts[index] = post.copy(
                likedByMe = !post.likedByMe,
                liketxt = if (post.likedByMe) post.liketxt - 1 else post.liketxt + 1
            )
            save(existingPosts[index])
        }
    }

    override fun shareById(id: Long) {
        val existingPosts = data.value.orEmpty().toMutableList()
        val index = existingPosts.indexOfFirst { it.id == id }
        if (index != -1) {
            val post = existingPosts[index]
            existingPosts[index] = post.copy(
                shareByMe = !post.shareByMe,
                sharetxt = if (post.shareByMe) post.sharetxt - 1 else post.sharetxt + 1
            )
            save(existingPosts[index])
        }
    }


    override fun removeById(id: Long) {
        val existingPosts = data.value.orEmpty().toMutableList()
        val postToRemove = existingPosts.firstOrNull { it.id == id }
        if (postToRemove != null) {
            existingPosts.remove(postToRemove)
            for (post in existingPosts) {
                if (post.likedByMe) {
                    post.likedByMe = false
                    post.liketxt = post.liketxt - 1
                }
            }
            data.value = existingPosts
        }
    }

    override fun save(post: Post) {
        val existingPosts = data.value.orEmpty().toMutableList()
        if (post.id == 0L) {
            val newPost = post.copy(id = ++nextid)
            existingPosts.add(0, newPost)
        } else {
            val index = existingPosts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                existingPosts[index] = post
            }
        }

        data.value = existingPosts
    }
}
object AndroidUtils{
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}





