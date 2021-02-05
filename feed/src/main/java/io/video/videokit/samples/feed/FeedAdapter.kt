package io.video.videokit.samples.feed

import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.playlist.Playlist
import io.video.videokit.playlist.PlaylistListener

class FeedAdapter(
    private val lifecycle: LifecycleOwner,
    private val playlist: Playlist
) : RecyclerView.Adapter<FeedViewHolder>() {

    init {
        playlist.addListener(lifecycle, object : PlaylistListener {
            override fun onVideoInserted(id: String, index: Int) = notifyItemInserted(index)
            override fun onVideoRemoved(id: String, index: Int) = notifyItemRemoved(index)
            override fun onVideoChanged(id: String, index: Int) {
                notifyItemChanged(index, id) // use the payload to avoid change animations
            }
            override fun onVideoMoved(id: String, fromIndex: Int, toIndex: Int) {
                notifyItemMoved(fromIndex, toIndex)
            }
        })
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(parent, lifecycle)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(playlist.getVideo(position))
    }

    override fun onViewRecycled(holder: FeedViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onFailedToRecycleView(holder: FeedViewHolder): Boolean {
        holder.unbind()
        return false
    }

    // Autoplay logic:

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            maybePlay(recyclerView)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            maybePlay(recyclerView) // Important for first layout
        }

        private fun maybePlay(recyclerView: RecyclerView) {
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                val manager = recyclerView.layoutManager!! as LinearLayoutManager
                val position = manager.findFirstCompletelyVisibleItemPosition()
                if (position >= 0) {
                    val holder = recyclerView.findViewHolderForAdapterPosition(position) as? FeedViewHolder
                    holder?.play()
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(scrollListener)
    }
}