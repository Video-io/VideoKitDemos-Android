# VK Stories

This demo application shows a recording interface with horizontal list of stories.

### MainActivity

The main activity is responsible for logging the user in by creating a VideoKit `Session`.
Being a sample application, we use a random user identity to login. As soon as the session is
active, we launch the `RecordFragment`.

### RecordFragment

This fragment subclasses VideoKit's `RecorderFragment`, but it would be possible to implement the
same functionality from outside, for example like in the VK Feed sample.

When creating the fragment, we use `RecorderOptions.Builder.overlay(R.layout.overlay)` to pass a layout
resource which contains overlays that will be drawn on top of the recorder UI. These include:

- The official `RecorderControls` class. The recorder fragment will automatically find the controls and bind them.
- Some custom UI elements for duration, discarding or confirming the video preview. We show how to keep them in sync with the recorder state using `RecorderListener`.
- A top recycler view which will show thumbnails of the currently available stories.

Stories are just `io.video.videokit.video.Video` objects that can be accessed through the video store
(`VideoKit.videos()`). By requesting a `Playlist` instead of a static list of videos, we ensure that
the videos are reactive and the content is updated whenever a new video is added, or old videos are
changed or deleted.

We use the video thumbnail url to show stories thumbnail. Whenever a thumbnail is clicked, we pass
the stories `Playlist` and the clicked index to `StoriesFragment`.

### StoriesFragment

This fragment displays each story in a pager fashion - you can swipe through videos with your finger
and there's extra gesture support. This is done by subclassing VideoKit's `PagerFragment`, but you
could easily do the same from outside the fragment - subclassing is just for demonstration purposes.

When launched, the fragment retrieves the playlist and index from launch arguments, and passes them
to the `Pager` (in this case, the superclass) with some specific configuration options.

For example, the default `PlayerControls` are used, but we disable most UI elements through XML
(`app:playerControls*` attributes) to obtain a minimal stories UI.
