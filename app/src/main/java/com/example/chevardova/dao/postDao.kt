package com.example.chevardova.dao

import com.example.chevardova.posts.Post

interface postDao {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post): Post
    fun removeById(id:Long)
    fun update(post: Post)
}