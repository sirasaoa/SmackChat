package com.example.smackchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.smackchat.R
import com.example.smackchat.model.Channel

class ChannelAdapter(context : Context, channels :MutableList<Channel> ): BaseAdapter(){
    private val context = context
    private var channels = channels

    override fun getView(position: Int, convertview: View?, parent: ViewGroup?): View {
        val channelView :View
        val holder : ViewHolder
        if(convertview == null){
            channelView = LayoutInflater.from(context).inflate(R.layout.channel_list, null)
            holder = ViewHolder()
            holder.channelName= channelView.findViewById(R.id.channelNameText)

            channelView.tag = holder
        }else{
            holder = convertview.tag as ViewHolder
            channelView = convertview
        }
        holder.channelName?.text = "# " + channels[position].name
        return channelView
    }

    override fun getItem(position: Int): Any {
        return channels[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return channels.count()
    }

    fun updateItemList(channelList:ArrayList<Channel>){
       if(channelList.isNotEmpty()){
           this.channels.clear()
           this.channels.addAll(channelList)
       }
        notifyDataSetChanged()
    }

    private class ViewHolder{
        var channelName: TextView? = null

    }
}