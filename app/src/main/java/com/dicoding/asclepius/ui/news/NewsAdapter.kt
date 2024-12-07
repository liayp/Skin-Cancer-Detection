package com.dicoding.asclepius.ui.news

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem
import com.bumptech.glide.Glide

class NewsAdapter(private val articles: List<ArticlesItem?>?, private val onItemClick: (ArticlesItem) -> Unit) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles?.get(position)
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles?.size ?: 0
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.tvDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.ivImage)

        fun bind(article: ArticlesItem?) {
            titleText.text = article?.title
            descriptionText.text = article?.description

            // Load image using Glide
            Glide.with(itemView.context)
                .load(article?.urlToImage)
                .into(imageView)

            itemView.setOnClickListener {
                article?.let {
                    if (!it.url.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(it.url) // Arahkan ke URL dari artikel
                        }
                        itemView.context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            itemView.context,
                            "URL tidak tersedia untuk berita ini.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
