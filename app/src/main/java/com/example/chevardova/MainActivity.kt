package com.example.chevardova

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var textView2: TextView
    private lateinit var textView: TextView
    private lateinit var textView3: TextView
    private var likes =0
    private var share =990
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView5)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView6)
        var imageButton2: ImageButton = findViewById(R.id.imageButton3)
        var imageButton: ImageButton = findViewById(R.id.imageButton)
        var imageButton3: ImageButton = findViewById(R.id.imageButton2)
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
