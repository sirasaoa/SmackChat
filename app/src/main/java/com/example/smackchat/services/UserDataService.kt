package com.example.smackchat.services

import android.graphics.Color
import com.example.smackchat.ui.App
import java.util.*

object UserDataService {
    var id =""
    var avatarColor = ""
    var avatarName = ""
    var email =""
    var name = ""

    fun logout(){
        id =""
        avatarColor = ""
        avatarName = ""
        email =""
        name = ""
        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearMessage()
        MessageService.clearChannels()
    }
    fun returnAvatarColor(components:String):Int{
        //[1.3456789345, 1.548484784, 2.8848383000, 1]
        //1.3456789345 1.548484784 2.8848383000 1
        val strippedColor = components
                             .replace("[","")
                             .replace("]","")
                             .replace(",","")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if(scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

         return Color.rgb(r,g,b)
    }
}