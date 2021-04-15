package io.video.videokit.samples.stream

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import io.video.videokit.VideoError
import io.video.videokit.live.host.StreamHostListener
import io.video.videokit.live.host.ui.StreamHostFragment
import io.video.videokit.live.viewer.StreamViewerListener
import io.video.videokit.live.viewer.ui.StreamViewerFragment
import io.video.videokit.live.viewer.ui.StreamViewerOptions
import io.video.videokit.stream.Stream

class ViewerFragment : StreamViewerFragment() {

    companion object {
        fun newInstance(stream: Stream) = ViewerFragment().apply {
            val args = newArgs {
                // Configure it if needed
            }
            args.putParcelable("stream", stream)
            arguments = args
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle events.
        addListener(viewLifecycleOwner, object : StreamViewerListener {

            override fun onError(error: VideoError) {
                if (error.code == VideoError.CANCELED) {
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    val message = "Something went wrong: $error"
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton(android.R.string.ok) { _, _ ->
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    builder.setCancelable(false)
                    builder.setTitle("Something went wrong")
                    builder.setMessage(message)
                    builder.show()
                }
            }

            private var streamState: String? = null

            override fun onStreamChanged(stream: Stream?) {
                val new = stream?.state
                val old = streamState
                when {
                    new == old -> Unit // Same state
                    new == "paused" -> {
                        Toast.makeText(requireContext(), "Stream was paused by the broadcaster.", LENGTH_SHORT).show()
                    }
                    old == "paused" && new == "live" -> {
                        Toast.makeText(requireContext(), "Stream was resumed by the broadcaster.", LENGTH_SHORT).show()
                    }
                    new == "finished" -> {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setPositiveButton(android.R.string.ok) { _, _ ->
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        builder.setCancelable(false)
                        builder.setTitle("Stream finished")
                        builder.setMessage("Stream was finished by the broadcaster. Thanks for watching.")
                        builder.show()
                    }
                }
                streamState = new
            }
        })

        // Get stream from arguments and apply it.
        if (savedInstanceState == null) {
            val stream = requireArguments().getParcelable<Stream>("stream")!!
            set(stream, play = true)
        }
    }
}