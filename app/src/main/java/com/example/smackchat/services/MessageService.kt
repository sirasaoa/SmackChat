package com.example.smackchat.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smackchat.model.Channel
import com.example.smackchat.utilities.URL_CHANNELS
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context,complete : (Boolean)->Unit){
      val channelRequest = object: JsonArrayRequest(Method.GET, URL_CHANNELS,null,Response.Listener {response ->
           try{
              for(x in 0 until response.length()){
                  val channel = response.getJSONObject(x)
                  val name = channel.getString("name")
                  val description = channel.getString("description")
                  val id = channel.getString("_id")

                  val newChannel = Channel(name,description,id)
                  this.channels.add(newChannel)
              }
               complete(true)
           }catch (e:JSONException){
               Log.e("JSON","EXC:" + e.localizedMessage)
               complete(false)
           }
      },Response.ErrorListener {error->
          Log.e("ERROR","Could not retrieve channels: $error")
          complete(false)
      }){
          override fun getBodyContentType(): String {
              return "application/json; charset=utf-8"
          }

          override fun getHeaders(): MutableMap<String, String> {
              val headers = HashMap<String,String>()
              headers.put("Authorization","Bearer ${AuthService.authToken}")
              return headers
          }
      }
        Volley.newRequestQueue(context).add(channelRequest)
    }
}