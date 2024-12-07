package com.dicoding.asclepius.ui.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.retrofit.ApiClient
import com.dicoding.asclepius.data.retrofit.ApiService
import com.dicoding.asclepius.data.response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.rvNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getNews()
    }

    private fun getNews() {
        val newsApi = ApiClient.instance.create(ApiService::class.java)
        newsApi.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val news = response.body()?.articles
                    newsAdapter = NewsAdapter(news) { article ->
                        // Handle item click, e.g., navigate to detail page
                        Toast.makeText(this@NewsActivity, "Clicked: ${article.title}", Toast.LENGTH_SHORT).show()
                    }
                    recyclerView.adapter = newsAdapter
                } else {
                    Toast.makeText(this@NewsActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(this@NewsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
