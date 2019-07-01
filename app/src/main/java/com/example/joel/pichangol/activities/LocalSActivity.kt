package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
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
import com.example.joel.pichangol.models.*
import kotlinx.android.synthetic.main.activity_local_s.*
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class LocalSActivity : AppCompatActivity() {

    // Volley variables
    var requestQueue: RequestQueue? = null
    var serverIP = Server.instance.ip
    var reviews = ArrayList<Review>()

    // Activity variables
    var local_id = 0
    var date = "1561420800000"
    var myReview: Review? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_s)

        lblBack.setOnClickListener {
            this.finish()
        }

        btnFields.setOnClickListener {
            val soccerFieldActivity = Intent(this, SoccerFieldActivity::class.java)
            startActivity(soccerFieldActivity)
        }

        lblNewReview.setOnClickListener {
            val reviewActivity = Intent(this, ReviewActivity::class.java)

            reviewActivity.putExtra("commentary", "")

            reviewActivity.putExtra("stars", 0)

            startActivity(reviewActivity)
        }

        lblUpdateReview.setOnClickListener {
            val reviewActivity = Intent(this, ReviewActivity::class.java)

            reviewActivity.putExtra("commentary", lblCommentary.text)

            reviewActivity.putExtra("stars", Server.instance.review?.stars?.toInt())

            startActivity(reviewActivity)
        }

        showHideReview()

        val extras = intent?.extras

        local_id = extras?.get("localId") as? Int ?: 0

        rvReviews.layoutManager = LinearLayoutManager(this)

        localGetRequest()
        reviewGetRequest()
    }

    fun localGetRequest() {
        Server.instance.review = null

        requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
                Request.Method.GET,
                "http://$serverIP:8080/api-rest/get/getLocalIdDate/$local_id?date=$date",
                null,
                Response.Listener { response ->

                    val id = response["id"] as? Int ?: 0
                    val nombre = response["nombre"] as? String ?: ""
                    val address = response["address"] as? String ?: ""
                    val description = response["description"] as? String ?: ""
                    val latitude = response["latitude"] as? Double ?: 0.0
                    val longitude = response["longitude"] as? Double ?: 0.0


                    // WorkDays
                    val workDays = ArrayList<WorkDays>()
                    val listWorDays = response.getJSONArray("listWorDays")

                    for (i in 0 until listWorDays.length()) {
                        val workDay = listWorDays.getJSONObject(i)

                        val day = workDay["day"] as? Int ?: 0
                        val start = workDay["start"] as? Int ?: 0
                        val end = workDay["end"] as? Int ?: 0

                        workDays.add(WorkDays(day, start, end))
                    }


                    // NonDays
                    val nonDays = ArrayList<NonDays>()
                    val listNonDays = response.getJSONArray("listNonDays")

                    for (i in 0 until listNonDays.length()) {
                        val nonDay = listNonDays.getJSONObject(i)

                        val dates = nonDay["date"] as? String ?: ""
                        val reason = nonDay["reasion"] as? String ?: ""

                        nonDays.add(NonDays(dates, reason))
                    }


                    // SoccerField
                    val soccerFields = ArrayList<SoccerField>()
                    val listSocField = response.getJSONArray("listSocField")

                    for (i in 0 until listSocField.length()) {
                        val soccerField = listSocField.getJSONObject(i)

                        val id = soccerField["id"] as? Int ?: 0
                        val description = soccerField["description"] as? String ?: ""
                        val price = soccerField["price"] as? Double ?: 0.0

                        // Reservations
                        val reservations = ArrayList<Reservation>()

                        if (!soccerField.isNull("reservedDTOs")) {
                            val reservedDTOs = soccerField.getJSONArray("reservedDTOs")

                            for (e in 0 until reservedDTOs.length()) {
                                val reservedDTO = reservedDTOs.getJSONObject(e)

                                val day = reservedDTO["day"] as? String ?: ""
                                val start = reservedDTO["start"] as? Int ?: 0
                                val end = reservedDTO["end"] as? Int ?: 0

                                reservations.add(Reservation("", "", day, start, end, 0.0))
                            }
                        }

                        soccerFields.add(SoccerField(id, description, price, reservations))
                    }

                    val localS = LocalS(id, nombre, description, address, latitude, longitude, workDays, nonDays, soccerFields)

                    Server.instance.localS = localS

                    lblName.text = localS.nombre
                    lblAddress.text = localS.address
                    lblDescription.text = localS.description

                    imgLoading.visibility = View.GONE
                },
                Response.ErrorListener {
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this, "Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_LONG).show()
                }
        )

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }

    fun reviewGetRequest() {
        requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
                Request.Method.GET,
                "http://$serverIP:8080/api-rest/get/obetenerReviewLocal/$local_id",
                null,
                Response.Listener { response ->
                    Log.d("test", "${response.length()}")
                    if (response.length() > 0) {
                        for (i in 0 until response.length()) {
                            val reviewJson = response.getJSONObject(i)

                            val firstName = reviewJson.getJSONObject("customer")["firstName"] as? String
                                    ?: ""
                            val lastName = reviewJson.getJSONObject("customer")["lastName"] as? String
                                    ?: ""

                            val review = Review(
                                    reviewJson["id"] as? Int ?: 0,
                                    reviewJson.getJSONObject("customer").getJSONObject("account")["id"] as? Int
                                            ?: 0,
                                    "$firstName $lastName",
                                    reviewJson["stars"] as? String ?: "",
                                    reviewJson["commentary"] as? String ?: ""
                            )

                            reviews.add(review)
                        }

                        if (Server.instance.profile != null) {
                            if (reviews.find { x -> x.account_id == Server.instance.profile?.account_id } != null) {
                                myReview = reviews.find { x -> x.account_id == Server.instance.profile?.account_id }
                                reviews.remove(myReview)
                                Server.instance.review = myReview
                                loadReview()
                            }
                        }

                        rvReviews.adapter = ReviewAdapter(reviews)

                        imgLoading2.visibility = View.GONE

                    } else {
                        lblNothing.visibility = View.VISIBLE

                        rvReviews.adapter = ReviewAdapter(reviews)

                        imgLoading2.visibility = View.GONE
                    }

                },
                Response.ErrorListener {
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this, "Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_LONG).show()
                }
        )

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }

    override fun onResume() {
        super.onResume()
        showHideReview()
        loadReview()
    }

    fun loadReview() {
        if (Server.instance.profile != null) {
            if (Server.instance.review != null) {
                val profile = Server.instance.profile

                when (profile?.account_id) {
                    1 -> lblImgReview.setBackgroundResource(R.drawable.user_1)
                    2 -> lblImgReview.setBackgroundResource(R.drawable.user_2)
                    3 -> lblImgReview.setBackgroundResource(R.drawable.user_3)
                    4 -> lblImgReview.setBackgroundResource(R.drawable.user_4)
                    5 -> lblImgReview.setBackgroundResource(R.drawable.user_5)
                }

                lblUserNameReview.text = "${profile?.firstName} ${profile?.lastName}"

                lblCommentary.text = Server.instance.review?.commentary

                val stars = Server.instance.review?.stars?.toInt() ?: 0

                starReview1.setBackgroundResource(R.drawable.star)
                starReview2.setBackgroundResource(R.drawable.star)
                starReview3.setBackgroundResource(R.drawable.star)
                starReview4.setBackgroundResource(R.drawable.star)
                starReview5.setBackgroundResource(R.drawable.star)

                if (stars >= 1) {
                    starReview1.setBackgroundResource(R.drawable.star_b)
                }
                if (stars >= 2) {
                    starReview2.setBackgroundResource(R.drawable.star_b)
                }
                if (stars >= 3) {
                    starReview3.setBackgroundResource(R.drawable.star_b)
                }
                if (stars >= 4) {
                    starReview4.setBackgroundResource(R.drawable.star_b)
                }
                if (stars >= 5) {
                    starReview5.setBackgroundResource(R.drawable.star_b)
                }

                lblNewReview.visibility = View.GONE
            }
        }
    }

    fun showHideReview(){
        if (Server.instance.profile == null) {
            lblImgReview.visibility = View.GONE
            lblUserNameReview.visibility = View.GONE
            starReview1.visibility = View.GONE
            starReview2.visibility = View.GONE
            starReview3.visibility = View.GONE
            starReview4.visibility = View.GONE
            starReview5.visibility = View.GONE
            lblCommentary.visibility = View.GONE
            lblUpdateReview.visibility = View.GONE
            lblNewReview.visibility = View.GONE

            val reviewsParams = rvReviews.layoutParams as ConstraintLayout.LayoutParams
            reviewsParams.bottomToTop = R.id.btnFields
            rvReviews.requestLayout()
        } else{
            lblImgReview.visibility = View.VISIBLE
            lblUserNameReview.visibility = View.VISIBLE
            starReview1.visibility = View.VISIBLE
            starReview2.visibility = View.VISIBLE
            starReview3.visibility = View.VISIBLE
            starReview4.visibility = View.VISIBLE
            starReview5.visibility = View.VISIBLE
            lblCommentary.visibility = View.VISIBLE
            lblUpdateReview.visibility = View.VISIBLE
            lblNewReview.visibility = View.VISIBLE

            val reviewsParams = rvReviews.layoutParams as ConstraintLayout.LayoutParams
            reviewsParams.bottomToTop = R.id.lblUserNameReview
            rvReviews.requestLayout()
        }
    }

}
