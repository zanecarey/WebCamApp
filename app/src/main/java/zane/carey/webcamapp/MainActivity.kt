package zane.carey.webcamapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

val api = RestApi()

var categories = arrayOf("Beach", "Coast", "Forest", "Island", "Lake", "Mountain")
var countries = arrayOf("Canada" , "France", "Germany" ,  "Great Britain" , "Greece" , "Hungary" , "Italy" , "USA"  , "Spain" )
var usRegions = arrayOf("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Texas")
var canadaRegions = arrayOf("Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon", "Northwest Territories", "Nunavut")
var franceRegions = arrayOf("Île-de-France Region", "Centre-Val de Loire", "Bourgogne-Franche-Comté", "Normandy", "Hauts-de-France", "Grand Est", "Pays de la Loire Region", " Brittany Region", "Nouvelle-Aquitaine", "Occitanie", "Auvergne-Rhône-Alpes", "Provence-Alpes-Côte d'Azur Region", "Corsica")
var categoryChoice = "Beach"
var countryChoice = "CA"
var regionChoice = "AL"

private var cams = ArrayList<WebCam>()

private lateinit var adapter: RecyclerAdapter
private lateinit var camRecyclerView: RecyclerView


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchCardView = findViewById(R.id.searchCardView) as CardView

        camRecyclerView = findViewById(R.id.resultsRecyclerView) as RecyclerView

        //SPINNER STUFF
        val catSpinner = findViewById(R.id.categorySpinner) as Spinner
        val countrySpinner = findViewById(R.id.countrySpinner) as Spinner
        val regionSpinner = findViewById(R.id.regionSpinner) as Spinner

        catSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        catSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoryChoice = parent?.getItemAtPosition(position).toString()
            }
        }

        countrySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                countryChoice = parent?.getItemAtPosition(position).toString()

                when(countryChoice) {
                    "Canada" -> regionSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, canadaRegions)
                    "France" -> regionSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, franceRegions)
                    "USA" -> regionSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, usRegions)
                }
            }
        }

        regionSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, canadaRegions)
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                regionChoice = parent?.getItemAtPosition(position).toString()
            }
        }

        //Search Card View Listener
        searchCardView.setOnClickListener {
            val regionCode = getRegionCode(regionChoice)
            getInfo(regionCode, categoryChoice)
        }

    }

    fun getInfo(region: String, category: String) = runBlocking<Unit> {
        val job = CoroutineScope(Dispatchers.Main).launch {
            val request = api.getCams(region, category).await()
            val response = request.result
            for(i in response.webcams.indices) {
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

    fun getRegionCode(regionChoice: String): String {
        when (regionChoice) {
            "Alabama" -> return "US.AL"
            "Alaska" -> return "US.AK"
            "Arizona" -> return "US.AZ"
            "Arkansas" -> return "US.AR"
            "California" -> return "US.CA"
            "Texas" -> return "US.TX"
            else -> return "US.TX"
        }
    }
}
