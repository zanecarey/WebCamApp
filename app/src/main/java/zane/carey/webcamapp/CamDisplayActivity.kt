package zane.carey.webcamapp

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import android.net.http.SslError
import android.view.View
import android.webkit.SslErrorHandler
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import zane.carey.webcamapp.cityTextView
import zane.carey.webcamapp.countryTextView
import zane.carey.webcamapp.currentPic
import zane.carey.webcamapp.currentRadio
import zane.carey.webcamapp.daytimePic
import zane.carey.webcamapp.embedWebView
import zane.carey.webcamapp.latitudeTextView
import zane.carey.webcamapp.longitudeTextView
import zane.carey.webcamapp.regionTextView


lateinit var camTitle: TextView
lateinit var currentPic: ImageView
lateinit var daytimePic: ImageView
lateinit var countryTextView: TextView
lateinit var cityTextView: TextView
lateinit var regionTextView: TextView
lateinit var latitudeTextView: TextView
lateinit var longitudeTextView: TextView
lateinit var embedWebView: WebView
lateinit var currentRadio: RadioButton
lateinit var daylightRadio: RadioButton
lateinit var viewCountTextView: TextView

var embedLink = ""
var previewLink = ""
var daylightLink = ""

class CamDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_display)

        camTitle = findViewById(R.id.camTitle_textView)
        currentPic = findViewById(R.id.currentPic)
        daytimePic = findViewById(R.id.daytimePic)
        countryTextView = findViewById(R.id.countryTextView)
        cityTextView = findViewById(R.id.cityTextView)
        regionTextView = findViewById(R.id.regionTextView)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        embedWebView = findViewById(R.id.embedWebView)
        currentRadio = findViewById(R.id.currentRadio)
        daylightRadio = findViewById(R.id.daytimeRadio)
        viewCountTextView = findViewById(R.id.viewsTotalTextView)

        embedWebView.settings.javaScriptEnabled = true
        embedWebView.settings.domStorageEnabled = true
        embedWebView.settings.setAppCacheEnabled(true)
        embedWebView.webViewClient = MyWebViewClient()

        val camID = getCamID()

        retreiveInfo(camID)

        currentPic.setOnClickListener {
            startCamFeed()
        }

        //change pic based on radio button chosen
        currentRadio.setOnClickListener{
            daytimePic.visibility = View.GONE
            currentPic.visibility = View.VISIBLE
        }

        daylightRadio.setOnClickListener{
            currentPic.visibility = View.GONE
            daytimePic.visibility = View.VISIBLE
        }
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
                previewLink = response.image.current.previewPic
                daylightLink = response.image.daylight.previewPic
                Glide.with(this@CamDisplayActivity)
                    .asBitmap()
                    .load(previewLink)
                    .into(currentPic)
                Glide.with(this@CamDisplayActivity)
                    .asBitmap()
                    .load(daylightLink)
                    .into(daytimePic)
                viewCountTextView.text = response.statistics.views.toString()
                countryTextView.text = response.location.country
                cityTextView.text = response.location.city
                regionTextView.text = response.location.region
                latitudeTextView.text = response.location.latitude.toString()
                longitudeTextView.text = response.location.longitude.toString()
                embedLink = response.player.live.embed
            }
        }
    }


    fun startCamFeed() {
//        embedWebView!!.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                view?.loadUrl(url)
//                return true
//            }
//        }
        //embedWebView!!.loadUrl(embedLink)
        embedWebView!!.loadUrl("https://www.lookr.com/lookout/1485691420#action-play-day")
    }

    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

            embedWebView.loadUrl(url)
            return true

        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed() // Ignore SSL certificate errors
        }
    }


}
