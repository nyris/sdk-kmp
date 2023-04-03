package io.nyris.sdk.demo

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nyris.sdk.Nyris
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SdkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nyris = Nyris.createInstance(
            apiKey = BuildConfig.API_KEY,
            config = NyrisConfig(isDebug = true)
        )

        CoroutineScope(Dispatchers.Main + Job()).launch {
            nyris.imageMatching()
                .match(assets.loadImage("test_image.jpg"))
                .onSuccess {
                    Toast.makeText(applicationContext, "Woop Woop!", Toast.LENGTH_LONG).show()
                }
                .onFailure {
                    if (it is ResponseException) {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

private fun AssetManager.loadImage(path: String): ByteArray = open(path).run {
    ByteArray(available()).apply {
        read(this)
        close()
    }
}
