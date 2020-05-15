package com.example.smackchat.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smackchat.R
import com.example.smackchat.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginCreateUserClick(view: View){
        if(view.id == R.id.btnLoginCreateUser) {
            val createUserIntent = Intent(
                this,
                CreateUserActivity::class.java
            )
            startActivity(createUserIntent)
            finish()
        }
    }
    fun loginLoginBtnClick(view:View){
        if(view.id == R.id.BtnLoginLogin) {
            enableSpinner(true)
            val email = loginEmailText.text.toString()
            val password = loginPasswordText.text.toString()
            hideKeyboard()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                AuthService.loginUser(email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.findUserByEmail(this) { findSuccess ->
                            if (findSuccess) {
                                enableSpinner(false)
                                finish()
                            } else {
                                errorToast()
                            }
                        }
                    } else {
                        errorToast()
                    }
                }
            } else {
                loginSpinner.visibility = View.INVISIBLE
                Toast.makeText(
                    this,
                    "Make sure user email, password are filled in",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun enableSpinner(enable:Boolean){
        if(enable) {
            loginSpinner.visibility = View.VISIBLE
        }else{
            loginSpinner.visibility = View.INVISIBLE
        }
        BtnLoginLogin.isEnabled = !enable
        btnLoginCreateUser.isEnabled = !enable
    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
           inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}
