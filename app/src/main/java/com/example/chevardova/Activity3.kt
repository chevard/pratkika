package com.example.chevardova

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chevardova.databinding.Activity3Binding
import com.google.android.material.snackbar.Snackbar

class Activity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = Activity3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.empty_post_error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
            } else {
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }
        }
    }
}