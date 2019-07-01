package com.example.joel.pichangol.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.adapters.ReservationAdapter
import com.example.joel.pichangol.models.Reservation
import kotlinx.android.synthetic.main.activity_reservation.*

class ReservationActivity : AppCompatActivity() {

    // Volley variables
    var requestQueue : RequestQueue? = null
    var serverIP = Server.instance.ip
    var reservations = ArrayList<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        lblBack.setOnClickListener {
            this.finish()
        }

        rvReservations.layoutManager = LinearLayoutManager(this)

        reservationGetRequest()
    }

    fun reservationGetRequest(){
        requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
                Request.Method.GET,
                "http://$serverIP:8080/api-rest/get/getReservsxCustomer/${Server.instance.profile?.id}",
                null,
                Response.Listener { response ->
                    if(response.length() > 0){
                        for(i in 0 until response.length()){
                            val reservationJson = response.getJSONObject(i)

                            val local = reservationJson.getJSONObject("soccerFieldDTO").getJSONObject("local")["name"] as? String ?: ""
                            val soccerField = reservationJson.getJSONObject("soccerFieldDTO")["description"] as? String ?: ""
                            val date = reservationJson["day"] as? String ?: ""
                            val start = reservationJson["start"] as? Int ?: 0
                            val end = reservationJson["end"] as? Int ?: 0
                            val total = reservationJson["chargeAmount"] as? Double ?: 0.0

                            reservations.add(Reservation(local,soccerField,date,start,end,total))
                        }

                        rvReservations.adapter = ReservationAdapter(reservations)

                        imgLoading.visibility = View.GONE

                    } else{
                        lblNothing.visibility = View.VISIBLE

                        rvReservations.adapter = ReservationAdapter(reservations)

                        imgLoading.visibility = View.GONE
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
