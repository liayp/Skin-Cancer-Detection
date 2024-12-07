package com.dicoding.asclepius.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.data.local.HistoryDatabase
import com.dicoding.asclepius.data.local.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.ui.history.HistoryActivity
import com.dicoding.asclepius.ui.news.NewsActivity
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private val historyDatabase by lazy { HistoryDatabase.getInstance(this) }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let {
            Log.d("PhotoPicker", "Selected URI: $it")
            currentImageUri = it
            binding.previewImageView.setImageURI(it)
            startCrop(it)
        } ?: Log.d("PhotoPicker", "No media selected")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
        binding.historyAnalyze.setOnClickListener { openHistoryActivity() }
        binding.newsButton.setOnClickListener { openNewsActivity() }
    }

    private fun openHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun openNewsActivity() {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private fun startCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(800, 800)
            .start(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                UCrop.getOutput(it)?.let { uri -> showImage(uri) }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            data?.let {
                UCrop.getError(it)?.let { error -> showToast("Crop error: ${error.message}") }
            }
        }
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        currentImageUri = uri
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val classifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        val resultText = results?.joinToString(separator = "\n") { classification ->
                            classification.categories.joinToString { category ->
                                "${category.label}: ${"%.2f".format(category.score * 100)}%"
                            }
                        } ?: "No results available"

                        // Ambil kategori dan confidence score dari hasil pertama
                        results?.firstOrNull()?.categories?.maxByOrNull { it.score }?.let { category ->
                            val label = category.label
                            val confidence = category.score

                            // Simpan ke history
                            saveToHistory(uri, label, confidence)
                        }

                        // Intent untuk berpindah ke ResultActivity
                        val intent = Intent(this@MainActivity, ResultActivity::class.java).apply {
                            putExtra("IMAGE_URI", uri.toString())
                            putExtra("RESULT_TEXT", resultText)
                        }
                        startActivity(intent)
                    }
                }
            )

            val bitmap = classifierHelper.uriToBitmap(uri)?.copy(Bitmap.Config.ARGB_8888, true)
            bitmap?.let {
                classifierHelper.classifyImage(bitmap)
            } ?: showToast("Gagal mengonversi gambar!")
        } ?: showToast("Pilih gambar terlebih dahulu!")
    }

    private fun saveToHistory(uri: Uri, label: String, confidence: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(Date())
            val historyEntity = HistoryEntity(
                uri = uri.toString(),
                label = label,
                confidence = confidence,
                dateGenerate = formattedDate
            )
            historyDatabase.historyDao().insert(historyEntity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
