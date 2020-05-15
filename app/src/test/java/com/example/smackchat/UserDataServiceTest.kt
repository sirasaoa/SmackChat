package com.example.smackchat

import android.graphics.Color
import com.example.smackchat.services.UserDataService
import org.junit.Assert
import org.junit.Test

class UserDataServiceTest {
    @Test
    fun checkAvatarColour():Unit{
        val color = UserDataService.returnAvatarColor("[0.45098039215686275, 0.39215686274509803, 0.5254901960784314, 1]")
        Assert.assertEquals(color,0)
    }

}