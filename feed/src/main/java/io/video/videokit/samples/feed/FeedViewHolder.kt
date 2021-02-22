package io.video.videokit.samples.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.player.AspectMode
import io.video.videokit.player.ui.PlayerView
import io.video.videokit.player.ui.controls.PlayerControls
import io.video.videokit.video.Video

class FeedViewHolder(parent: ViewGroup, lifecycle: LifecycleOwner) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.fragment_feed_item, parent, false)
) {
    private val player = itemView.findViewById<PlayerView>(R.id.player)
    private val controls = itemView.findViewById<PlayerControls>(R.id.player_controls)

    init {
        controls.setPlayer(player)
        player.bind(lifecycle)
        player.loop = true
        player.aspectMode = AspectMode.CENTER_CROP
    }

    fun bind(video: Video) = player.set(video, play = false)

    fun unbind() = player.reset()

    fun play() = player.play()

    fun pause() = player.pause()
}