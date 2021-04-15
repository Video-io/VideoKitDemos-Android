package io.video.videokit.samples.stream

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.video.videokit.VideoError
import io.video.videokit.live.host.StreamHostListener
import io.video.videokit.live.host.ui.StreamHostFragment
import io.video.videokit.live.host.ui.StreamHostOptions
import io.video.videokit.stream.Stream

class HostFragment : StreamHostFragment() {

    companion object {
        fun newInstance() = HostFragment().apply {
            arguments = newArgs {
                // Configure it if needed
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener(viewLifecycleOwner, object : StreamHostListener {
            override fun onError(error: VideoError) {
                if (error.code == VideoError.CANCELED) {
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    val message = "Something went wrong: $error"
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton(android.R.string.ok) { _, _ ->
                        // Should auto-finish for unrecoverable errors.
                        if (error.code in setOf(
                                VideoError.NO_PERMISSIONS,
                                VideoError.CAMERA_FAILURE)) {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                    builder.setCancelable(false)
                    builder.setTitle("Something went wrong")
                    builder.setMessage(message)
                    builder.show()
                }
            }

            override fun onResult(stream: Stream) {
                // We're done.
                Toast.makeText(requireContext(), "Stream is now marked as finished.", Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }
}