package io.video.videokit.samples.stories

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.video.videokit.player.AspectMode
import io.video.videokit.player.ui.PlayerView
import io.video.videokit.player.ui.controls.PlayerControls
import io.video.videokit.video.Video

class ThumbnailViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.thumbnail_item, parent, false)
) {
    private val image = itemView.findViewById<ImageView>(R.id.thumbnail)

    fun bind(video: Video, position: Int) {
        Glide.with(image).load(video.thumbnailImageUrl ?: "")
            .placeholder(R.drawable.ic_story_24)
            .error(R.drawable.ic_story_24)
            .circleCrop()
            .into(image)
    }
}