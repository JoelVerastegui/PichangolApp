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
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.volley.RequestQueue
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Init Activity Variables
    var isConfiguration = false
    var store : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init sharedPreferences
        store = this.getSharedPreferences("store", Context.MODE_PRIVATE)

        if(store?.getString("serverIP","") != ""){

            val serverIP = store?.getString("serverIP","") as String

            val ip = serverIP.split(".")

            lblIP1.setText(ip[0])
            lblIP2.setText(ip[1])
            lblIP3.setText(ip[2])
            lblIP4.setText(ip[3])

        }


        lblConfiguration.setOnClickListener {

            if (isConfiguration) {

                // lblConfiguration rotation animation
                ObjectAnimator.ofFloat(lblConfiguration,"rotation", 0f).apply {
                    duration = 200
                    start()
                }

                toggleConfiguration(-80f)

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

            if(email == "beleza" && password == "beleza"){
                val miIntent = Intent(this, PrincipalActivity::class.java)
                startActivity(miIntent)
            } else{
                Toast.makeText(this,"Credenciales incorrectas",Toast.LENGTH_LONG).show()
            }
        }

        lblSignIn.setOnClickListener {
            saveServerIP()

            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
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
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
