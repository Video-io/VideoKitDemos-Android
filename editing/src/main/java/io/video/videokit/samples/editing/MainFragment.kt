package io.video.videokit.samples.editing

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import io.video.videokit.VideoError
import io.video.videokit.camera.CameraFacing
import io.video.videokit.camera.CameraFlash
import io.video.videokit.editor.Edit
import io.video.videokit.editor.EditorListener
import io.video.videokit.editor.EditorState
import io.video.videokit.editor.Segment
import io.video.videokit.editor.ui.EditorFragment
import io.video.videokit.recorder.*
import io.video.videokit.recorder.ui.*
import io.video.videokit.upload.UploadRequest

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editor = if (savedInstanceState == null) {
            val fragment = EditorFragment.newInstance {
                saveToGallery(true)
                overlay(R.layout.editor_overlay)
            }
            childFragmentManager.commitNow {
                setReorderingAllowed(true)
                replace(R.id.inner_container, fragment, "Editor")
            }
            fragment
        } else {
            childFragmentManager.findFragmentByTag("Editor") as EditorFragment
        }

        // Despite using commitNow, the editor fragment view can be created lazily by the system.
        // Use the view lifecycle to ensure that the editor is in a legit state before configuring it.
        editor.viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) {
            setUpEditor(editor)
            if (savedInstanceState == null) {
                // First open: start with the recorder UI.
                showRecorder(editor)
            }
        }
    }

    private fun setUpEditor(editor: EditorFragment) {
        // Find extra views that we have set in our XML overlay, and configure them.
        val openRecorderView = editor.overlay!!.findViewById<View>(R.id.open_recorder)
        val emptyMessageView = editor.overlay!!.findViewById<View>(R.id.empty_message)
        openRecorderView.setOnClickListener {
            showRecorder(editor)
        }

        // Listen to editor events. We will:
        // - show a toast to teach the user about hold & drag
        // - handle errors
        // - open the edited video onResult
        // - control the extra views visibility
        editor.addListener(viewLifecycleOwner, object : EditorListener {

            private var toastShown = false

            override fun onSegmentsChanged(segments: List<Segment>) {
                emptyMessageView.isVisible = segments.isEmpty()
                if (segments.size == 2 && editor.state == EditorState.PREVIEW && !toastShown) {
                    Toast.makeText(requireContext(), "Hold & drag to rearrange segments.", LENGTH_LONG).show()
                    toastShown = true
                }
            }

            override fun onStateChanged(state: Int) {
                openRecorderView.isVisible = state == EditorState.PREVIEW
            }

            override fun onResult(edit: Edit) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Edit completed. Would you like to open the resulting file?")
                    .setCancelable(true)
                    .setPositiveButton("Yes") { _, _ ->
                        startActivity(
                            Intent(Intent.ACTION_VIEW)
                                .setDataAndType(edit.galleryUri, "video/mp4")
                                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
                    }
                    .show()
            }

            override fun onError(error: VideoError) {
                if (error.code == VideoError.CANCELED) {
                    requireActivity().finish()
                    return
                }
                val message = "Something went wrong: $error"
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    if (error.code in setOf(VideoError.TRANSCODING_FAILURE)) {
                        editor.reset()
                    }
                }
                builder.setCancelable(false)
                builder.setTitle("Something went wrong")
                builder.setMessage(message)
                builder.show()
            }
        })
    }

    private fun showRecorder(editor: EditorFragment) {
        editor.pause()
        val recorder = RecorderFragment.newInstance {
            upload(null) // don't upload, we want to edit first
            overlay(R.layout.recorder_overlay) // to disable the preview step
            writeClips(true) // write each clip as individual file (Clip.uri)
            maxDuration(30 * 1000) // 30 seconds per session
        }
        childFragmentManager.commitNow {
            setReorderingAllowed(true)
            add(R.id.inner_container, recorder, "Recorder")
        }
        recorder.addListener(viewLifecycleOwner, object : RecorderListener {
            override fun onResult(record: Record) {
                // To append the recorded video as a single file, we can use:
                //     editor.appendSegment(record.uri)
                // However, since we enabled Recorder.writeClips, we can pass individual
                // clips so the user can edit them one by one and rearrange them!
                val segments = record.clips.mapNotNull { it.uri }
                segments.forEach {
                    editor.appendSegment(it)
                }
                hideRecorder(recorder)
            }

            override fun onError(error: VideoError) {
                if (error.code == VideoError.CANCELED) {
                    hideRecorder(recorder)
                    return
                }
                val message = "Something went wrong: $error"
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    // Should auto-finish for unrecoverable errors.
                    if (error.code in setOf(
                            VideoError.NO_PERMISSIONS,
                            VideoError.CAMERA_FAILURE)) {
                        hideRecorder(recorder)
                    }
                }
                builder.setCancelable(false)
                builder.setTitle("Something went wrong")
                builder.setMessage(message)
                builder.show()
            }
        })
    }

    private fun hideRecorder(recorder: RecorderFragment) {
        childFragmentManager.commit {
            remove(recorder)
        }
    }
}