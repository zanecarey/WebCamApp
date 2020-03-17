package zane.carey.webcamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

val api = RestApi()

var areaType = "country"
var categoryChoice = "Category"
var countryChoice = "Country"
var regionChoice = "AU.04"
var property = "hd"
private var cams = ArrayList<WebCam>()

var latitude: Double = 0.0
var longitude: Double = 0.0

var numCams = 0

var offset = 0

var recyclerStarted = false

private lateinit var adapter: RecyclerAdapter
private lateinit var camRecyclerView: RecyclerView
private lateinit var fab: FloatingActionButton
private lateinit var titleView: CardView
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var hideCard: CardView
private lateinit var hideIV: ImageView
private lateinit var animationUp: Animation
private lateinit var animationDown: Animation

private lateinit var animation: LayoutAnimationController

private var hideButtonFlag = true

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        val res: Resources = resources

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {

            titleView = findViewById(R.id.titleCardView) as CardView
            hideCard = findViewById(R.id.hideCardView) as CardView
            hideIV = findViewById(R.id.hideImageView) as ImageView
            val searchCardView = findViewById(R.id.searchCardView) as CardView
            val resetCardView = findViewById(R.id.resetCardView) as CardView
            camRecyclerView = findViewById(R.id.resultsRecyclerView) as RecyclerView
            val catSpinner = findViewById(R.id.categorySpinner) as Spinner
            val countrySpinner = findViewById(R.id.countrySpinner) as Spinner
            val regionSpinner = findViewById(R.id.regionSpinner) as Spinner

            val livestreamSwitch = findViewById(R.id.livestreamSwitch) as Switch
            val subregionSwitch = findViewById(R.id.subregionSwitch) as Switch

            val numCamsTextView = findViewById(R.id.numCamsTextView) as TextView

            fab = findViewById(R.id.floatingActionButton) as FloatingActionButton

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            //obtainLocation()

            animationUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            animationUp.duration = 200
            animationDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
            animationDown.duration = 200
            animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)

            /*
            * Onclick for the floating action button which lets the user input a distance in
            * kilometers from their location using GPS. All webcams within that distance are then
            * listed
            */
            fab.setOnClickListener {
                try {
                    //obtainLocation()
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location.longitude

                                //launch alert to get radius
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Find cams within a certain distance to your location")

                                val numberPicker = NumberPicker(this)
                                numberPicker.minValue = 1
                                numberPicker.maxValue = 100
                                builder.setView(numberPicker)

                                builder.setPositiveButton("Ok") { dialog, which ->

                                    val radiusVal = numberPicker.value


                                    //get cams
                                    val job = CoroutineScope(Dispatchers.Main).launch {
                                        val request = api.getNearbyCams(latitude, longitude, radiusVal).await()
                                        val response = request.result

                                        if (response.webcams.isEmpty()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "No results using these filters",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            for (i in response.webcams.indices) {
                                                cams.add(
                                                    WebCam(
                                                        response.webcams[i].id,
                                                        response.webcams[i].title,
                                                        response.webcams[i].image.current.previewPic
                                                    )
                                                )
                                            }
                                            withContext(Dispatchers.Main) {

                                                adapter = RecyclerAdapter(cams, this@MainActivity)
                                                camRecyclerView.adapter = adapter
                                                camRecyclerView.layoutManager =
                                                    LinearLayoutManager(this@MainActivity)
                                            }
                                        }
                                    }
                                }
                                val dialog = builder.create()
                                dialog.show()
                            } else {
                                Toast.makeText(this,"No Location detected!", Toast.LENGTH_LONG).show()
                            }
                        }



                } catch (ex: SecurityException) {
                }
            }

            catSpinner.adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.categories)
                )
            catSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categoryChoice = parent?.getItemAtPosition(position).toString().toLowerCase()
                }
            }

            countrySpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.countries_list)
            )
            countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countryChoice = parent?.getItemAtPosition(position).toString()

                    when (countryChoice) {
                        "Australia" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.australiaRegions)
                        )
                        "Austria" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.austriaRegions)
                        )
                        "Canada" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.canadaRegions)
                        )
                        "Finland" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.finlandRegions)
                        )
                        "France" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.franceRegions)
                        )
                        "Great Britain" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.britainRegions)
                        )
                        "Germany" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.germanyRegions)
                        )
                        "Japan" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.japanRegions)
                        )
                        "New Zealand" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.newzealandRegions)
                        )
                        "Poland" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.polandRegions)
                        )
                        "Russia" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.russiaRegions)
                        )
                        "Spain" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.spainRegions)
                        )
                        "Sweden" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.swedenRegions)
                        )
                        "Switzerland" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.switzerlandRegions)
                        )
                        "USA" -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.usaRegions)
                        )
                        else -> regionSpinner.adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(R.array.noRegion)
                        )
                    }
                }
            }

            regionSpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.canadaRegions)
            )
            regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    regionChoice = parent?.getItemAtPosition(position).toString()
                }
            }

            //Search Card View Listener
            searchCardView.setOnClickListener {
                if (livestreamSwitch.isChecked) {
                    property = "live"
                }

                var regionCode = ""


                if (subregionSwitch.isChecked) {
                    regionCode = getRegionCode(regionChoice)

                } else if (countryChoice != "Country") {
                    regionCode = getCountryCode(countryChoice)
                }
                if (countryChoice == "Country" && categoryChoice == "Category") {
                    getInfo("Country", "Category")
                } else if (categoryChoice == "Category") {
                    getInfo(regionCode, "Category")
                } else if (countryChoice == "Country") {
                    getInfo("Country", getCategoryCode(categoryChoice))
                } else {
                    getInfo(regionCode, getCategoryCode(categoryChoice))
                }

                if (hideButtonFlag) {
                    hideFilters()
                }
            }

            //Reset the recycler view which enables a new search
            resetCardView.setOnClickListener {
                if (recyclerStarted) {
                    offset = 0
                    numCams = 0
                    numCamsTextView.visibility = View.GONE
                    cams.clear()
                    adapter.notifyDataSetChanged()
                }
            }


            //subregion Switch Listener
            subregionSwitch.setOnClickListener {
                if (subregionSwitch.isChecked) {
                    regionSpinner.visibility = View.VISIBLE
                    areaType = "region"
                } else {
                    regionSpinner.visibility = View.GONE
                    areaType = "country"
                }
            }

            hideCard.setOnClickListener {
                hideFilters()

            }
        }
    }

    //Initiate call to get all of the webcams using the selected filters
    fun getInfo(region: String, category: String) = runBlocking<Unit> {
        val job = CoroutineScope(Dispatchers.Main).launch {
            val request: Result

            //Use the correct API call based on the selected region filter
            var requestType = "noCat"
            if (category == "category" && region == "Country") {
                request = api.getCamsNoCountryNoCategory(property, offset).await()
                requestType = "noCountrynoCat"
            } else if (region == "Country") {
                request = api.getCamsNoCountry(category, property, offset).await()
                requestType = "noCountry"
            } else if (category == "Category" || category == "category") {
                request = api.getCamsNoCat(areaType, region, property, offset).await()
            } else {
                request = api.getCams(areaType, region, category, property, offset).await()
                requestType = "allCams"
            }

            val response = request.result
            if (response.webcams.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "No results using these filters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                numCams = request.result.total
                for (i in response.webcams.indices) {
                    cams.add(
                        WebCam(
                            response.webcams[i].id,
                            response.webcams[i].title,
                            response.webcams[i].image.current.previewPic
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    numCamsTextView.visibility = View.VISIBLE
                    numCamsTextView.text = numCams.toString() + " WebCams"
                    adapter = RecyclerAdapter(cams, this@MainActivity)
                    camRecyclerView.adapter = adapter
                    camRecyclerView.layoutAnimation = animation
                    camRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    camRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)

                            if (!recyclerView.canScrollVertically(1)) {

                                if (numCams > 50) {
                                    val job = CoroutineScope(Dispatchers.Main).launch {
                                        //get more of list

                                        offset += 50
                                        val req: Result
                                        if (requestType == "noCat") {
                                            req =
                                                api.getCamsNoCat(areaType, region, property, offset)
                                                    .await()

                                        } else if (requestType == "noCountry") {
                                            req = api.getCamsNoCountry(category, property, offset)
                                                .await()
                                        } else if (requestType == "noCountryNoCat") {
                                            req = api.getCamsNoCountryNoCategory(property, offset)
                                                .await()
                                        } else {
                                            req = api.getCams(
                                                areaType,
                                                region,
                                                category,
                                                property,
                                                offset
                                            ).await()
                                        }
                                        val resp = req.result
                                        val respSize = resp.total
                                        for (i in resp.webcams.indices) {
                                            cams.add(
                                                WebCam(
                                                    response.webcams[i].id,
                                                    response.webcams[i].title,
                                                    response.webcams[i].image.current.previewPic
                                                )
                                            )
                                        }
                                        adapter.notifyItemRangeInserted(cams.size - 1, respSize)
                                    }
                                }
                            }
                        }
                    }
                    )
                    recyclerStarted = true
                }
            }
        }
    }

    //Function to translate the full country name into it's abbreviation
    fun getCountryCode(countryChoice: String): String {
        when (countryChoice) {
            "Australia" -> return "AU"
            "Austria" -> return "AT"
            "Belarus" -> return "BY"
            "Canada" -> return "CA"
            "Croatia" -> return "HR"
            "Czechia" -> return "CZ"
            "Finland" -> return "FI"
            "France" -> return "FR"
            "Germany" -> return "DE"
            "Great Britain" -> return "GB"
            "Greece" -> return "GR"
            "Iceland" -> return "IS"
            "Italy" -> return "IT"
            "Japan" -> return "JP"
            "New Zealand" -> return "NZ"
            "Norway" -> return "NO"
            "Poland" -> return "PL"
            "Romania" -> return "RO"
            "Russia" -> return "RU"
            "Spain" -> return "SP"
            "Sweden" -> return "SE"
            "Switzerland" -> return "CH"
            "USA" -> return "US"
            else -> return "US"
        }
    }

    //Translate full region name into its abbreviated region name
    fun getRegionCode(regionChoice: String): String {
        when (regionChoice) {
            //australia
            "Queensland" -> return "AU.04"
            "Victoria" -> return "AU.07"
            "New South Wales" -> return "AU.02"
            //austria
            "Carinthia" -> return "AT.02"
            "Lower Austria" -> return "AT.03"
            "Upper Austria" -> return "AT.04"
            "Salzburg" -> return "AT.05"
            "Styria" -> return "AT.06"
            "Tyrol" -> return "AT.07"
            "Voralberg" -> return "AT.08"
            "Vienna" -> return "AT.09"

            //canada
            "British Columbia" -> return "CA.02"
            "Nova Scotia" -> return "CA.07"
            "Ontario" -> return "CA.08"
            //finland
            "Newland" -> return "FI.01"
            "Pirkanmaa" -> return "FI.06"
            "Kymenlaakso" -> return "FI.08"
            "North Savo" -> return "FI.11"
            "North Ostrobothnia" -> return "FI.17"
            "Lapland" -> return "FI.19"
            //france
            "Brittany Region" -> return "FR.53"
            "Nouvelle-Aquitaine" -> return "FR.75"
            "Occitanie" -> return "FR.76"
            "Auvergne-Rhône-Alpes" -> return "FR.84"
            "Provence-Alpes-Côte d'Azur Region" -> return "FR.93"
            //great britain
            "England" -> return "GB.ENG"
            "Scotland" -> return "GB.SCT"
            "Wales" -> return "GB.WLS"
            //germany
            "Baden-Württemberg" -> return "DE.01"
            "Bavaria" -> return "DE.02"
            "Hesse" -> return "DE.05"
            "North Rhine-Westphalia" -> return "DE.07"
            "Schleswig-Holstein" -> return "DE.10"
            //japan
            "Nagano" -> return "JP.26"
            "Tokyo" -> return "JP.40"
            //new zealand
            "Auckland" -> return "NZ.E7"
            "Canterbury" -> return "NZ.39"
            //poland
            "Lower Silesia" -> return "PL.72"
            "Pomerania" -> return "PL.82"
            "Silesia" -> return "PL.83"
            //russia
            "Stavropol Kray" -> return "RU.70"
            //spain
            "Canary Islands" -> return "ES.53"
            "Catalonia" -> return "ES.56"
            "Valencia" -> return "ES.60"
            //sweden
            "Stockholm" -> return "SE.26"
            "Västra Götaland County" -> return "SE.28"
            //switzerland
            "Canton of Bern" -> return "CH.BE"
            "Canton Grisons" -> return "CH.GR"
            "Vaud" -> return "CH.VD"
            "Valais" -> return "CH.VS"
            "Zurich" -> return "CH.ZH"
            //usa
            "Arizona" -> return "US.AZ"
            "California" -> return "US.CA"
            "Florida" -> return "US.FL"
            "New York" -> return "US.NY"
            "Oregon" -> return "US.OR"
            "Texas" -> return "US.TX"
            "Utah" -> return "US.UT"
            "Washington" -> return "US.WA"
            else -> return "US.AL"
        }
    }

    //Tranlsate full category name into correct database name
    fun getCategoryCode(category: String): String {
        when (category) {
            "golf course" -> return "golf"
            "lake/river" -> return "lake"
            "mountain/canyon" -> return "mountain"
            "sky" -> return "meteo"
            "sports area" -> return "sportarea"
            "street/traffic" -> return "traffic"
            else -> return category
        }
    }

    //Hide the filters using animations
    private fun hideFilters() {
        if (hideButtonFlag) {
            titleView.startAnimation(animationUp)
            var timer = object : CountDownTimer(200, 16) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    titleView.visibility = View.GONE
                }
            }
            timer.start()

            hideIV.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp)
            hideButtonFlag = false
        } else {
            titleView.startAnimation(animationDown)
            var timer = object : CountDownTimer(200, 16) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    titleView.visibility = View.VISIBLE
                }
            }
            timer.start()

            hideIV.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp)
            hideButtonFlag = true
        }
    }
}
