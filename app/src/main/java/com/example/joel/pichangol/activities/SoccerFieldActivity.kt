package com.example.joel.pichangol.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.adapters.DateAdapter
import com.example.joel.pichangol.adapters.FieldAdapter
import com.example.joel.pichangol.adapters.FieldHourAdapter
import com.example.joel.pichangol.models.Date
import com.example.joel.pichangol.models.Field
import com.example.joel.pichangol.models.Reservation
import com.example.joel.pichangol.models.SoccerField
import kotlinx.android.synthetic.main.activity_soccer_field.*
import kotlinx.android.synthetic.main.item_fields.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SoccerFieldActivity : AppCompatActivity() {

    // Volley variables
    var requestQueue: RequestQueue? = null
    var serverIP = Server.instance.ip

    // Activity variables
    var dates = ArrayList<Date>()
    var fields = ArrayList<Field>()
    var local = Server.instance.localS
    var selectedDate = Date()
    var displayedDates = ArrayList<java.util.Date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soccer_field)

        lblBack.setOnClickListener {
            this.finish()
        }

        var actual = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        var date = SimpleDateFormat("yyyy-MM-dd").parse(actual)

        selectedDate = date

        var calendar = Calendar.getInstance()
        calendar.time = date

        var access = false
        for(e in 0 until 4){
            access = false
            do{
                if(e != 0){
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                    actual = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
                    date = SimpleDateFormat("yyyy-MM-dd").parse(actual)
                }

                var day = calendar.get(Calendar.DAY_OF_WEEK)
                day = day-1

                if(day == 0){
                    day = 7
                }

                if(local?.workDays?.find{ x -> x.day == day } != null){
                    if(local?.nonDays?.filter{ x -> x.date.equals(actual)}?.size == 0){
                        access = true
                        var dayText = when(day){
                            1 -> "LUN" 2 -> "MAR" 3 -> "MIE" 4 -> "JUE" 5 -> "VIE" 6 -> "SAB" else -> "DOM"
                        }
                        var monthText = when(calendar.get(Calendar.MONTH)){
                            0 -> "Ene." 1 -> "Feb." 2 -> "Mar." 3 -> "Abr." 4 -> "May." 5 -> "Jun." 6 -> "Jul." 7 -> "Ago." 8 -> "Sep."
                            9 -> "Oct." 10 -> "Nov." else -> "Dic."
                        }
                        displayedDates.add(date)
                        dates.add(Date(dayText,"${calendar.get(Calendar.DAY_OF_MONTH)} ${monthText} ${calendar.get(Calendar.YEAR)}"))
                    } else{
                        if(e == 0){
                            calendar.add(Calendar.DAY_OF_MONTH,1)
                            actual = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
                            date = SimpleDateFormat("yyyy-MM-dd").parse(actual)
                        }
                    }
                } else{
                    if(e == 0){
                        calendar.add(Calendar.DAY_OF_MONTH,1)
                        actual = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
                        date = SimpleDateFormat("yyyy-MM-dd").parse(actual)
                    }
                }
            } while(!access)
        }

        rvDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvDays.adapter = DateAdapter(dates,this)

        selectedDate = displayedDates[0]

        rvFields.layoutManager = LinearLayoutManager(this)

        soccerFieldGetRequest(selectedDate)
    }

    fun soccerFieldGetRequest(selectedDate : java.util.Date){
        Server.instance.selectedDate = selectedDate
        fields.clear()
        requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            "http://$serverIP:8080/api-rest/get/getLocalIdDate/${local?.id}?date=${selectedDate.time}",
            null,
            Response.Listener { response ->
                val soccerFields = ArrayList<SoccerField>()

                if(!response.isNull("listSocField")){
                    val listSocField = response.getJSONArray("listSocField")

                    for(i in 0 until listSocField.length()){
                        val soccerField = listSocField.getJSONObject(i)

                        val id = soccerField["id"] as? Int ?: 0
                        val description = soccerField["description"] as? String ?: ""
                        val price = soccerField["price"] as? Double ?: 0.0

                        // Reservations
                        val reservations = ArrayList<Reservation>()

                        if(!soccerField.isNull("reservedDTOs")){
                            val reservedDTOs = soccerField.getJSONArray("reservedDTOs")

                            for(e in 0 until reservedDTOs.length()){
                                val reservedDTO = reservedDTOs.getJSONObject(e)

                                val day = reservedDTO["day"] as? String ?: ""
                                val start = reservedDTO["start"] as? Int ?: 0
                                val end = reservedDTO["end"] as? Int ?: 0

                                reservations.add(Reservation("","",day,start,end,0.0))
                            }
                        }

                        soccerFields.add(SoccerField(id,description,price,reservations))
                    }

                    Server.instance.localS?.soccerFields = soccerFields

                    local = Server.instance.localS


                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.time = selectedDate


                    var selectedDay = selectedCalendar.get(Calendar.DAY_OF_WEEK)
                    selectedDay = selectedDay-1

                    if(selectedDay == 0){
                        selectedDay = 7
                    }

                    var selectedWorkDay = local?.workDays?.find { x -> x.day == selectedDay }

                    if(selectedWorkDay != null){
                        val start = selectedWorkDay.start
                        val end = selectedWorkDay.end

                        var removeSoccerFields = ArrayList<SoccerField>()

                        for(i in 0 until soccerFields.size){
                            val hours = ArrayList<String>()
                            for(e in start until end) {
                                var next = false
                                for(c in 0 until soccerFields[i].reservations.size){
                                    if(e in soccerFields[i].reservations[c].start..soccerFields[i].reservations[c].end-1){
                                        next = true
                                        break
                                    }
                                }
                                if(!next){
                                    if (e < 10) {
                                        hours.add("0$e:00")
                                    } else {
                                        hours.add("$e:00")
                                    }
                                }
                            }
                            if(hours.size > 0){
                                fields.add(Field(soccerFields[i].description, hours))
                            } else{
                                removeSoccerFields.add(soccerFields[i])
                            }
                        }

                        if(removeSoccerFields.size > 0){
                            soccerFields.removeAll(removeSoccerFields)

                            Server.instance.localS?.soccerFields = soccerFields

                            local = Server.instance.localS
                        }
                    }

                    rvFields.adapter = FieldAdapter(fields)
                    imgLoading.visibility = View.GONE
                    imgLoading2.visibility = View.GONE
                } else{
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"No se han encontrado canchas disponibles", Toast.LENGTH_LONG).show()
                }


            },
            Response.ErrorListener {
                imgLoading.visibility = View.GONE
                Toast.makeText(this,"Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_LONG).show()
            }
        )

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}