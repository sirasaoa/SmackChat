package com.example.smackchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smackchat.R
import com.example.smackchat.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

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
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        AuthService.loginUser(this,email,password){ loginSuccess->
            if(loginSuccess){
                AuthService.findUserByEmail(this){ findSuccess->
                    if(findSuccess){
                      finish()
                    }
            }
        }
        }
    }

}
