package zane.carey.webcamapp

import android.app.ActionBar
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


lateinit var camTitle: TextView
lateinit var currentPic: ImageView
lateinit var fullScreenPic: ImageView
lateinit var daytimePic: ImageView
lateinit var lsAvailable: TextView
lateinit var countryTextView: TextView
lateinit var cityTextView: TextView
lateinit var regionTextView: TextView
lateinit var latitudeTextView: TextView
lateinit var longitudeTextView: TextView
lateinit var currentRadio: RadioButton
lateinit var daylightRadio: RadioButton
lateinit var viewCountTextView: TextView
lateinit var viewPageCardView: CardView
var embedLink = ""
var urlLink = ""
var previewLink = ""
var daylightLink = ""
lateinit var myWebView: WebView
lateinit var myImage: ImageView

class CamDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_display)

        camTitle = findViewById(R.id.camTitle_textView)
        currentPic = findViewById(R.id.currentPic)
        fullScreenPic = findViewById(R.id.fullScreenImage)
        daytimePic = findViewById(R.id.daytimePic)
        countryTextView = findViewById(R.id.countryTextView)
        cityTextView = findViewById(R.id.cityTextView)
        regionTextView = findViewById(R.id.regionTextView)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        currentRadio = findViewById(R.id.currentRadio)
        daylightRadio = findViewById(R.id.daytimeRadio)
        viewCountTextView = findViewById(R.id.viewsTotalTextView)
        viewPageCardView = findViewById(R.id.viewCardView)
        lsAvailable = findViewById(R.id.availableResultTextView)
        myWebView = WebView(this)
        //myImage = ImageView(this)



        val camID = getCamID()

        retreiveInfo(camID)

        viewPageCardView.setOnClickListener {
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
                if(response.property[0].propertyID == "live"){
                    lsAvailable.text = "Yes"
                } else {
                    lsAvailable.text = "No"
                }
                Glide.with(this@CamDisplayActivity)
                    .asBitmap()
                    .load(previewLink)
                    .into(currentPic)
                Glide.with(this@CamDisplayActivity)
                    .asBitmap()
                    .load(previewLink)
                    .into(fullScreenPic)
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
                urlLink = response.url.current.mobileUrl
            }
        }
    }


    fun startCamFeed() {
        //launch alert dialog with webview
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cam Web Page")
        builder.setPositiveButton("Ok") {dialog, which ->

        }

        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.settings.setAppCacheEnabled(true)
        myWebView.webViewClient = MyWebViewClient()

        myWebView!!.loadUrl(urlLink)

        builder.setView(myWebView)

        val dialog = builder.create()
        dialog.show()
    }

    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

            myWebView.loadUrl(url)
            return true

        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed() // Ignore SSL certificate errors
        }
    }
}
