package com.example.chevardova.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chevardova.posts.Post
import com.example.chevardova.rep.PostRepository
import com.example.chevardova.rep.PostRepositoryInMemoryImpl
val empty = Post(
    id = 0,
    content = "",
    author = "Я",
    likedByMe = false,
    published = "только что",
    liketxt = 0,
    sharetxt = 0,
    shareByMe = false,
    video = ""
)

class PostViewModel(application: Application): AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryInMemoryImpl(application)
    val data: LiveData<List<Post>> = repository.getData()
    // val data: LiveData<List<Post>> = repository.getAll()
    val edited = MutableLiveData(empty)
    fun save(){
        edited.value?.let{
            repository.save(it)
        }
        edited.value = empty
    }
    fun edit(post: Post){
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
