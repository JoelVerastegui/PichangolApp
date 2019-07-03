
package com.example.joel.pichangol.activities

import android.app.DatePickerDialog
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
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.json.JSONObject
import java.util.*

class SignInActivity : AppCompatActivity() {

    // Activity variables
    var firstName = ""
    var lastName = ""
    var email = ""
    var password = ""
    var password2 = ""
    var birthday = ""
    var phone = ""
    var map = false

    // Volley variables
    var requestQueue : RequestQueue? = null
    var serverIP = Server.instance.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val extras = intent.extras

        map = extras["map"] as? Boolean ?: false


        lblBack.setOnClickListener {
            this.finish()
        }

        // Select date
        btnDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = 1999 //c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                var day = "0$dayOfMonth"

                var dayText = day.substring(day.length-2)

                var month = "0$monthOfYear"

                var monthText = month.substring(month.length-2)

                birthday = "$year/$monthText/$dayText"

                // Display Selected date in textbox
                txtBirthDate.setText("$dayText/$monthText/$year")
            }, year, month, day)

            dpd.show()
        }


        btnNext.setOnClickListener {

            firstName = txtFirstName.text.toString()
            lastName = txtLastName.text.toString()
            email = txtEmail.text.toString()
            password = txtPassword.text.toString()
            password2 = txtPassword2.text.toString()
            txtBirthDate.setText(birthday)
            phone = txtPhone.text.toString()

            if(verifyFields()){
                verifyEmailPostRequest()
            }

        }

    }

    fun verifyFields() : Boolean{
        if(firstName.length >= 1 && firstName.length <= 70){
            if(lastName.length >= 1 && lastName.length <= 70) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length >= 1 && email.length <= 70) {
                    if (password.length >= 1 && password.length <= 30) {
                        if (password == password2) {
                            if (phone.length >= 1 && phone.length <= 9) {
                                return true
                            } else {
                                Toast.makeText(this, "El numero de telefono ingresado es invalido.", Toast.LENGTH_LONG).show()
                                txtPhone.requestFocus()
                                return false
                            }
                        } else {
                            Toast.makeText(this, "Las claves ingresadas no coinciden.", Toast.LENGTH_LONG).show()
                            txtPassword.requestFocus()
                            return false
                        }
                    } else {
                        Toast.makeText(this, "La longitud de la clave ingresada es invalida.", Toast.LENGTH_LONG).show()
                        txtPassword.requestFocus()
                        return false
                    }
                } else {
                    Toast.makeText(this, "El email ingresado es invalido.", Toast.LENGTH_LONG).show()
                    txtEmail.requestFocus()
                    return false
                }
            } else{
                Toast.makeText(this,"La longitud del apellido ingresado es invalido.",Toast.LENGTH_LONG).show()
                txtLastName.requestFocus()
                return false
            }
        } else{
            Toast.makeText(this,"La longitud del nombre ingresado es invalido.",Toast.LENGTH_LONG).show()
            txtFirstName.requestFocus()
            return false
        }
    }


    fun verifyEmailPostRequest(){
        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "http://$serverIP:8080/api-rest/post/validacionCustomer/",
            null,
            Response.Listener { response ->

                if(response["response"] == true){
                    signInPostRequest()
                } else{
                    imgLoading.visibility = View.GONE

                    Toast.makeText(this,"El email o numero ingresado ya se encuentra registrado.", Toast.LENGTH_LONG).show()
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
                params.put("phone",phone)

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }

    fun signInPostRequest(){
        requestQueue = Volley.newRequestQueue(this)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "http://$serverIP:8080/api-rest/post/signUp/",
            null,
            Response.Listener { response ->

                if(response["response"] == true){

                    val customer = response.getJSONObject("Customer")

                    val id = customer["id"] as? Int ?: 0
                    val account_id = customer.getJSONObject("account")["id"] as? Int ?: 0
                    val firstName = customer["firstName"] as? String ?: ""
                    val lastName = customer["lastName"] as? String ?: ""
                    val birthday = customer["birthday"] as? String ?: ""
                    val phone = customer["phone"] as? String ?: ""
                    val email = customer["email"] as? String ?: ""

                    val profile = Profile(id,account_id,firstName,lastName,birthday,phone,email)

                    Server.instance.profile = profile

                    Toast.makeText(this,"Usuario registrado correctamente. Bienvenido.", Toast.LENGTH_LONG).show()

                    if(map){
                        val principalActivity = Intent(this,PrincipalActivity::class.java)
                        startActivity(principalActivity)
                    } else{
                        var purchaseActivity = Intent(this,PurchaseActivity::class.java)
                        startActivity(purchaseActivity)
                    }





                } else{
                    imgLoading.visibility = View.GONE

                    Toast.makeText(this,"${response["message"]}", Toast.LENGTH_LONG).show()
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

                params.put("firstName",firstName)
                params.put("lastName",lastName)
                params.put("birthday",birthday)
                params.put("phone",phone)
                params.put("email",email)
                params.put("pass",password)

                return JSONObject(params).toString().toByteArray()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)
    }

}
