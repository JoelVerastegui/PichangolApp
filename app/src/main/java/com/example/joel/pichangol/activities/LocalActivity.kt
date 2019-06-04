package com.example.joel.pichangol.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.joel.pichangol.R
import com.example.joel.pichangol.adapters.ReviewAdapter
import com.example.joel.pichangol.models.Review
import kotlinx.android.synthetic.main.activity_local.*
import java.util.ArrayList

class LocalActivity : AppCompatActivity() {

    var reviewList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)

        rvReview.layoutManager = LinearLayoutManager(this)

        loadReviews()

        rvReview.adapter = ReviewAdapter(reviewList)



        btnReserve.setOnClickListener {
            var scheduleIntent = Intent(this, ScheduleActivity::class.java)
            startActivity(scheduleIntent)
        }
    }

    fun loadReviews(){
        reviewList.add(Review(1,1,1,5,"La gente es buena en el local, ademas dan equipo gratis :D"))
        reviewList.add(Review(2,2,1,4,"El ambiente es impecable, realmente una buena experiencia."))
        reviewList.add(Review(3,3,1,2,"Pos fui a mejores, pero bueno, no estuvo tan mal :P"))
        reviewList.add(Review(4,4,1,1,"No me gusto, pero ustedes que van a saber pe mascotas"))
        reviewList.add(Review(5,5,1,4,"Fue chevere, ese dia fui con mis patas y todos nos matamos de risa"))
    }
}
