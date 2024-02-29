package com.example.chevardova.rep

import androidx.lifecycle.LiveData
import com.example.chevardova.posts.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun getData(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id:Long)
}