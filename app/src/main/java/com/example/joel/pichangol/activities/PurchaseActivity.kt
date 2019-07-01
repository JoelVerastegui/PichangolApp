package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.models.Hour
import kotlinx.android.synthetic.main.activity_purchase.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PurchaseActivity : AppCompatActivity() {

    var local = Server.instance.localS
    var soccerField = Server.instance.soccerField
    var hours = Server.instance.selectedHours
    var selectedHours = ArrayList<Hour>()

    var serverIP = Server.instance.ip

    var totalPrice = 0.0
    var price = 0.0

    // Volley variables
    var requestQueue : RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        lblBack.setOnClickListener {
            this.finish()
        }

        btnReserve.setOnClickListener {
            reservePostRequest()
        }

        lblLocal.text = local?.nombre
        lblSoccerField.text = soccerField?.description

        selectedHours = ArrayList(hours.filter { x -> x.availability == 2 })

        var quantity = selectedHours.size

        if(quantity > 1){
            val nextHour = "0${selectedHours[selectedHours.size-1].hour.toInt() + 1}"
            val hourText = nextHour.substring(nextHour.length-2)
            lblHours.text = "${selectedHours[0].hour}:00 - $hourText:00 ($quantity horas)"
        } else{
            val nextHour = "0${selectedHours[0].hour.toInt() + 1}"
            val hourText = nextHour.substring(nextHour.length-2)
            lblHours.text = "${selectedHours[0].hour}:00 - $hourText:00 (1 hora)"
        }



        price = soccerField?.price as? Double ?: 0.0

        lblPrice.text = "S/. $price"

        totalPrice = (price * quantity)

        lblTotalPrice.text = "S/. $totalPrice"

        lblReservedDate.text = "${SimpleDateFormat("EEEE, dd 'de' MMMM, yyyy", Locale("es","ES")).format(Server.instance.selectedDate)}"
    }

    fun reservePostRequest(){
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()


        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "http://$serverIP:8080/api-rest/post/postReserv/",
                null,
            Response.Listener { response ->
                if(response["response"] == true){
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"Reserva realizada correctamente.",Toast.LENGTH_LONG).show()

                    val principalActivity = Intent(this,PrincipalActivity::class.java)
                    startActivity(principalActivity)
                } else{
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"La reserva no se pudo realizar.",Toast.LENGTH_LONG).show()
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

                var actual = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis())

                params.put("soccerFieldId","${soccerField?.id}")
                if(Server.instance.profile != null){
                    params.put("reserverId","${Server.instance.profile?.id}")
                    params.put("reserverType","CUSTOMER")
                } else{
                    params.put("reserverId","1")
                    params.put("reserverType","GUEST")
                }
                params.put("day","${SimpleDateFormat("yyyy-MM-dd").format(Server.instance.selectedDate)}")
                params.put("start",selectedHours[0].hour)
                params.put("end","${selectedHours[selectedHours.size-1].hour.toInt()+1}")
                params.put("chargeId","${Random().nextInt(9999)}")
                params.put("chargeAmount","$totalPrice")
                params.put("createDate","$actual")

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
