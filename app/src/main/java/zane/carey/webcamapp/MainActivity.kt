package zane.carey.webcamapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*


var categories = arrayOf("Category","Beach", "Coast", "Forest", "Island", "Lake", "Mountain")
var countries = arrayOf("Country", "USA", "Canada")
var categoryChoice = "Category"
var countryChoice = "Country"

class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val catCardView = findViewById(R.id.categoryCardView) as CardView

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
    }
}
