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
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    // Activity variables
    var full_name = ""
    var email = ""
    var password = ""
    var password2 = ""

    // Volley variables
    var requestQueue : RequestQueue? = null

    // Server variables
    var serverIP = Server.instance.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnNext.setOnClickListener {

            full_name = txtFullName.text.toString()
            email = txtEmail.text.toString()
            password = txtPassword.text.toString()
            password2 = txtPassword2.text.toString()

            if(verifyFields()){
                verifyEmailPostRequest(email)
            }

        }

    }

    fun verifyFields() : Boolean{
        if(full_name.length >= 1 && full_name.length <= 70){
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length >= 1 && email.length <= 70){
                if(password.length >= 1 && password.length <= 30){
                    if(password == password2){
                        return true
                    } else{
                        Toast.makeText(this,"Las claves ingresadas no coinciden.",Toast.LENGTH_LONG).show()
                        txtPassword.requestFocus()
                        return false
                    }
                } else{
                    Toast.makeText(this,"La longitud de la clave ingresada es invalida.",Toast.LENGTH_LONG).show()
                    txtPassword.requestFocus()
                    return false
                }
            } else{
                Toast.makeText(this,"El email ingresado es invalido.",Toast.LENGTH_LONG).show()
                txtEmail.requestFocus()
                return false
            }
        } else{
            Toast.makeText(this,"La longitud del nombre completo ingresado es invalido.",Toast.LENGTH_LONG).show()
            txtFullName.requestFocus()
            return false
        }
    }


    fun verifyEmailPostRequest(email:String){
        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "http://$serverIP:8080/api-rest/post/signUp",
            null,
            Response.Listener { response ->

                if(response["response"] == false){
                    val message = response["message"] as? String ?: ""
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
                    txtEmail.requestFocus()
                    imgLoading.visibility = View.GONE
                } else{
                    imgLoading.visibility = View.GONE

                    var signInAdditionalIntent = Intent(this, SignInAdditionalActivity::class.java)

                    signInAdditionalIntent.putExtra("full_name",full_name)
                    signInAdditionalIntent.putExtra("email",email)
                    signInAdditionalIntent.putExtra("password",password)

                    startActivity(signInAdditionalIntent)
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

                params.put("email",email)

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
