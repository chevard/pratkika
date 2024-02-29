package com.example.chevardova.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.chevardova.posts.Post

class PostDaompl(private val db: SQLiteDatabase) : postDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES} INTEGER NOT NULL DEFAULT 0
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_SHARES_BY_ME = "shareByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARES = "shares"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_SHARES_BY_ME,
            COLUMN_LIKES,
            COLUMN_SHARES
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_AUTHOR, post.author)
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, post.published)
            put(PostColumns.COLUMN_LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(PostColumns.COLUMN_SHARES_BY_ME, if (post.shareByMe) 1 else 0)
            put(PostColumns.COLUMN_LIKES, post.liketxt)
            put(PostColumns.COLUMN_SHARES, post.sharetxt)
        }
        val id = db.insert(PostColumns.TABLE, null, values)
        if (id == -1L) {
            throw IllegalStateException("Failed to insert post into database")
        } else {
            return getPostById(id)
        }
    }



    override fun likeById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }


    override fun shareById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
               shares = shares + 1
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }
    override fun update(post: Post) {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_AUTHOR, post.author)
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, post.published)
            put(PostColumns.COLUMN_LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(PostColumns.COLUMN_SHARES_BY_ME, if (post.shareByMe) 1 else 0)
            put(PostColumns.COLUMN_LIKES, post.liketxt)
            put(PostColumns.COLUMN_SHARES, post.sharetxt)
        }
        db.update(
            PostColumns.TABLE,
            values,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(post.id.toString())
        )
    }

    private fun getPostById(id: Long): Post {
        val cursor = db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return map(it)
            }
        }
        throw IllegalStateException("Post with id $id not found")
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                shareByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES_BY_ME)) != 0,
                liketxt = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                sharetxt = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES)),
                video = ""
            )
        }
    }
}