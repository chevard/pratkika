package com.example.chevardova.rep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chevardova.dao.postDao
import com.example.chevardova.posts.Post

class PostRepositorySQLiteImpl(private val postDao: postDao) :PostRepository
{
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = postDao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> {
        return data
    }


    override fun getData(): LiveData<List<Post>> {
        return data
    }

    override fun likeById(postId: Long) {
    postDao.likeById(postId)
    posts = postDao.getAll()
    data.value = posts
}

    override fun shareById(id: Long) {
        postDao.shareById(id)
        posts = postDao.getAll()
        data.value = posts
    }

    override fun removeById(id: Long) {
        postDao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = postDao.save(post)
        posts = if(id == 0L){
            listOf(saved) + posts
        }
        else{
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }
}