# VK Stream

This demo application showcases the VideoKit live stream SDK, including the `StreamHost`
and `StreamViewer` interfaces.

### MainActivity

The main activity is responsible for logging the user in by creating a VideoKit `Session`.
Being a sample application, we use a random user identity to login. As soon as the session is
active, we launch the `MainFragment`.

### MainFragment

The main fragment uses the stream store (`VideoKit.streams()`) to query for currently available
streams and list them. When a stream is clicked, it is opened in a `StreamViewerFragment`.

The main fragment also offers a button to start a new stream by opening a `StreamHostFragment`.
These two capabilities should be used together from two different devices: when one device
starts streaming, the stream will be listed in the other device and can be watched.

### HostFragment

This fragment subclasses VideoKit's `StreamHostFragment` just to handle navigation and errors.
It is used to host a new stream, which will appear on the main app while in the `live` state.

### ViewerFragment

This fragment subclasses VideoKit's `StreamViewerFragment` just to handle navigation and errors.
It is used to view an existing `live` stream chosen from the list.