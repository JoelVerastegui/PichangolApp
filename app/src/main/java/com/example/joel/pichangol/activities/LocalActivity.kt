package com.example.joel.pichangol.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.adapters.ReviewAdapter
import com.example.joel.pichangol.models.Profile
import com.example.joel.pichangol.models.Review
import kotlinx.android.synthetic.main.activity_local.*
import java.util.ArrayList

class LocalActivity : AppCompatActivity() {

    // Activity variables
    var local_id = 0

    // Volley variables
    var requestQueue : RequestQueue? = null
    var reviews = ArrayList<Review>()
    var uniqueProfiles = ArrayList<Int>()

    // Server variables
    var serverIP = Server.instance.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)

        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        val extras = intent?.extras

        local_id = extras?.get("localId") as? Int ?: 0

        lblName.text = extras?.get("localName") as? String ?: ""
        lblAddress.text = extras?.get("localAddress") as? String ?: ""
        lblDescription.text = extras?.get("localDescription") as? String ?: ""

        rvReview.layoutManager = LinearLayoutManager(this)

        reviewGetRequest(local_id)

        btnReserve.setOnClickListener {
            var scheduleIntent = Intent(this, ScheduleActivity::class.java)
            startActivity(scheduleIntent)
        }
    }

    fun reviewGetRequest(local_id : Int){

        // Clear all server profile list data
        Server.instance.profileList.clear()

        requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET,
            "http://$serverIP:8080/api-rest/get/obetenerResenasLocal/$local_id",
            null,
            Response.Listener { response ->
                if(response.length() > 0){
                    for(i in 0 until response.length()){
                        val reviewJson = response.getJSONObject(i)

                        val review = Review(
                            reviewJson["id"] as? Int ?: 0,
                            reviewJson.getJSONObject("account")["id"] as? Int ?: 0,
                            reviewJson.getJSONObject("local")["id"] as? Int ?: 0,
                            reviewJson["stars"] as? Int ?: 0,
                            reviewJson["commentary"] as? String ?: ""
                        )

                        if(reviews.find { r -> r.account_id == review.account_id } == null){
                            uniqueProfiles.add(review.account_id)
                        }

                        reviews.add(review)
                    }

                    for(account_id in uniqueProfiles){
                        profileGetRequest(account_id)
                    }
                } else{
                    lblNothing.visibility = View.VISIBLE

                    rvReview.adapter = ReviewAdapter(reviews)

                    imgLoading.visibility = View.GONE
                }

            },
            Response.ErrorListener {  }
        )

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }

    fun profileGetRequest(account_id : Int){
        requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            "http://$serverIP:8080/api-rest/get/obetenerProfileIdAcc/$account_id",
            null,
            Response.Listener { response ->
                val profile = Profile(
                    response["id"] as? Int ?: 0,
                    response.getJSONObject("account")["id"] as? Int ?: 0,
                    response["fullName"] as? String ?: "",
                    response["phone1"] as? String ?: "",
                    response["phone2"] as? String ?: "",
                    response["phone3"] as? String ?: "",
                    response["dni"] as? String ?: "",
                    response["status"] as? Int ?: 0
                    )

                Server.instance.profileList.add(profile)

                if(Server.instance.profileList.size == uniqueProfiles.size){
                    rvReview.adapter = ReviewAdapter(reviews)

                    imgLoading.visibility = View.GONE
                }
            },
            Response.ErrorListener {  }
        )

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
