package com.example.smackchat.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smackchat.R
import com.example.smackchat.model.Message
import com.example.smackchat.services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context:Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMessage(context,messages[position])

    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val userImage = itemView.findViewById<ImageView>(R.id.messageImage)!!
        private val timeStamp = itemView.findViewById<TextView>(R.id.timeStampLbl)
        private val userName = itemView.findViewById<TextView>(R.id.messageUserNameLb)
        private val messageBody = itemView.findViewById<TextView>(R.id.messageBodyLbl)

        fun bindMessage(context: Context,message: Message){
           val resourceId = context.resources.getIdentifier(message.userAvatar,"drawable",context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
           userName?.text = message.userName
           timeStamp?.text = returnDataString(message.timeStamp)
           messageBody?.text = message.message
        }

        private fun returnDataString(isoString:String): String{
            //2020-05-06T09:20:44.936Z
            //Monday 4.35PM
            val isoFormator = SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormator.timeZone = TimeZone.getTimeZone("UTC")
            var convertDate = Date()
            try{
                convertDate = isoFormator.parse(isoString)
            }catch (e:ParseException){
                Log.e("PARSE","Cannot formate Date")
            }
            val outDataString = SimpleDateFormat("E, hh:mm a", Locale.getDefault())
            return outDataString.format(convertDate)
        }
    }
}