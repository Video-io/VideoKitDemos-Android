package io.video.videokit.samples.stories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.video.videokit.VideoError
import io.video.videokit.VideoKit
import io.video.videokit.camera.CameraFacing
import io.video.videokit.common.SortOrder
import io.video.videokit.execution.onError
import io.video.videokit.execution.onSuccess
import io.video.videokit.recorder.*
import io.video.videokit.recorder.ui.*
import io.video.videokit.requests.FilteredPlaylistRequest
import io.video.videokit.samples.stories.databinding.FragmentRecordOverlaysBinding
import io.video.videokit.upload.Upload
import io.video.videokit.upload.UploadListener
import io.video.videokit.video.Video

class RecordFragment : RecorderFragment() {

    companion object {
        fun newInstance() = RecordFragment().apply {
            arguments = newArgs(RecorderOptions.build {
                maxDuration(15000L)
                facing(CameraFacing.FRONT)
                overlay(R.layout.fragment_record_overlays)
            })
        }
    }

    private var binding: FragmentRecordOverlaysBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // To use view binding on our overlays, use the bind function on the overlay view
        // that is exposed by the superclass.
        binding = FragmentRecordOverlaysBinding.bind(overlay!!)


        // NOTE: It is recommended to load the playlist from your ViewModel and store it there.
        // This way, you avoid reloading it on configuration change, and you can subscribe to
        // LiveData safely.
        val request = FilteredPlaylistRequest.build {
            order(SortOrder.DESC)
            pageSize(20)
        }
        VideoKit.videos().getPlaylist(request).onError {
            Toast.makeText(requireContext(), "Error while loading stories playlist: $it", Toast.LENGTH_LONG).show()
        }.onSuccess { playlist ->
            binding?.recycler?.adapter = ThumbnailAdapter(viewLifecycleOwner, playlist) { position ->
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack("Record")
                    replace(R.id.container, StoriesFragment.newInstance(playlist, position))
                }
            }
        }

        // Configure extra controls.
        binding?.recycler?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.post?.setOnClickListener {
            // Preview has been accepted. Let's confirm and finalize the upload.
            confirm()
        }
        binding?.discard?.setOnClickListener {
            // By calling reset, we exit RecorderState.PREVIEW AND clear any pending record.
            // If you want to only exit the preview state and keep the record, use exitRecord().
            reset()
        }
        binding?.gallery?.setOnClickListener {
            import()
        }

        // Configure recorder listener.
        addListener(viewLifecycleOwner, object : RecorderListener {
            override fun onStateChanged(state: Int) {
                binding?.recycler?.isInvisible = state != RecorderState.IDLE || record != null
                binding?.duration?.isVisible = state == RecorderState.RECORDING || (state == RecorderState.IDLE && record != null)
                binding?.post?.isVisible = state == RecorderState.PREVIEW
                binding?.discard?.isVisible = state == RecorderState.PREVIEW
                binding?.gallery?.isVisible = state == RecorderState.IDLE && record == null
            }

            override fun onRecordChanged(record: Record?) {
                binding?.recycler?.isInvisible = state != RecorderState.IDLE || record != null
                binding?.duration?.isVisible = state == RecorderState.RECORDING || (state == RecorderState.IDLE && record != null)
                binding?.gallery?.isVisible = state == RecorderState.IDLE && record == null
            }

            override fun onDurationChanged(duration: Long) {
                // max duration is 15s so we don't care about minutes / hours.
                val seconds = duration / 1000
                val maxSeconds = maxDuration / 1000
                binding?.duration?.text = "${seconds}s / ${maxSeconds}s"
            }

            override fun onResult(record: Record) {
                // Got video! It will appear automatically in the story thumbnails.
            }

            override fun onError(error: VideoError) {
                val message = "Something went wrong: $error"
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    // Should auto-finish for unrecoverable errors.
                    if (error.code in setOf(
                                    VideoError.CANCELED,
                                    VideoError.NO_PERMISSIONS,
                                    VideoError.CAMERA_FAILURE)) {
                        requireActivity().finish()
                    }
                }
                builder.setCancelable(false)
                builder.setTitle("Something went wrong")
                builder.setMessage(message)
                builder.show()
            }
        })

        // Listen to upload events.
        VideoKit.uploads().addListener(viewLifecycleOwner, object : UploadListener {
            override fun onUploadStarted(upload: Upload) {
                // Video upload has started.
            }
            override fun onUploadCompleted(upload: Upload, video: Video) {
                // Video was uploaded.
            }
            override fun onUploadError(upload: Upload, error: VideoError) {
                // Video upload failed.
            }
            override fun onUploadProgress(upload: Upload) {
                // Video upload is making progress.
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}