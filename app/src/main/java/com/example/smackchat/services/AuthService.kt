package com.example.smackchat.services

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smackchat.ui.App
import com.example.smackchat.utilities.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

object AuthService {
        /*var isLoggedIn = false
        var userEmail = ""
        var authToken = ""*/
    fun registerUser(context: Context, email:String, password:String, complete:(Boolean)->Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER,Response.Listener {response ->
            Log.e("RESPONSE",response)
            complete(true)
        },Response.ErrorListener {error ->
            Log.e("ERROR","Could not register user: $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(context: Context,email: String,password: String,complete:(Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN,null,Response.Listener{ response ->
            try {
                App.prefs.authToken = response.getString("token")
                App.prefs.userEmail = response.getString("user")
                App.prefs.isLoggedIn = true
                complete(true)
            }catch (e:JSONException){
                Log.e("JSON","EXC:" + e.localizedMessage)
                complete(false)
            }
        },Response.ErrorListener {error->
            Log.e("ERROR","Could not login user: $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.prefs.requestQueue.add(loginRequest)
    }

    fun crateUser(context: Context,name:String,email: String,avatarName:String,avatarColor: String,complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("name",name)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)

        val requestBody = jsonBody.toString()

        var createRequest = object :JsonObjectRequest(Method.POST, URL_CRATE_USER,null,
            Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor=response.getString("avatarColor")
                    UserDataService.email =response.getString("email")
                    UserDataService.id = response.getString("_id")
                    complete(true)
                }catch (e:JSONException){
                    Log.e("JSON","EXC:" + e.localizedMessage)
                    complete(false)
                }
            },
            Response.ErrorListener {error ->
                Log.e("ERROR","Could not create user: $error")
                complete(false)
            }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String> {
                var headers = HashMap<String,String>()
                headers.put("Authorization" ,"Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(createRequest)
    }

    fun findUserByEmail(context:Context,complete: (Boolean) -> Unit){
        val getUserByEmail = object : JsonObjectRequest(Method.GET,"$URL_GET_USER_BY_EMAIL${App.prefs.userEmail}",null,
            Response.Listener { response->
                try {
                    UserDataService.id = response.getString("_id")
                    UserDataService.avatarColor=response.getString("avatarColor")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.email =response.getString("email")
                    UserDataService.name = response.getString("name")

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                    complete(true)
                }catch (e:JSONException){
                    Log.e("JSON","EXC:" + e.localizedMessage)
                    complete(false)
                }
            },
            Response.ErrorListener {error->
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                Log.e("ERROR","Could not get the user: $error")
                complete(false)
            }){
                  override fun getBodyContentType(): String {
                     return "application/json; charset=utf-8"
                  }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization" ,"Bearer ${App.prefs.authToken}")
                return headers
                }
        }
        App.prefs.requestQueue.add(getUserByEmail)
    }
}