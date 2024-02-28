package com.example.chevardova.posts

object WallService {
    fun updateNumber(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 1000000 -> String.format("%.1fK", count / 1000.0)
            else -> String.format("%.1fM", count / 1000000.0)
        }
    }
}