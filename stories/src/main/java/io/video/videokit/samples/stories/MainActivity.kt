package io.video.videokit.samples.stories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import io.video.videokit.VideoKit
import io.video.videokit.execution.onError
import io.video.videokit.execution.onSuccess
import io.video.videokit.internal.Logger
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.collector = Logger.Collector { level, tag, message, throwable ->
            // Register a log collector to observe logs or send to analytics services like crashlytics.
            // Log.println(level, tag, message + "${throwable?.printStackTrace() ?: ""}")
        }

        if (VideoKit.sessions().get() != null) return
        val loading = findViewById<ProgressBar>(R.id.loading)
        val message = findViewById<TextView>(R.id.message)
        VideoKit.sessions().start(
                appToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2lkIjoiMW81cFBiVzMwNG44SXVwR2JVSm8iLCJyb2xlIjoiYXBwIiwiaWF0IjoxNjEyMTY2MzkwLCJpc3MiOiJ2aWRlby5pbyIsImp0aSI6ImZQN290S3dFb2V5U2tGNVNzQVBmLXdkaDU1In0.ebYY3nXYCyc9b8NuQZ742ejLEKsqxh0lZiK7FjtmKBM",
                identity = UUID.randomUUID().toString()
        ).onError {
            loading.isVisible = false
            message.text = "Something went wrong: $it"
        }.onSuccess {
            loading.isVisible = false
            message.isVisible = false
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.container, RecordFragment.newInstance())
            }
        }
    }
}