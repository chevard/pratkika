package com.example.chevardova

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chevardova.databinding.Activity2Binding
import androidx.activity.result.contract.ActivityResultContract

class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.setText(intent?.getStringExtra(Intent.EXTRA_TEXT))
        binding.plus.setOnClickListener {
            val intent = Intent()
            if(binding.content.text.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val content = binding.content.text.toString()
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_TEXT, content) })
            }
            finish()
        }
    }
    object Contract : ActivityResultContract<String, String?>() {
        override fun createIntent(context: Context, input: String) = Intent(context, Activity2::class.java).putExtra(Intent.EXTRA_TEXT, input)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra(Intent.EXTRA_TEXT)
    }
}
