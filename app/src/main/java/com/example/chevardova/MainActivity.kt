package com.example.chevardova

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import java.util.Random
class MainActivity : AppCompatActivity() {
    private lateinit var textView2: TextView
    private lateinit var textView: TextView
    private lateinit var textView3: TextView
    private var likes =0
    private var share =990

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var root: ConstraintLayout = (findViewById(R.id.root))

        root.setOnClickListener{
            Toast.makeText(this, "Вы нажали на ConstraintLayout", Toast.LENGTH_SHORT).show()
        }
        textView = findViewById(R.id.textView5)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView6)
        var imageButton2: ImageButton = findViewById(R.id.imageButton3)
        var imageButton: ImageButton = findViewById(R.id.imageButton)
        var imageButton3: ImageButton = findViewById(R.id.imageButton2)
        var imageView2: ImageView = findViewById(R.id.imageView2)
        imageView2.setOnClickListener {
            Toast.makeText(this, "Вы нажали на аватар", Toast.LENGTH_SHORT).show()
        }
        val random = Random()
        val views = random.nextInt(1000000)
        textView3.text = views.toString()
        textView3.text = if (views > 999&&views<999999) {
            String.format("%.1fK", views / 1000.0)
        } else if (views > 999999) {
            String.format("%.1fM", views / 1000000.0)
        } else {
            views.toString()
        }
        imageButton.setOnClickListener {
            Toast.makeText(this, "Вы лайкнули пост", Toast.LENGTH_SHORT).show()
            likes++
            textView.text = if (likes > 999&&likes<999999) {
                String.format("%.1fK", likes / 1000.0)
            } else if (likes > 999999) {
                String.format("%.1fM", likes / 1000000.0)
            } else {
                likes.toString()
            }

            imageButton2.visibility = View.VISIBLE
            imageButton.visibility = View.INVISIBLE
        }
        imageButton2.setOnClickListener {
            Toast.makeText(this, "Вы дизлайкнули пост", Toast.LENGTH_SHORT).show()
            likes--
            textView.text = if (likes > 999&&likes<999999) {
                String.format("%.1fK", likes / 1000.0)
            } else if (likes > 999999) {
                String.format("%.1fM", likes / 10000000.0)
            } else {
                likes.toString()
            }
            textView.text = if (likes > 999999) {
                String.format("%.1fM", likes / 1000000.0)
            } else {
                likes.toString()
            }
            imageButton.visibility = View.VISIBLE
            imageButton2.visibility = View.INVISIBLE
        }
        imageButton3.setOnClickListener {
            Toast.makeText(this, "Вы репостнули пост", Toast.LENGTH_SHORT).show()
            share += 1
            textView2.text = if (share > 999&&share<999999) {
                String.format("%.1fK", share / 1000.0)
            } else if (share > 999999) {
                String.format("%.1fM", share / 1000000.0)
            } else {
                share.toString()
            }

        }
    }
}
