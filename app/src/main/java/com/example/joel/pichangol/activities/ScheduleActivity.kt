package com.example.joel.pichangol.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.joel.pichangol.R
import com.example.joel.pichangol.adapters.HourAdapter
import com.example.joel.pichangol.models.Hour
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.ArrayList


class ScheduleActivity : AppCompatActivity() {

    var hourList = ArrayList<Hour>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        rvHours.layoutManager = LinearLayoutManager(this)

        loadHours()

        rvHours.adapter = HourAdapter(hourList)
    }

    fun loadHours(){
        hourList.add(Hour("08",0))
        hourList.add(Hour("09",1))
        hourList.add(Hour("10",1))
        hourList.add(Hour("11",1))
        hourList.add(Hour("12",1))
        hourList.add(Hour("13",0))
        hourList.add(Hour("14",0))
        hourList.add(Hour("15",0))
        hourList.add(Hour("16",1))
        hourList.add(Hour("17",1))
        hourList.add(Hour("18",1))
        hourList.add(Hour("19",0))
        hourList.add(Hour("20",0))
    }
}
