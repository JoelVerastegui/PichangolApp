package com.example.joel.pichangol.activities

import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.util.Log
import android.view.View
import android.view.Window
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
import com.example.joel.pichangol.models.Profile
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Init Activity Variables
    var isConfiguration = false
    var store : SharedPreferences? = null

    // Volley variables
    var requestQueue : RequestQueue? = null

    // Server variables
    var serverIP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var text = Date()
        //var date = SimpleDateFormat("EEEE, dd 'de' MMMM, yyyy", Locale("es","ES")).format(text)
        var date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis())
        Log.d("test","Fecha: ${date}")
/*
        var calendar = Calendar.getInstance()
        calendar.time = date
        Log.d("test","Day of the week: ${calendar.get(Calendar.DAY_OF_MONTH)}")


        var actual = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        var date2 = SimpleDateFormat("yyyy-MM-dd").parse(actual)

        Log.d("test","Fecha actual UTC: ${date2.time}")
*/
/*
        text = "2019-06-24"
        date = SimpleDateFormat("yyyy-MM-dd").parse(text)
        //Log.d("test","Fecha ahora UTC: ${SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())}")

        var actual = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        date = SimpleDateFormat("yyyy-MM-dd").parse(actual)

        Log.d("test","Date: ${date.time}")
        Log.d("test","Actual: ${System.currentTimeMillis()}")

        date = SimpleDateFormat("yyyy-MM-dd").parse("2019-06-29")
        var calendar = Calendar.getInstance()
        calendar.time = date
        Log.d("test","Day of the week: ${calendar.get(Calendar.DAY_OF_WEEK)}")
*/
        // Init sharedPreferences
        store = this.getSharedPreferences("store", Context.MODE_PRIVATE)

        if(store?.getString("serverIP","") != ""){

            serverIP = store?.getString("serverIP","") as String

            val ip = serverIP.split(".")

            lblIP1.setText(ip[0])
            lblIP2.setText(ip[1])
            lblIP3.setText(ip[2])
            lblIP4.setText(ip[3])

        } else{
            saveServerIP()
        }


        lblConfiguration.setOnClickListener {

            if (isConfiguration) {

                // lblConfiguration rotation animation
                ObjectAnimator.ofFloat(lblConfiguration,"rotation", 0f).apply {
                    duration = 200
                    start()
                }

                toggleConfiguration(-130f)

                isConfiguration = false

                saveServerIP()

            } else{

                // lblConfiguration rotation animation
                ObjectAnimator.ofFloat(lblConfiguration,"rotation", 75f).apply {
                    duration = 200
                    start()
                }

                toggleConfiguration(0f)

                isConfiguration = true

            }


        }

        btnLogin.setOnClickListener {
            saveServerIP()

            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            loginPostRequest(email,password)

            /*
            if(email == "beleza" && password == "beleza"){
                val miIntent = Intent(this, PrincipalActivity::class.java)
                startActivity(miIntent)
            } else{
                Toast.makeText(this,"Credenciales incorrectas",Toast.LENGTH_LONG).show()
            }*/
        }

        lblSignIn.setOnClickListener {
            saveServerIP()

            val signInIntent = Intent(this, SignInActivity::class.java)
            signInIntent.putExtra("map",true)
            startActivity(signInIntent)
        }

        lblGuest.setOnClickListener {
            saveServerIP()

            val principalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(principalActivity)
        }

    }

    fun toggleConfiguration(translationY : Float){
        ObjectAnimator.ofFloat(lblServerIP,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblIP1,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblDot1,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblIP2,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblDot2,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblIP3,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblDot3,"translationY", translationY).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblIP4,"translationY", translationY).apply {
            duration = 200
            start()
        }
    }

    fun saveServerIP(){
        lblIP1.hideKeyboard()
        lblIP2.hideKeyboard()
        lblIP3.hideKeyboard()
        lblIP4.hideKeyboard()

        val ip1 = lblIP1.text
        val ip2 = lblIP2.text
        val ip3 = lblIP3.text
        val ip4 = lblIP4.text

        store?.edit()?.putString("serverIP", "$ip1.$ip2.$ip3.$ip4")?.commit()

        Server.instance.ip = "$ip1.$ip2.$ip3.$ip4"

        serverIP = Server.instance.ip
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
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

                    val principalActivity = Intent(this, PrincipalActivity::class.java)
                    imgLoading.visibility = View.GONE
                    startActivity(principalActivity)

                } else {
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"Credenciales incorrectas.",Toast.LENGTH_SHORT).show()
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

                params.put("login",email)
                params.put("pass",password)

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
