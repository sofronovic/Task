package com.nsofronovic.task.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nsofronovic.task.R
import com.nsofronovic.task.databinding.ViewPostItemBinding
import com.nsofronovic.task.model.Post

class PostAdapter(private val context: Context) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<Post> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(context).inflate(R.layout.view_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        if (posts.isNotEmpty()) {
            val item = posts[position]

            holder.title.text = item.title
            holder.body.text = item.body.replaceAfter("\n", "")
        }
    }

    override fun getItemCount(): Int = posts.size

    fun setData(postList: List<Post>) {
        posts = postList
        notifyDataSetChanged()
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ViewPostItemBinding.bind(view)
        val title = binding.tvTitle
        val body = binding.tvBody
    }
}
