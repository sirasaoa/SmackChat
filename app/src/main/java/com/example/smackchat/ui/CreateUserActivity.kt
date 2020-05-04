package com.example.smackchat.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smackchat.R
import com.example.smackchat.services.AuthService
import com.example.smackchat.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {
    var userAvatar = "profileDefault"
    var avatarColor: String = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatarClick(view:View){
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(color==0){
            userAvatar = "light$avatar"
        }else{
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar,"drawable",packageName)
        createAvatarImageView.setImageResource(resourceId)
    }
    fun generateBackgroundColorClick(view:View){
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        val saveR = r.toDouble()/255
        val saveG = g.toDouble()/255
        val saveB = b.toDouble()/255

        avatarColor = "[$saveR, $saveG, $saveB, 1]"
    }
    fun createNewUserClick(view: View){
        enableSpinner(true)
        val userName= createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password: String = createPasswordText.text.toString()
        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(this,email,password){ registerSuccess->
                if(registerSuccess){
                    AuthService.loginUser(this,email,password){loginSuccess->
                        if(loginSuccess){
                            AuthService.crateUser(this,userName,email,userAvatar,avatarColor){crateSuccess->
                                if(crateSuccess){
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                }else{
                                    errorToast()
                                }
                            }
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
        }
       }else{
            Toast.makeText(this,"Make sure user name, email, password are filled in",Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    private fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again",Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable:Boolean){
        if(enable) {
            createSpinner.visibility = View.VISIBLE
        }else{
            createSpinner.visibility = View.INVISIBLE
        }
        btnCreateUser.isEnabled = !enable
        btnBackgoundColor.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
    }
}
