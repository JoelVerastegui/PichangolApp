package com.example.joel.pichangol.models

class LocalS(var id : Int,
             var nombre : String,
             var description : String,
             var address : String,
             var latitude : Double,
             var longitude : Double,
             var workDays : List<WorkDays>,
             var nonDays : List<NonDays>,
             var soccerFields : List<SoccerField>)