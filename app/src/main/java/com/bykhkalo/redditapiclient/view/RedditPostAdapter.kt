package com.bykhkalo.redditapiclient.view

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bykhkalo.redditapiclient.R
import com.bykhkalo.redditapiclient.model.RedditPost
import com.bykhkalo.redditapiclient.utils.DebugUtils.Companion.TAG
import com.memebattle.pwc.util.NetworkState

class RedditPostAdapter(private val retryCallback: () -> Unit, private val callbackOnClick: ActivityCallbackOnClick) : PagedListAdapter<RedditPost, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<RedditPost>() {
            override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
                return oldItem  == newItem

            }


            override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
                return oldItem.name == newItem.name
            }

        }

    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
                Log.d(TAG, "setNetworkState: loading holder removed")
            } else {
                notifyItemInserted(super.getItemCount())
                Log.d(TAG, "setNetworkState: loading holder inserted")
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }



    interface ActivityCallbackOnClick {
        fun showImageCallback(imageUrl: String?)
    }


    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state
        } else {
            R.layout.item_post
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_post -> RedditPostItemViewHolder.create(parent)
            R.layout.item_network_state -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Log.d(TAG, "onBindViewHolder: pos: $position")
        when (getItemViewType(position)) {
            R.layout.item_post -> {

                val itemData = getItem(position)
                if (itemData != null) {
                    holder.itemView.setOnClickListener {
                        callbackOnClick.showImageCallback(itemData.url_overridden_by_dest)
                    }
                }



                (holder as RedditPostItemViewHolder).bind(getItem(position))
            }
            R.layout.item_network_state -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState
            )
        }
    }
}