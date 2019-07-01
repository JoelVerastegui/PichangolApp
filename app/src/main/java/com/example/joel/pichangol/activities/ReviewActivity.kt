package com.example.joel.pichangol.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.models.Review
import kotlinx.android.synthetic.main.activity_review.*
import org.json.JSONObject

class ReviewActivity : AppCompatActivity() {

    // Volley variables
    var requestQueue : RequestQueue? = null
    var serverIP = Server.instance.ip

    // Activity variables
    var commentary = ""
    var stars = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        lblBack.setOnClickListener {
            this.finish()
        }

        //Show keyboard on txtCommentary
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(txtCommentary, InputMethodManager.SHOW_FORCED)

        btnPublish.setOnClickListener {
            commentary = txtCommentary.text.toString()
            Log.d("test","$commentary")

            if(commentary.length > 0){
                if(stars > 0){
                    reviewPostRequest()
                } else{
                    Toast.makeText(this,"Debe seleccionar al menos una estrella.",Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this,"Debe ingresar una reseña.",Toast.LENGTH_SHORT).show()
            }
        }
        star1.setOnClickListener {
            paintStars(1)
        }
        star2.setOnClickListener {
            paintStars(2)
        }
        star3.setOnClickListener {
            paintStars(3)
        }
        star4.setOnClickListener {
            paintStars(4)
        }
        star5.setOnClickListener {
            paintStars(5)
        }

        val extras = intent.extras

        commentary = extras["commentary"] as? String ?: ""

        stars = extras["stars"] as? Int ?: 0

        txtCommentary.setText(commentary)

        paintStars(stars)
    }

    fun paintStars(stars : Int){
        this.stars = stars
        star1.setBackgroundResource(R.drawable.star)
        star2.setBackgroundResource(R.drawable.star)
        star3.setBackgroundResource(R.drawable.star)
        star4.setBackgroundResource(R.drawable.star)
        star5.setBackgroundResource(R.drawable.star)

        if(stars >= 1){
            star1.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 2){
            star2.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 3){
            star3.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 4){
            star4.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 5){
            star5.setBackgroundResource(R.drawable.star_b)
        }
    }

    fun reviewPostRequest(){
        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
                Request.Method.POST,
                "http://$serverIP:8080/api-rest/post/postReview",
                null,
                Response.Listener { response ->

                    if(response["response"] == true){
                        if(response["message"] == ""){
                            Toast.makeText(this,"Reseña registrada correctamente.",Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(this,"Reseña actualizada correctamente.",Toast.LENGTH_SHORT).show()
                        }

                        val review = Review(0,0,"","$stars","$commentary")

                        Server.instance.review = review

                        imgLoading.visibility = View.GONE

                        this.finish()
                    } else {
                        imgLoading.visibility = View.GONE
                        Toast.makeText(this,"Algo salio mal...",Toast.LENGTH_SHORT).show()
                    }

                },
                Response.ErrorListener {
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"Probablemente, el servicio es incorrecto. Error: $it",Toast.LENGTH_LONG).show()
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String,String>()

                params.put("customerId","${Server.instance.profile?.account_id}")
                params.put("localId","${Server.instance.localS?.id}")
                params.put("stars","$stars")
                params.put("commentary","$commentary")

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
