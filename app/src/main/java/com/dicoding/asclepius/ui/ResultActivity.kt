package com.dicoding.asclepius.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val resultText = intent.getStringExtra("RESULT_TEXT")

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }
        binding.resultText.text = resultText ?: "No results available"
    }


}