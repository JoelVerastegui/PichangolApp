package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.models.Review
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    val profileList = Server.instance.profileList

    fun loadReview(review : Review) {
        val profile = profileList.find { p -> p.account_id == review.account_id }

        itemView.lblUserName.text = "${profile?.full_name}"
        itemView.lblCommentary.text = review.commentary

        when(review.account_id) {
            1 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_1)
            2 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_2)
            3 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_3)
            4 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_4)
            5 -> itemView.lblUserImg.setBackgroundResource(R.drawable.user_5)
        }
    }
}