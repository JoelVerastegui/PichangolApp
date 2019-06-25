package com.example.joel.pichangol.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.example.joel.pichangol.R
import com.example.joel.pichangol.adapters.DateAdapter
import com.example.joel.pichangol.adapters.FieldAdapter
import com.example.joel.pichangol.adapters.FieldHourAdapter
import com.example.joel.pichangol.models.Date
import com.example.joel.pichangol.models.Field
import kotlinx.android.synthetic.main.activity_soccer_field.*
import kotlinx.android.synthetic.main.item_fields.*

class SoccerFieldActivity : AppCompatActivity() {

    var dates = ArrayList<Date>()
    var fields = ArrayList<Field>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soccer_field)

        rvDays.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        dates.add(Date("LUN","24 Jun. 2019"))
        dates.add(Date("MAR","25 Jun. 2019"))
        dates.add(Date("MIE","26 Jun. 2019"))
        dates.add(Date("JUE","27 Jun. 2019"))

        rvDays.adapter = DateAdapter(dates)



        rvFields.layoutManager = LinearLayoutManager(this)


        var hours1 = ArrayList<String>()

        hours1.add("13:00")
        hours1.add("14:00")
        hours1.add("15:00")
        hours1.add("16:00")
        hours1.add("17:00")
        hours1.add("18:00")
        hours1.add("19:00")


        var hours2 = ArrayList<String>()

        hours2.add("10:00")
        hours2.add("11:00")
        hours2.add("12:00")
        hours2.add("13:00")
        hours2.add("14:00")
        hours2.add("15:00")
        hours2.add("16:00")


        var hours3 = ArrayList<String>()

        hours3.add("08:00")
        hours3.add("09:00")
        hours3.add("10:00")
        hours3.add("11:00")
        hours3.add("12:00")
        hours3.add("13:00")
        hours3.add("14:00")
        hours3.add("15:00")
        hours3.add("16:00")
        hours3.add("17:00")
        hours3.add("18:00")
        hours3.add("19:00")
        hours3.add("20:00")
        hours3.add("21:00")
        hours3.add("22:00")
        hours3.add("23:00")


        fields.add(Field("Cancha 1 Miraflores",hours1))
        fields.add(Field("Cancha 2 Miraflores",hours2))
        fields.add(Field("Cancha 3 Miraflores",hours2))
        fields.add(Field("Cancha 4 Miraflores",hours2))
        fields.add(Field("Cancha 5 Miraflores",hours1))
        fields.add(Field("Cancha 6 Miraflores",hours3))
        fields.add(Field("Cancha 7 Miraflores",hours2))
        fields.add(Field("Cancha 8 Miraflores",hours1))

        rvFields.adapter = FieldAdapter(fields)

    }
}
