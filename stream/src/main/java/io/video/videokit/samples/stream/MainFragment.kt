package io.video.videokit.samples.stream

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.VideoKit
import io.video.videokit.execution.onError
import io.video.videokit.execution.onSuccess
import io.video.videokit.stream.Stream
import io.video.videokit.stream.StreamRequest
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val counter = view.findViewById<TextView>(R.id.counter)
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        fun load() {
            VideoKit.streams().query(StreamRequest(state = "live")).onError {
                Toast.makeText(requireContext(), "Could not query streams. $it", LENGTH_LONG).show()
                recycler.adapter = null
                counter.text = "Something went wrong: $it"
            }.onSuccess {
                recycler.adapter = Adapter(it) { stream ->
                    requireActivity().supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        addToBackStack("Viewer")
                        replace(R.id.container, ViewerFragment.newInstance(stream))
                    }
                }
                counter.text = "${it.size} streams found."
            }
        }
        load()

        val refresh = view.findViewById<View>(R.id.refresh)
        refresh.setOnClickListener {
            load()
        }

        val stream = view.findViewById<View>(R.id.stream)
        stream.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack("Streamer")
                replace(R.id.container, HostFragment.newInstance())
            }
        }
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.stream_item, parent, false)
    ) {
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val id = itemView.findViewById<TextView>(R.id.id)
        private val url = itemView.findViewById<TextView>(R.id.status)

        fun bind(stream: Stream, onStreamClick: (Stream) -> Unit) {
            itemView.setOnClickListener { onStreamClick(stream) }
            id.text = stream.id
            url.text = stream.playbackUrls["hls"]?.take(20)
            date.text = SimpleDateFormat.getDateTimeInstance().format(Date(stream.createdAt))
        }
    }

    class Adapter(
        val streams: List<Stream>,
        private val onStreamClick: (Stream) -> Unit
    ) : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount() = streams.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(streams[position], onStreamClick)
        }
    }
}