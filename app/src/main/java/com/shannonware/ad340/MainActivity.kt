package com.shannonware.ad340

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val forecastRepository = ForecastRepository()

    // region Setup Methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val zipcodeEditText: EditText = findViewById(R.id.zipcodeEditText)
        val enterButton: Button = findViewById(R.id.enterButton)

        enterButton.setOnClickListener {
            // Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
            val zipcode: String = zipcodeEditText.text.toString()

            if (zipcode.length != 5) {
                Toast.makeText(this, R.string.zipcode_entry_error, Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, zipcode, Toast.LENGTH_SHORT).show()
                forecastRepository.loadForecast(zipcode)
            }
        }

        val forecastList: RecyclerView = findViewById(R.id.forecastList)
        forecastList.layoutManager = LinearLayoutManager(this)
        val dailyForecastAdapter = DailyForecastAdapter() {
            val msg = getString(R.string.forecast_clicked_format, it.temp, it.description)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        } // trailing lambda syntax
        forecastList.adapter = dailyForecastAdapter

        val weeklyForecastObserver = Observer<List<DailyForecast>> { forecastItems ->
            // Observer will be updated any time data is passed into the repository
            // update our list adapter
            dailyForecastAdapter.submitList(forecastItems)
            Toast.makeText(this, "Loaded Items", Toast.LENGTH_SHORT).show()
        }
        // By binding to the lifecycle of the enclosing class, if any loading takes too long,
        // it won't return once the activity has been destroyed.
        forecastRepository.weeklyForecast.observe(this, weeklyForecastObserver)
    }
}