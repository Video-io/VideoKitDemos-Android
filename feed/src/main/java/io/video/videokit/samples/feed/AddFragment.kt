package io.video.videokit.samples.feed

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import io.video.videokit.VideoError
import io.video.videokit.camera.CameraFacing
import io.video.videokit.camera.CameraFlash
import io.video.videokit.recorder.*
import io.video.videokit.upload.UploadRequest

class AddFragment : Fragment(R.layout.fragment_add) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val recorder = RecorderFragment.newInstance {
                overlay(R.layout.fragment_add_controls)
                facing(CameraFacing.BACK)
                flash(CameraFlash.OFF)
                upload(UploadRequest.build {
                    tags("tag0", "tag1")
                    title("My Video")
                })
                maxDuration(30000)
            }
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.container, recorder, "Recorder")
            }
            recorder.setUp()
        } else {
            val recorder = childFragmentManager.findFragmentByTag("Recorder") as RecorderFragment
            recorder.setUp()
        }
    }

    private fun Recorder.setUp() {
        addListener(viewLifecycleOwner, object : RecorderListener {
            override fun onResult(record: Record) {
                forward()
            }

            override fun onError(error: VideoError) {
                if (error.code == VideoError.CANCELED) {
                    back()
                } else {
                    val message = "Something went wrong: $error"
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton(android.R.string.ok) { _, _ ->
                        // Should auto-finish for unrecoverable errors.
                        if (error.code in setOf(
                                        VideoError.CANCELED,
                                        VideoError.NO_PERMISSIONS,
                                        VideoError.CAMERA_FAILURE)) {
                            back()
                        }
                    }
                    builder.setCancelable(true)
                    builder.setTitle("Something went wrong")
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })
    }

    // Going back means popping the backstack.
    private fun back() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    // On completion, pop the back stack to go back to the feed.
    private fun forward() {
        requireActivity().supportFragmentManager.popBackStack()
    }
}