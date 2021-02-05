package io.video.videokit.samples.stories

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.VideoError
import io.video.videokit.VideoKit
import io.video.videokit.camera.CameraFacing
import io.video.videokit.common.Quality
import io.video.videokit.common.SortOrder
import io.video.videokit.execution.onError
import io.video.videokit.execution.onSuccess
import io.video.videokit.player.AspectMode
import io.video.videokit.player.pager.ui.PagerFragment
import io.video.videokit.player.pager.ui.PagerOptions
import io.video.videokit.playlist.Playlist
import io.video.videokit.recorder.*
import io.video.videokit.requests.FilteredPlaylistRequest
import io.video.videokit.upload.Upload
import io.video.videokit.upload.UploadListener
import io.video.videokit.video.Video

class StoriesFragment : PagerFragment() {

    companion object {
        fun newInstance(playlist: Playlist, index: Int) = StoriesFragment().apply {
            val args = newArgs(PagerOptions.build {
                aspectMode(AspectMode.CENTER_CROP)
                loop(false)
                playbackQuality(Quality.AUTO)
                overlay(R.layout.fragment_stories_overlays)
            })
            args.putParcelable("playlist", playlist)
            args.putInt("index", index)
            arguments = args
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val index = requireArguments().getInt("index")
            val playlist = requireArguments().getParcelable<Playlist>("playlist")!!
            playlist.loadMoreOnAccess = false
            set(playlist, index, play = true)
        }
        Toast.makeText(requireContext(),
            "Tap to pause, double tap or swipe to navigate.",
            Toast.LENGTH_LONG).show()
    }
}