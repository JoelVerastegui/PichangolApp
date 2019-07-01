package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.DisplayMetrics
import android.view.View
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.adapters.HourAdapter
import com.example.joel.pichangol.models.Hour
import com.example.joel.pichangol.models.SoccerField
import kotlinx.android.synthetic.main.activity_hour.*
import kotlinx.android.synthetic.main.activity_hour.view.*
import java.util.*

class HourActivity : AppCompatActivity() {

    var hours = ArrayList<Hour>()
    var local = Server.instance.localS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hour)

        lblBack.setOnClickListener {
            this.finish()
        }

        // get device dimensions
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        Server.instance.screenWidth = displayMetrics.widthPixels
        Server.instance.screenHeight = displayMetrics.heightPixels




        val extras = intent.extras

        var soccerField = Server.instance.localS?.soccerFields?.find { x -> x.id == extras["soccerFieldId"] }
        Server.instance.soccerField = soccerField


        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = Server.instance.selectedDate


        var selectedDay = selectedCalendar.get(Calendar.DAY_OF_WEEK)
        selectedDay = selectedDay-1

        if(selectedDay == 0){
            selectedDay = 7
        }


        var selectedWorkDay = local?.workDays?.find { x -> x.day == selectedDay }

        if(selectedWorkDay != null){
            val start = selectedWorkDay.start
            val end = selectedWorkDay.end

            for(e in start until end) {
                var next = false
                for(c in 0 until soccerField!!.reservations.size){
                    if(e in soccerField.reservations[c].start..soccerField.reservations[c].end-1){
                        next = true
                        break
                    }
                }
                if(next){
                    if (e < 10) {
                        hours.add(Hour("0$e",0))
                    } else {
                        hours.add(Hour("$e",0))
                    }
                } else{
                    if (e < 10) {
                        hours.add(Hour("0$e",1))
                    } else {
                        hours.add(Hour("$e",1))
                    }
                }
            }

        }




        rvHours.layoutManager = GridLayoutManager(this,5)

        var adapter = HourAdapter(hours, this)

        rvHours.adapter = adapter

        btnNext.setOnClickListener {
            Server.instance.selectedHours = ArrayList(adapter.getArrayList())
            if(Server.instance.profile == null){
                var notLoggedActivity = Intent(this,NotLoggedActivity::class.java)
                startActivity(notLoggedActivity)
            } else{
                var purchaseActivity = Intent(this,PurchaseActivity::class.java)
                startActivity(purchaseActivity)
            }
        }
        btnNextIcon.setOnClickListener {
            Server.instance.selectedHours = ArrayList(adapter.getArrayList())
            if(Server.instance.profile == null){
                var notLoggedActivity = Intent(this,NotLoggedActivity::class.java)
                startActivity(notLoggedActivity)
            } else{
                var purchaseActivity = Intent(this,PurchaseActivity::class.java)
                startActivity(purchaseActivity)
            }
        }

    }
}
