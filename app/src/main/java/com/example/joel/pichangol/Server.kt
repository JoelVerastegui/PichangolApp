package com.example.joel.pichangol

import com.example.joel.pichangol.models.*
import java.util.*
import java.util.Date

class Server {
    var ip = ""
    var profile : Profile? = null
    var tempProfile : Profile? = null
    var screenWidth = 0
    var screenHeight = 0
    var selectedDate = Date()
    var soccerField : SoccerField? = null
    var review : Review? = null


    var localS : LocalS? = null

    var profileList = ArrayList<Profile>()
    var selectedHours = ArrayList<Hour>()

    companion object {
        public val instance = Server()
    }
}