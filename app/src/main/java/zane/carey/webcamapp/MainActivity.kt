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
var countries = arrayOf("IT", "DE")
var categoryChoice = "Beach"
var countryChoice = "IT"

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

        catSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        catSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoryChoice = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, categoryChoice, Toast.LENGTH_SHORT).show()
            }
        }

        countrySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                countryChoice = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, countryChoice, Toast.LENGTH_SHORT).show()
            }
        }


        //Search Card View Listener
        searchCardView.setOnClickListener {
            getInfo(countryChoice, categoryChoice)
        }

    }

    fun getInfo(country: String, category: String) = runBlocking<Unit> {
        val job = CoroutineScope(Dispatchers.Main).launch {
            val request = api.getCams(country, category).await()
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
}
