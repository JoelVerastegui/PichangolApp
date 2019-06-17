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
import kotlinx.android.synthetic.main.activity_sign_in_additional.*
import org.json.JSONObject

class SignInAdditionalActivity : AppCompatActivity() {

    // Activity variables
    var full_name = ""
    var email = ""
    var password = ""
    var phone1 = ""
    var phone2 = ""
    var phone3 = ""
    var dni = ""
    var skip = false

    // Volley variables
    var requestQueue : RequestQueue? = null

    // Server variables
    var serverIP = Server.instance.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_additional)

        val extras = intent.extras

        full_name = extras.get("full_name") as? String ?: ""
        email = extras.get("email") as? String ?: ""
        password = extras.get("password") as? String ?: ""

        btnNext.setOnClickListener {
            skip = false

            phone1 = txtPhone.text.toString()
            phone2 = txtPhone2.text.toString()
            phone3 = txtPhone3.text.toString()
            dni = txtDni.text.toString()

            if(verifyFields()){
                signInPostRequest()
            }
        }

        btnSkip.setOnClickListener {
            skip = true
            signInPostRequest()
        }

    }

    fun verifyFields() : Boolean{
        if(phone1.length >= 7 && phone1.length <= 9){
            if(phone2.length >= 7 && phone2.length <= 9){
                if(phone3.length >= 7 && phone3.length <= 9){
                    if(dni.length == 8){
                        return true
                    } else{
                        Toast.makeText(this,"El dni ingresado es incorrecto.",Toast.LENGTH_LONG).show()
                        txtDni.requestFocus()
                        return false
                    }
                } else{
                    Toast.makeText(this,"El numero de telefono 3 ingresado es incorrecto.",Toast.LENGTH_LONG).show()
                    txtPhone3.requestFocus()
                    return false
                }
            } else{
                Toast.makeText(this,"El numero de telefono 2 ingresado es incorrecto.",Toast.LENGTH_LONG).show()
                txtPhone2.requestFocus()
                return false
            }
        } else{
            Toast.makeText(this,"El numero de telefono 1 ingresado es incorrecto.",Toast.LENGTH_LONG).show()
            txtPhone.requestFocus()
            return false
        }
    }

    fun signInPostRequest(){
        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "http://$serverIP:8080/api-rest/post/signUp",
            null,
            Response.Listener { response ->

                if(response["response"] == true){
                    val profileObj = response.getJSONObject("profile")

                    val id = profileObj["id"] as? Int ?: 0
                    val account_id = profileObj.getJSONObject("account")["id"] as? Int ?: 0
                    val full_name = profileObj["fullName"] as? String ?: ""
                    val phone1 = profileObj["phone1"] as? String ?: ""
                    val phone2 = profileObj["phone2"] as? String ?: ""
                    val phone3 = profileObj["phone3"] as? String ?: ""
                    val dni = profileObj["dni"] as? String ?: ""
                    val status = profileObj["status"] as? Int ?: 0

                    Server.instance.profile = Profile(id,account_id,full_name,phone1,phone2,phone3,dni,status)

                    imgLoading.visibility = View.GONE

                    Toast.makeText(this,"Usuario registrado correctamente.",Toast.LENGTH_LONG).show()

                    val principalActivity = Intent(this, PrincipalActivity::class.java)
                    startActivity(principalActivity)

                } else{
                    imgLoading.visibility = View.GONE
                    Toast.makeText(this,"Error al registrar el usuario.",Toast.LENGTH_LONG).show()
                }

            },
            Response.ErrorListener {
                imgLoading.visibility = View.GONE
                Toast.makeText(this,"Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String,String>()

                params.put("fullName",full_name)
                params.put("email",email)
                params.put("password",password)

                if(!skip){
                    params.put("phone1",phone1)
                    params.put("phone2",phone2)
                    params.put("phone3",phone3)
                    params.put("dni",dni)
                }

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }
}
