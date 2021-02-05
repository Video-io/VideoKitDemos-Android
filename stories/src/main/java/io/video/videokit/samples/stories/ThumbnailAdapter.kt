package io.video.videokit.samples.stories

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.playlist.Playlist
import io.video.videokit.playlist.PlaylistListener

class ThumbnailAdapter(
    private val lifecycle: LifecycleOwner,
    val playlist: Playlist,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ThumbnailViewHolder>() {

    private var recyclerView: RecyclerView? = null

    init {
        playlist.addListener(lifecycle, object : PlaylistListener {
            override fun onVideoInserted(id: String, index: Int) {
                notifyItemInserted(index)
                if (index == 0) {
                    // New story! Scroll there.
                    recyclerView?.post { recyclerView?.smoothScrollToPosition(0) }
                }
            }
            override fun onVideoRemoved(id: String, index: Int) {
                notifyItemRemoved(index)
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        return ThumbnailViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(playlist.getVideo(position), position)
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }
}