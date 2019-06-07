package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.joel.pichangol.R
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnNext.setOnClickListener {
            var signInAdditionalIntent = Intent(this, SignInAdditionalActivity::class.java)
            startActivity(signInAdditionalIntent)
        }

    }
}
