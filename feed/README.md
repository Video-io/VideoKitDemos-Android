# VK Feed

This demo application shows a vertical feed of videos backed by a `Playlist` object.

### MainActivity

The main activity is responsible for logging the user in by creating a VideoKit `Session`.
Being a sample application, we use a random user identity to login. As soon as the session is
active, we launch the `FeedFragment`.

### FeedFragment

This fragment hosts a vertical `RecyclerView` which displays all videos, with fake buttons to
like, share or comment each item. The implementation follows the [official documentation](https://docs.video.io/docs/android/player-lists)
for scrolling lists.

- A `Playlist` is used, to leverage reactive changes, caching and preloading
- Playlist changes are observed and dispatched to the recycler adapter
- Default `PlayerControls` are used to show play/pause/progress
- The view holder calls `player.reset()` when unbinding, to release resources
- There is some logic to implement autoplay on scroll

This fragment also contains a button to add new videos to the feed, which launches `AddFragment`.

### AddFragment

This fragment is a very lightweight wrapper around VideoKit's `RecorderFragment`, which starts
the recording journey and returns a result. We use a wrapper to configure the recorder listener,
especially the `onResult` and `onError` callbacks.

Another option would be to subclass `RecorderFragment` instead.

The video will be uploaded while still recording, and as soon as the video preview is confirmed,
the video will be automatically available in the initial `FeedFragment` as the first video.

