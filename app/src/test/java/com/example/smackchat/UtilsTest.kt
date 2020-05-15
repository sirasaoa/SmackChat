package com.example.smackchat

import com.example.smackchat.ui.CreateUserActivity
import org.junit.Assert
import org.junit.Test

class UtilsTest {
    @Test
    fun checkIsEmailValid(){
        val email:String = "ishwarisorthe@gmail.com"
        Assert.assertEquals(true,CreateUserActivity.Utils.isValidEmail(email))
    }

    @Test
    fun checkIsEmailInvalid(){
        val email:String = "ishwarisorthegmail.com"
        Assert.assertEquals(false,CreateUserActivity.Utils.isValidEmail(email))
    }

    @Test
    fun checkUpperCaseEmail(){
        val email:String = "Ishwarisorthe@gmail.com"
        Assert.assertEquals(true,CreateUserActivity.Utils.isValidEmail(email))
    }

    @Test
    fun checkIsPasswordValid(){
        val password:String = "Qwerty@123"
        Assert.assertEquals(true,CreateUserActivity.Utils.isValidPassword(password))
    }

    @Test
    fun checkIsPasswordInvalid(){
        val password:String = "qwerty@123"
        Assert.assertEquals(false,CreateUserActivity.Utils.isValidPassword(password))
    }

    @Test
    fun checkIsPasswordContainUpperCase(){
        val password:String = "qWerty@123"
        Assert.assertTrue(CreateUserActivity.Utils.isValidPassword(password))
    }

    @Test
    fun checkIsPasswordContainSpecialChar(){
        val password:String = "qWerty@123"
        Assert.assertTrue(CreateUserActivity.Utils.isValidPassword(password))
    }
    @Test
    fun checkIsPasswordNotContainSpecialChar(){
        val password:String = "qWerty123"
        Assert.assertEquals(false,CreateUserActivity.Utils.isValidPassword(password))
    }
}