//package com.example.chevardova.rep
//
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.example.chevardova.posts.Post
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import org.json.JSONException
//import org.json.JSONObject
//import java.io.IOException
//
//class PostRepositoryInMemoryImpl(private val context: Context
//) : PostRepository {
//    override fun getAll(): LiveData<List<Post>> {
//        try {
//            val jsonString = loadJsonFromAsset("postsOfBTPIT.json")
//            val jsonObject = JSONObject(jsonString)
//            val postData = jsonObject.getJSONArray("data")
//            val type = object : TypeToken<List<Post>>() {}.type
//            val posts: List<Post> = Gson().fromJson(postData.toString(), type)
//            return MutableLiveData(posts)
//        } catch (e: JSONException) {
//            Log.e("PostRepository", "Ошибка", e)
//        }
//        return MutableLiveData(emptyList())
//    }
//
//    override fun getData(): LiveData<List<Post>> = data
//    private fun sync() {
//        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
//            it.write(gson.toJson(data.value))
//        }
//    }
//    private fun loadJsonFromAsset(fileName: String): String {
//        return try {
//            context.assets.open(fileName).bufferedReader().use { it.readText() }
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//            ""
//        }
//    }
//    companion object {
//        private const val filename = "postsOfBTPIT.json"
//    }
//    private val gson = Gson()
//    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
//    private var posts: List<Post> = readPosts()
//        set(value) {
//            field = value
//            sync()
//        }
//    private fun readPosts(): List<Post> {
//        val file = context.filesDir.resolve(filename)
//        return if (file.exists()) {
//            context.openFileInput(filename).bufferedReader().use {
//                gson.fromJson(it, type)
//            }
//        } else {
//            emptyList()
//        }
//    }
//    private val data = MutableLiveData(posts)
//    override fun likeById(id: Long) {
//        val existingPosts = data.value.orEmpty().toMutableList()
//        val index = existingPosts.indexOfFirst { it.id == id }
//        if (index != -1) {
//            val updatedPost = existingPosts[index].copy(
//                likedByMe = !existingPosts[index].likedByMe,
//                liketxt = if (existingPosts[index].likedByMe) existingPosts[index].liketxt - 1 else existingPosts[index].liketxt + 1
//            )
//            existingPosts[index] = updatedPost
//            data.value = existingPosts
//            sync()
//        }
//    }
//    override fun shareById(id: Long) {
//        posts = posts.map { post ->
//            if (post.id == id) {
//                post.copy(sharetxt = post.sharetxt + 1)
//            } else {
//                post
//            }
//        }
//
//        data.value = posts
//    }
//
//    override fun removeById(id: Long) {
//        val existingPosts = data.value.orEmpty().toMutableList()
//        val postToRemove = existingPosts.firstOrNull { it.id == id }
//        if (postToRemove != null) {
//            existingPosts.remove(postToRemove)
//            for (post in existingPosts) {
//                if (post.likedByMe) {
//                    post.likedByMe = false
//                    post.liketxt = post.liketxt - 1
//                }
//            }
//            data.value = existingPosts
//        }
//
//    }
//
//    override fun save(post: Post) {
//        if (post.id == 0L) {
//            posts = listOf(
//                post.copy(
//                    id = (posts.firstOrNull()?.id ?: 0L) + 1
//                )
//            ) + posts
//            data.value = posts
//            return
//        }
//
//        posts = posts.map {
//            if (it.id != post.id) it else it.copy(content = post.content)
//        }
//
//        data.value = posts
//        sync()
//    }
//}