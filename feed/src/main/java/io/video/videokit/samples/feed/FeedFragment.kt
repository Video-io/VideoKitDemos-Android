package io.video.videokit.samples.feed

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.video.videokit.VideoKit
import io.video.videokit.common.SortOrder
import io.video.videokit.execution.onError
import io.video.videokit.execution.onSuccess
import io.video.videokit.requests.FilteredPlaylistRequest
import java.util.*

class FeedFragment : Fragment(R.layout.fragment_feed) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // NOTE: It is recommended to load the playlist from your ViewModel and store it there.
        // This way, you avoid reloading it on configuration change, and you can subscribe to
        // LiveData safely.
        val button = view.findViewById<ExtendedFloatingActionButton>(R.id.add)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val message = view.findViewById<TextView>(R.id.message)
        val request = FilteredPlaylistRequest.build {
            order(SortOrder.DESC)
            pageSize(20)
        }
        VideoKit.videos().getPlaylist(request).onError {
            loading.isVisible = false
            message.text = "Something went wrong: $it"
        }.onSuccess {
            button.isVisible = true
            loading.isVisible = false
            message.isVisible = false
            recycler.adapter = FeedAdapter(viewLifecycleOwner, it)
        }

        // Control the FAB extension
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) = Unit
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && button.isExtended) {
                    button.shrink()
                } else if (dy < 0 && !button.isExtended) {
                    button.extend()
                }
            }
        })

        // Launch the recording fragment
        button.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack("Feed")
                replace(R.id.container, AddFragment())
            }
        }
    }
}