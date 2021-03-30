# VK Editing

This demo application showcases the VideoKit editing features and the `Editor` class.

### MainActivity

The main activity is responsible for logging the user in by creating a VideoKit `Session`.
Being a sample application, we use a random user identity to login. As soon as the session is
active, we launch the `MainFragment`.

### MainFragment

This is where everything happens. This fragment uses VideoKit's `EditorFragment`
(with default `EditorControls`) as the base editing interface. On top of the default controls,
we add an extra icon to import videos from camera.

When clicked, this icon (`R.id.open_recorder` in `editor_overlay.xml`) will use VideoKit's `RecorderFragment`
to show the recording interface, again, with default `RecorderControls` from the SDK. As soon as the
recorder provide results, we hide it and pass the `Uri`s to the editor.

Important:

- we use `app:recorderControlsShowPreviewScreen="false"` to disable the preview in the recorder flow.
  We're not interested in previewing the recorded video before adding it - the editor already offers
  video preview.

- we use `Recorder.writeClips` to tell the recorder that each clip should be written into a separate file.
  This way, we can pass them as individual segments to the editor so that they can be rearranged, repositioned
  and edited individually.

```kotlin
override fun onResult(record: Record) {
    // If writeClips = false, we can only use the whole record as a single uri.
    // We can't get all clips and use Clip.uri, because it will be null.
    // editor.appendSegment(record.uri)

    // If writeClips = true (recommended for this use case), we can pass individual
    // clips so the user can edit them one by one and rearrange them!
    val segments = record.clips.mapNotNull { it.uri }
    segments.forEach {
        editor.appendSegment(it)
    }
}
```