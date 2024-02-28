package com.example.chevardova

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.chevardova.NewPostFragment.Companion.textArg
import com.example.chevardova.databinding.ActivityAppBinding
import com.google.android.material.snackbar.Snackbar

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)

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
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = text
                        }
                    )
            }

            it.apply {
                action = Intent.ACTION_DEFAULT
                putExtra(Intent.EXTRA_TEXT, "")
            }
        }
    }

}