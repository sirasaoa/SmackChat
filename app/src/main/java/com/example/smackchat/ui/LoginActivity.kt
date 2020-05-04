package com.example.smackchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smackchat.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginCreateUserClick(view: View){
        val createUserIntent = Intent(this,
            CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }
    fun loginLoginBtnClick(view:View){

    }
}
