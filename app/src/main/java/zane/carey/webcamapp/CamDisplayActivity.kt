package zane.carey.webcamapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import okhttp3.Dispatcher

lateinit var camTitle: TextView
lateinit var camPic: ImageView
lateinit var countryTextView: TextView
lateinit var cityTextView: TextView
lateinit var regionTextView: TextView
lateinit var latitudeTextView: TextView
lateinit var longitudeTextView: TextView

class CamDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_display)

        camTitle = findViewById(R.id.camTitle_textView)
        camPic = findViewById(R.id.camPic)
        countryTextView = findViewById(R.id.countryTextView)
        cityTextView = findViewById(R.id.cityTextView)
        regionTextView = findViewById(R.id.regionTextView)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)

        val camID = getCamID()

        retreiveInfo(camID)
    }

    fun getCamID(): String {
        if (getIntent().hasExtra("camID")) {
            return getIntent().getStringExtra("camID")
        } else return ""
    }

    fun retreiveInfo(camID: String) = runBlocking<Unit> {
        val job = CoroutineScope(Dispatchers.Main).launch {

            val request = api.getCamInfo(camID).await()
            val response = request.result.webcams[0]

            withContext(Dispatchers.Main) {
                camTitle.text = response.title
                Glide.with(this@CamDisplayActivity)
                    .asBitmap()
                    .load(response.image.current.thumbPic)
                    .into(camPic)
                countryTextView.text = response.location.country
                cityTextView.text = response.location.city
                regionTextView.text = response.location.region
                latitudeTextView.text = response.location.latitude.toString()
                longitudeTextView.text = response.location.longitude.toString()
            }
        }
    }
}
