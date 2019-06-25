package com.example.joel.pichangol.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.example.joel.pichangol.R
import com.example.joel.pichangol.adapters.HourAdapter
import com.example.joel.pichangol.models.Hour
import kotlinx.android.synthetic.main.activity_hour.*

class HourActivity : AppCompatActivity() {

    var hours = ArrayList<Hour>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hour)

        rvHours.layoutManager = GridLayoutManager(this,5)

        hours.add(Hour("07",1))
        hours.add(Hour("08",1))
        hours.add(Hour("09",1))
        hours.add(Hour("10",0))
        hours.add(Hour("11",0))
        hours.add(Hour("12",0))
        hours.add(Hour("13",1))
        hours.add(Hour("14",1))
        hours.add(Hour("15",1))
        hours.add(Hour("16",1))
        hours.add(Hour("17",0))
        hours.add(Hour("18",0))
        hours.add(Hour("19",1))
        hours.add(Hour("20",1))

        rvHours.adapter = HourAdapter(hours)
    }
}
