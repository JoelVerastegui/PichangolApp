package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.models.Review
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewViewHolder(view : View) : RecyclerView.ViewHolder (view) {

    fun loadReview(review : Review) {

        itemView.lblUserName.text = review.userName
        itemView.lblCommentary.text = review.commentary

        when(review.account_id) {
            1 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_1)
            2 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_2)
            3 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_3)
            4 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_4)
            5 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_5)
            6 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_6)
            7 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_7)
            8 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_8)
            9 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_9)
            10 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_10)
        }

        val stars = review.stars.toInt()

        if(stars >= 1){
            itemView.userStar1.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 2){
            itemView.userStar2.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 3){
            itemView.userStar3.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 4){
            itemView.userStar4.setBackgroundResource(R.drawable.star_b)
        }
        if(stars >= 5){
            itemView.userStar5.setBackgroundResource(R.drawable.star_b)
        }
    }
}