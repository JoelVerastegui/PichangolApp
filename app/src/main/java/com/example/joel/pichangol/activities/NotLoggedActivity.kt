package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import com.example.joel.pichangol.models.Profile
import kotlinx.android.synthetic.main.activity_not_logged.*
import org.json.JSONObject
import java.util.HashMap

class NotLoggedActivity : AppCompatActivity() {

    // Volley variables
    var requestQueue : RequestQueue? = null
    var serverIP = Server.instance.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_logged)

        lblGuest.setOnClickListener {
            var purchaseActivity = Intent(this,PurchaseActivity::class.java)
            startActivity(purchaseActivity)
        }

        lblSignIn.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            signInIntent.putExtra("map",false)
            startActivity(signInIntent)
        }

        btnLogin.setOnClickListener {

            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            loginPostRequest(email,password)
        }
    }

    fun loginPostRequest(email : String, password : String){
        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
                Request.Method.POST,
                "http://$serverIP:8080/api-rest/post/login",
                null,
                Response.Listener { response ->

                    if(response["response"] == true){

                        val profileObj = response.getJSONObject("Customer")

                        val id = profileObj["id"] as? Int ?: 0
                        val account_id = profileObj.getJSONObject("account")["id"] as? Int ?: 0
                        val firstName = profileObj["firstName"] as? String ?: ""
                        val lastName = profileObj["lastName"] as? String ?: ""
                        val birthday = profileObj["birthday"] as? String ?: ""
                        val phone = profileObj["phone"] as? String ?: ""
                        val email = profileObj["email"] as? String ?: ""

                        Server.instance.profile = Profile(id,account_id,firstName,lastName,birthday,phone,email)

                        var purchaseActivity = Intent(this,PurchaseActivity::class.java)
                        imgLoading.visibility = View.GONE
                        startActivity(purchaseActivity)
                    } else {
                        imgLoading.visibility = View.GONE
                        Toast.makeText(this,"Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
                    }

                },
                Response.ErrorListener {
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_LONG).show()
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String,String>()

                params.put("login",email)
                params.put("pass",password)

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
