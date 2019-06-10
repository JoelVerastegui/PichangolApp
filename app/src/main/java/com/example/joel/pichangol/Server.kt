package com.example.joel.pichangol

import com.example.joel.pichangol.models.Profile

class Server {
    var ip = ""
    var profile : Profile? = null
    var tempProfile : Profile? = null

    var profileList = ArrayList<Profile>()

    companion object {
        public val instance = Server()
    }
}