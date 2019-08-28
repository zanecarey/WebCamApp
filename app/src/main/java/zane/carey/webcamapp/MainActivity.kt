package zane.carey.webcamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.view.MenuItem
import android.view.View
import android.widget.*
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

var areaType = "region"
var categoryChoice = "Beach"
var countryChoice = "CA"
var regionChoice = "AU.04"
var property = "hd"
private var cams = ArrayList<WebCam>()

var latitude: Double = 0.0
var longitude: Double = 0.0
var radius: Int = 50

var numCams = 0

private lateinit var adapter: RecyclerAdapter
private lateinit var camRecyclerView: RecyclerView
private lateinit var fab: FloatingActionButton

private lateinit var fusedLocationClient: FusedLocationProviderClient
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        val res: Resources = resources

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {


            val searchCardView = findViewById(R.id.searchCardView) as CardView
            val resetCardView = findViewById(R.id.resetCardView) as CardView
            camRecyclerView = findViewById(R.id.resultsRecyclerView) as RecyclerView
            val catSpinner = findViewById(R.id.categorySpinner) as Spinner
            val countrySpinner = findViewById(R.id.countrySpinner) as Spinner
            val regionSpinner = findViewById(R.id.regionSpinner) as Spinner

            val livestreamSwitch = findViewById(R.id.livestreamSwitch) as Switch
            val hdSwitch = findViewById(R.id.hdSwitch) as Switch

            val numCamsTextView = findViewById(R.id.numCamsTextView) as TextView

            fab = findViewById(R.id.floatingActionButton) as FloatingActionButton

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            obtainLocation()

            fab.setOnClickListener {
                try {
                    obtainLocation()
                    Toast.makeText(
                        this@MainActivity,
                        latitude.toString() + longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    //get cams
                    val job = CoroutineScope(Dispatchers.Main).launch {
                        val request = api.getNearbyCams(latitude, longitude, radius).await()
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
                                        response.webcams[i].image.current.thumbPic
                                    )
                                )
                            }
                            withContext(Dispatchers.Main) {

                                adapter = RecyclerAdapter(cams, this@MainActivity)
                                camRecyclerView.adapter = adapter
                                camRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                            }
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
                    categoryChoice = parent?.getItemAtPosition(position).toString()
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
                } else {
                    property = "hd"
                }
                val regionCode = getRegionCode(regionChoice)
                val catCode = getCategoryCode(categoryChoice)
                getInfo(regionCode, catCode)
            }

            resetCardView.setOnClickListener{
                numCams = 0
                numCamsTextView.visibility = View.GONE
                cams.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun getInfo(region: String, category: String) = runBlocking<Unit> {
        val job = CoroutineScope(Dispatchers.Main).launch {
            val request = api.getCams(areaType, region, category, property).await()
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
                            response.webcams[i].image.current.thumbPic
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    numCamsTextView.visibility = View.VISIBLE
                    numCamsTextView.text = numCams.toString() + " WebCams"
                    adapter = RecyclerAdapter(cams, this@MainActivity)
                    camRecyclerView.adapter = adapter
                    camRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }
        }
    }



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

    fun getCategoryCode(category: String) : String {
        when(category) {
            "Golf Course" -> return "golf"
            "Lake/River" -> return "lake"
            "Mountain/Canyon" -> return "mountain"
            "Sky" -> return "meteo"
            "Sports Area" -> return "sportarea"
            "Street/Traffic" -> return "traffic"
            else -> return category
        }
    }
    private fun obtainLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                latitude = location!!.latitude
                longitude = location!!.longitude}
    }
}
