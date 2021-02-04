package com.bykhkalo.redditapiclient.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bykhkalo.redditapiclient.R
import com.bykhkalo.redditapiclient.model.RedditPost
import com.bykhkalo.redditapiclient.utils.TimeUtils

class RedditPostItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var context: Context = view.context

    private var image: ImageView = view.findViewById(R.id.post_image)
    private var authorNameText: TextView = view.findViewById(R.id.post_author_name)
    private var title: TextView = view.findViewById(R.id.post_title)
    private var dateText: TextView = view.findViewById(R.id.post_date)
    private var commentCountText: TextView = view.findViewById(R.id.post_comments_count)



    companion object {
        fun create(parent: ViewGroup): RedditPostItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)


            return RedditPostItemViewHolder(view)
        }
    }

    fun bind(item: RedditPost?){
        if (item != null){
            Glide
                .with(context)
                .load(item.thumbnail)
                .error(R.drawable.ic_reddit)
                .into(image)


            authorNameText.text = item.subreddit
            title.text = item.title
            dateText.text = TimeUtils.getTimeAgo(item.created_utc)
            commentCountText.text = item.num_comments.toString() + " comments"
        }
    }

}